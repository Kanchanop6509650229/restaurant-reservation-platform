package com.restaurant.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Reservation Service.
 * This service handles restaurant reservation management, including creation, modification,
 * confirmation, and cancellation of reservations.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.restaurant.reservation", "com.restaurant.common"})
@EnableScheduling
public class ReservationServiceApplication {

    /**
     * Main entry point for the Reservation Service application.
     * Initializes the Spring Boot application context and starts the service.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}