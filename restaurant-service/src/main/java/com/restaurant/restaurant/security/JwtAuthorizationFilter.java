package com.restaurant.restaurant.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT authorization filter for processing incoming HTTP requests.
 * This filter:
 * - Extracts JWT tokens from request headers
 * - Validates tokens using JwtTokenProvider
 * - Sets authentication in SecurityContext if valid
 * 
 * Processes every incoming request exactly once to ensure
 * proper authentication and authorization.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    /** Provider for JWT token operations */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs filter with required JWT token provider.
     *
     * @param jwtTokenProvider The provider for JWT operations
     */
    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Processes each HTTP request for JWT authentication.
     * Extracts JWT token from request header, validates it,
     * and sets up security context if token is valid.
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param chain    The filter chain
     * @throws ServletException If a servlet error occurs
     * @throws IOException      If an I/O error occurs
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
     * Extracts JWT token from HTTP request header.
     * Looks for the token in the Authorization header with the Bearer scheme.
     * For example: "Authorization: Bearer eyJhbGciOiJIUzI1..."
     *
     * @param request The HTTP request to extract the token from
     * @return The JWT token string without the "Bearer " prefix, or null if not
     *         found
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}