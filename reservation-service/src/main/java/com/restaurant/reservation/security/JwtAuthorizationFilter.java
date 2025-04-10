package com.restaurant.reservation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Spring Security filter that processes JWT tokens in incoming HTTP requests.
 * This filter is responsible for:
 * - Extracting JWT tokens from the Authorization header
 * - Validating the token's authenticity
 * - Creating and setting the Spring Security Authentication object
 * 
 * The filter extends OncePerRequestFilter to ensure it's only executed once per request.
 * It works in conjunction with JwtTokenProvider to handle token validation and authentication.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    /** Provider for JWT token operations */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs a new JwtAuthorizationFilter with the specified JwtTokenProvider.
     *
     * @param jwtTokenProvider the provider to use for JWT operations
     */
    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Processes each HTTP request to handle JWT authentication.
     * The method:
     * 1. Extracts the JWT token from the request
     * 2. Validates the token if present
     * 3. Creates an Authentication object if the token is valid
     * 4. Sets the Authentication in the SecurityContext
     * 5. Continues the filter chain
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param chain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        String token = getJwtFromRequest(request);
        
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        chain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header of the request.
     * The token should be in the format: "Bearer <token>"
     *
     * @param request the HTTP request containing the Authorization header
     * @return the JWT token if present and properly formatted, null otherwise
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}