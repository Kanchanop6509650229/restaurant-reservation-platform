package com.restaurant.reservation.config;

import com.restaurant.reservation.security.JwtAuthorizationFilter;
import com.restaurant.reservation.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the reservation service.
 * Configures JWT-based authentication and authorization.
 * Defines security rules for API endpoints and enables method-level security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * JWT token provider for token validation and user authentication.
     */
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
        http
            // Disable CSRF protection for API endpoints
            .csrf().disable()
            // Configure stateless session management
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(HttpMethod.GET, "/api/reservations/public/**").permitAll()
                // H2 console access (development only)
                .requestMatchers("/h2-console/**").permitAll()
                // Health check endpoint
                .requestMatchers("/api/health").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            // Add JWT authorization filter
            .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        
        // Enable h2-console frame options
        http.headers().frameOptions().disable();
        
        return http.build();
    }
}