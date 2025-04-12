package com.restaurant.reservation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.restaurant.reservation.security.JwtAuthorizationFilter;
import com.restaurant.reservation.security.JwtTokenProvider;

/**
 * Security configuration for the reservation service.
 * Configures JWT-based authentication and authorization.
 * Defines security rules for API endpoints and enables method-level security.
 *
 * This configuration:
 * - Disables CSRF for REST API endpoints
 * - Configures stateless session management
 * - Sets up public and protected endpoints
 * - Adds JWT authentication filter
 * - Configures H2 console access for development
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** Logger for this configuration */
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /** JWT token provider for token validation and user authentication */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs a new SecurityConfig with the specified JwtTokenProvider.
     *
     * @param jwtTokenProvider The provider responsible for JWT token operations
     */
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Configures the security filter chain for the application.
     * Sets up:
     * - CSRF protection (disabled for API)
     * - Stateless session management
     * - Public endpoints access
     * - JWT authentication filter
     * - H2 console access (for development)
     *
     * @param http HttpSecurity object to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain");

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(HttpMethod.GET, "/api/reservations/public/**").permitAll()
                // Schedule endpoints
                .requestMatchers(HttpMethod.GET, "/api/schedules/restaurant/**").permitAll()
                // Restaurant search endpoint
                .requestMatchers(HttpMethod.POST, "/api/restaurants/search").permitAll()
                // H2 console access (development only)
                .requestMatchers("/h2-console/**").permitAll()
                // Health check endpoint
                .requestMatchers("/api/health", "/api/health/details").permitAll()
                // Swagger/OpenAPI endpoints
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            // Add JWT authorization filter
            .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            // Enable h2-console frame options
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        logger.info("Security filter chain configured successfully");
        return http.build();
    }
}