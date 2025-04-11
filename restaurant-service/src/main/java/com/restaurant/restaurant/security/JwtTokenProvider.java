package com.restaurant.restaurant.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
 * Provider class for JWT (JSON Web Token) operations.
 * This component handles:
 * - JWT token validation
 * - Authentication extraction from tokens
 * - Token signing key management
 * - Role/authority parsing from claims
 *
 * Uses HMAC-SHA for token signing and validates tokens against
 * the configured secret and expiration settings.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class JwtTokenProvider {

    /** Secret key used for signing JWT tokens */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /** Token expiration time in milliseconds */
    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    /**
     * Creates the signing key used for JWT token validation.
     * Uses HMAC-SHA algorithm with the configured secret key.
     *
     * @return The Key object used for signing tokens
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts authentication information from a JWT token.
     * Parses the token claims and creates an Authentication object with:
     * - User ID from the subject claim
     * - Roles from the roles claim, converted to Spring authorities
     *
     * @param token The JWT token string to parse
     * @return Authentication object containing user details and authorities
     * @throws JwtException if the token is invalid or expired
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Extract userId from the dedicated claim, fallback to subject if not present
        String userId = claims.get("userId", String.class);
        if (userId == null) {
            userId = claims.getSubject();
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        String roles = claims.get("roles", String.class);
        if (roles != null && !roles.isEmpty()) {
            authorities = Arrays.stream(roles.split(","))
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                    .collect(Collectors.toList());
        }

        return new UsernamePasswordAuthenticationToken(userId, "", authorities);
    }

    /**
     * Validates a JWT token for authenticity and expiration.
     * Attempts to parse the token using the signing key and checks for:
     * - Valid token format
     * - Valid signature
     * - Not expired
     * - Not malformed
     *
     * @param token The JWT token string to validate
     * @return true if the token is valid and not expired, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}