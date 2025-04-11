package com.restaurant.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application class for the Restaurant Service.
 * This class serves as the entry point for the Spring Boot application
 * and configures the base package scanning for components.
 * 
 * The application is configured to scan both the restaurant service
 * and common packages for Spring components.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.restaurant.restaurant", "com.restaurant.common"})
public class RestaurantServiceApplication {

    /**
     * Main method that starts the Spring Boot application.
     * This method initializes the Spring application context
     * and starts the embedded web server.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}