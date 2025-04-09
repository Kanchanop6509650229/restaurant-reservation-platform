package com.restaurant.reservation.config;

import com.restaurant.reservation.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuration class for scheduled tasks in the reservation service.
 * Manages periodic tasks such as processing expired reservations.
 * Uses Spring's scheduling framework for task execution.
 */
@Configuration
public class SchedulingConfig {

    /**
     * Logger instance for this class.
     * Used for logging scheduled task execution and any errors.
     */
    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

    /**
     * Service responsible for reservation-related operations.
     * Used to process expired reservations.
     */
    private final ReservationService reservationService;

    /**
     * Constructs a new SchedulingConfig with the specified ReservationService.
     *
     * @param reservationService The service responsible for reservation operations
     */
    public SchedulingConfig(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Scheduled task to process expired reservations.
     * Runs every minute (60,000 milliseconds) to:
     * - Identify reservations that have expired
     * - Update their status
     * - Handle any necessary notifications
     * - Clean up related resources
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    public void processExpiredReservations() {
        logger.info("Running scheduled task to process expired reservations");
        reservationService.processExpiredReservations();
    }
}