package com.restaurant.reservation.config;

import com.restaurant.reservation.security.CurrentUserArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web configuration class for the reservation service.
 * Configures Spring MVC components and custom argument resolvers.
 * Provides REST client configuration for external service calls.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Resolver for injecting the current authenticated user into controller methods.
     */
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    /**
     * Constructs a new WebConfig with the specified argument resolver.
     *
     * @param currentUserArgumentResolver The resolver for current user injection
     */
    public WebConfig(CurrentUserArgumentResolver currentUserArgumentResolver) {
        this.currentUserArgumentResolver = currentUserArgumentResolver;
    }

    /**
     * Adds custom argument resolvers to Spring MVC.
     * Registers the CurrentUserArgumentResolver to enable @CurrentUser annotation.
     *
     * @param resolvers List of argument resolvers to configure
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }

    /**
     * Creates a RestTemplate bean for making HTTP requests to external services.
     * Used for inter-service communication within the platform.
     *
     * @return Configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}