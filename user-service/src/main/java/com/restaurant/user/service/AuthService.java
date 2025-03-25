package com.restaurant.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.restaurant.common.events.user.UserLoggedInEvent;
import com.restaurant.common.exceptions.AuthenticationException;
import com.restaurant.user.dto.LoginRequest;
import com.restaurant.user.dto.LoginResponse;
import com.restaurant.user.kafka.producers.UserEventProducer;
import com.restaurant.user.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserEventProducer userEventProducer;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       CustomUserDetailsService userDetailsService,
                       UserEventProducer userEventProducer) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.userEventProducer = userEventProducer;
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        try {
            // First, try to load the user to check if it exists and check account status
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            logger.info("User found: {}", userDetails.getUsername());
            logger.info("Account status - enabled: {}, accountNonExpired: {}, accountNonLocked: {}, credentialsNonExpired: {}",
                    userDetails.isEnabled(), userDetails.isAccountNonExpired(), 
                    userDetails.isAccountNonLocked(), userDetails.isCredentialsNonExpired());
            
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(),
                                loginRequest.getPassword()
                        )
                );
                
                userDetails = (UserDetails) authentication.getPrincipal();
                String token = jwtTokenProvider.generateToken(userDetails);
                
                // Publish login event
                userEventProducer.publishUserLoggedInEvent(new UserLoggedInEvent(
                        userDetailsService.getUserIdByUsername(userDetails.getUsername()),
                        userDetails.getUsername(),
                        getClientIP(request)
                ));
                
                return new LoginResponse(token, "Authentication successful");
            } catch (BadCredentialsException e) {
                logger.error("Bad credentials for user: {}", loginRequest.getUsername());
                throw AuthenticationException.invalidCredentials();
            } catch (DisabledException e) {
                logger.error("Account is disabled for user: {}", loginRequest.getUsername());
                throw AuthenticationException.accountDisabled();
            } catch (LockedException e) {
                logger.error("Account is locked for user: {}", loginRequest.getUsername());
                throw AuthenticationException.accountLocked();
            } catch (Exception e) {
                logger.error("Authentication failed with exception: {}", e.getMessage(), e);
                throw AuthenticationException.invalidCredentials();
            }
        } catch (UsernameNotFoundException e) {
            logger.error("User not found: {}", loginRequest.getUsername());
            throw AuthenticationException.invalidCredentials();
        }
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}