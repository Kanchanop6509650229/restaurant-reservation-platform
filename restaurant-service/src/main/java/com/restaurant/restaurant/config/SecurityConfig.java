package com.restaurant.restaurant.config;

import com.restaurant.restaurant.security.JwtAuthorizationFilter;
import com.restaurant.restaurant.security.JwtTokenProvider;
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
 * Security configuration for the restaurant service.
 * This configuration provides:
 * - JWT-based authentication
 * - Role-based authorization
 * - Public and protected endpoint configuration
 * - Stateless session management
 * 
 * The configuration uses Spring Security to secure the application
 * and implements JWT token validation for authentication.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    /** Provider for JWT token operations */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs a new SecurityConfig with required dependencies.
     *
     * @param jwtTokenProvider Provider for JWT token operations
     */
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Creates and configures the JWT authorization filter.
     * This filter intercepts incoming requests and validates JWT tokens.
     *
     * @return Configured JwtAuthorizationFilter instance
     */
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtTokenProvider);
    }

    /**
     * Configures the security filter chain for the application.
     * This method:
     * - Disables CSRF protection (not needed for stateless API)
     * - Configures stateless session management
     * - Defines public and protected endpoints
     * - Sets up JWT token validation
     * - Enables H2 console access in development
     *
     * @param http HttpSecurity instance to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/restaurants/public/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/restaurants/*/tables/public/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/restaurants/*/operating-hours/public/**").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        // Enable h2-console for development
        http.headers().frameOptions().disable();
        
        return http.build();
    }
}