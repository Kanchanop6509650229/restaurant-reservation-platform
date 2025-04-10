package com.restaurant.reservation.security;

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
 * JWT (JSON Web Token) provider for handling token operations in the reservation service.
 * This class is responsible for:
 * - Generating signing keys for JWT operations
 * - Extracting user information from tokens
 * - Validating token authenticity
 * - Creating Spring Security Authentication objects from tokens
 * 
 * The provider uses HMAC-SHA algorithm for token signing and validation.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class JwtTokenProvider {

    /** Secret key used for JWT signing and validation */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /** Token expiration time in milliseconds */
    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

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
     * @return The user ID as a String
     */
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validates a JWT token's authenticity and integrity.
     * This method checks if the token is properly signed and not expired.
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Creates a Spring Security Authentication object from a JWT token.
     * This method extracts the user ID and roles from the token and creates
     * an Authentication object that can be used by Spring Security.
     *
     * @param token The JWT token to create the Authentication from
     * @return An Authentication object containing the user's ID and authorities
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String userId = claims.getSubject();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        String roles = claims.get("roles", String.class);
        if (roles != null && !roles.isEmpty()) {
            authorities = Arrays.stream(roles.split(","))
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                    .collect(Collectors.toList());
        }

        return new UsernamePasswordAuthenticationToken(userId, "", authorities);
    }
}