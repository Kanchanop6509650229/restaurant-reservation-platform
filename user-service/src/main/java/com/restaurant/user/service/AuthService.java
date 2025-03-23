package com.restaurant.user.service;

import com.restaurant.common.events.user.UserLoggedInEvent;
import com.restaurant.common.exceptions.AuthenticationException;
import com.restaurant.user.dto.LoginRequest;
import com.restaurant.user.dto.LoginResponse;
import com.restaurant.user.kafka.producers.UserEventProducer;
import com.restaurant.user.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

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
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(userDetails);
            
            // Publish login event
            userEventProducer.publishUserLoggedInEvent(new UserLoggedInEvent(
                    userDetailsService.getUserIdByUsername(userDetails.getUsername()),
                    userDetails.getUsername(),
                    getClientIP(request)
            ));
            
            return new LoginResponse(token, "Authentication successful");
        } catch (Exception e) {
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