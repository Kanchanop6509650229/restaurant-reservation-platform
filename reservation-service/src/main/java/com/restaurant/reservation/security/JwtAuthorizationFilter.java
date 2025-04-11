package com.restaurant.reservation.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

    /** Logger for this filter */
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

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

        try {
            String token = getJwtFromRequest(request);

            if (token != null) {
                logger.debug("JWT token found in request");

                if (jwtTokenProvider.validateToken(token)) {
                    logger.debug("JWT token is valid");
                    Authentication auth = jwtTokenProvider.getAuthentication(token);

                    if (auth != null) {
                        logger.debug("Setting authentication in security context for user: {}", auth.getName());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        logger.debug("Could not create authentication from token");
                    }
                } else {
                    logger.debug("JWT token is invalid");
                }
            }
        } catch (JwtException e) {
            logger.error("JWT token processing error: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            logger.error("Unexpected error during JWT processing: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header of the request.
     * The token should be in the format: "Bearer <token>"
     * Delegates to the JwtTokenProvider for token extraction.
     *
     * @param request the HTTP request containing the Authorization header
     * @return the JWT token if present and properly formatted, null otherwise
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return jwtTokenProvider.extractTokenFromHeader(bearerToken);
    }
}