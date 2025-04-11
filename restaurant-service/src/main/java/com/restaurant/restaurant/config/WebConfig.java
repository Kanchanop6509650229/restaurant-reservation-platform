package com.restaurant.restaurant.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.restaurant.restaurant.security.CurrentUserArgumentResolver;

/**
 * Web configuration for the restaurant service.
 * This configuration provides:
 * - Custom argument resolvers for controller methods
 * - Integration with Spring MVC
 * - Support for current user injection in controllers
 * 
 * The configuration extends Spring's WebMvcConfigurer to customize
 * the web application context and add custom argument resolvers.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** Resolver for injecting current user information into controller methods */
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    /**
     * Constructs a new WebConfig with required dependencies.
     *
     * @param currentUserArgumentResolver Resolver for current user information
     */
    public WebConfig(CurrentUserArgumentResolver currentUserArgumentResolver) {
        this.currentUserArgumentResolver = currentUserArgumentResolver;
    }

    /**
     * Adds custom argument resolvers to the application.
     * This method registers the CurrentUserArgumentResolver to enable
     * automatic injection of current user information in controller methods
     * using the @CurrentUser annotation.
     *
     * @param resolvers List of argument resolvers to be configured
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}