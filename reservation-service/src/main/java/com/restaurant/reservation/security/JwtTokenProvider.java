package com.restaurant.reservation.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT (JSON Web Token) provider for handling token operations in the reservation service.
 * This class is responsible for:
 * - Generating signing keys for JWT operations
 * - Extracting user information from tokens
 * - Validating token authenticity
 * - Creating Spring Security Authentication objects from tokens
 *
 * The provider uses HMAC-SHA algorithm for token signing and validation.
 * It handles token parsing, validation, and authentication creation in a secure manner.
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
@Component
public class JwtTokenProvider {

    /** Logger for this provider */
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    /** Secret key used for JWT signing and validation */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /** Token expiration time in milliseconds */
    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    /** Authorization header name */
    @Value("${jwt.header:Authorization}")
    private String tokenHeader;

    /** Token prefix in Authorization header */
    @Value("${jwt.prefix:Bearer }")
    private String tokenPrefix;

    /**
     * Generates a signing key from the JWT secret.
     * The key is used for both signing and validating JWT tokens.
     *
     * @return A Key object suitable for JWT operations
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the user ID from a JWT token.
     * The user ID is stored in the token's subject claim.
     *
     * @param token The JWT token to extract the user ID from
     * @return The user ID as a String, or null if the token is invalid
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validates a JWT token's authenticity and integrity.
     * This method checks if the token is properly signed and not expired.
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        if (token == null) {
            logger.debug("Token is null");
            return false;
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Check if token is expired
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                logger.debug("Token is expired");
                return false;
            }

            return true;
        } catch (JwtException e) {
            logger.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.debug("JWT token compact of handler are invalid: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Creates a Spring Security Authentication object from a JWT token.
     * This method extracts the user ID and roles from the token and creates
     * an Authentication object that can be used by Spring Security.
     *
     * @param token The JWT token to create the Authentication from
     * @return An Authentication object containing the user's ID and authorities, or null if the token is invalid
     */
    public Authentication getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            if (userId == null) {
                logger.warn("JWT token does not contain a subject");
                return null;
            }

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            String roles = claims.get("roles", String.class);
            if (roles != null && !roles.isEmpty()) {
                authorities = Arrays.stream(roles.split(","))
                        .filter(role -> role != null && !role.trim().isEmpty())
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                        .collect(Collectors.toList());
            }

            logger.debug("Created authentication for user: {} with {} authorities", userId, authorities.size());
            return new UsernamePasswordAuthenticationToken(userId, "", authorities);
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the JWT token from an HTTP Authorization header value.
     * The token should be in the format: "Bearer <token>"
     *
     * @param authorizationHeader The Authorization header value
     * @return The JWT token if present and properly formatted, null otherwise
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(tokenPrefix)) {
            return authorizationHeader.substring(tokenPrefix.length());
        }
        return null;
    }
}