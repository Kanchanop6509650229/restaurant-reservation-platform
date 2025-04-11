package com.restaurant.reservation.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.restaurant.reservation.service.ReservationService;

/**
 * Configuration class for scheduled tasks in the reservation service.
 * Manages periodic tasks such as processing expired reservations.
 * Uses Spring's scheduling framework for task execution.
 *
 * This class handles:
 * - Automatic processing of expired reservations
 * - Automatic completion of past reservations
 * - Scheduled cleanup of old data
 * - Configurable execution intervals
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
@Configuration
public class SchedulingConfig {

    /**
     * Logger instance for this class.
     * Used for logging scheduled task execution and any errors.
     */
    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

    /** Date-time formatter for logging */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Service responsible for reservation-related operations */
    private final ReservationService reservationService;

    /** Interval in milliseconds for processing expired reservations */
    @Value("${scheduling.expired-reservations.interval:60000}")
    private long expiredReservationsInterval;

    /** Interval in milliseconds for cleaning up old data */
    @Value("${scheduling.data-cleanup.interval:86400000}")
    private long dataCleanupInterval;

    /** Age in days of data to be considered for cleanup */
    @Value("${scheduling.data-cleanup.age-days:90}")
    private int dataCleanupAgeDays;

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
    @Scheduled(fixedRateString = "${scheduling.expired-reservations.interval:60000}")
    public void processExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Running scheduled task to process expired reservations at {}", now.format(FORMATTER));

        try {
            long startTime = System.currentTimeMillis();
            reservationService.processExpiredReservations();
            long duration = System.currentTimeMillis() - startTime;

            logger.info("Processed expired reservations in {} ms", duration);
        } catch (Exception e) {
            logger.error("Error processing expired reservations: {}", e.getMessage(), e);
        }
    }

    /**
     * Scheduled task to clean up old reservation data.
     * Runs once a day to archive or delete old reservations that are no longer needed
     * in the active database.
     */
    @Scheduled(fixedRateString = "${scheduling.data-cleanup.interval:86400000}", initialDelay = 3600000)
    public void cleanupOldData() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Running scheduled task to clean up old reservation data at {}", now.format(FORMATTER));

        try {
            long startTime = System.currentTimeMillis();
            // This would call a method in the service to archive or delete old data
            // For now, we'll just log that it would happen
            logger.info("Would clean up reservation data older than {} days", dataCleanupAgeDays);
            long duration = System.currentTimeMillis() - startTime;

            logger.info("Completed data cleanup in {} ms", duration);
        } catch (Exception e) {
            logger.error("Error cleaning up old reservation data: {}", e.getMessage(), e);
        }
    }
}