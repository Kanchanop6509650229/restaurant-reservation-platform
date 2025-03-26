package com.restaurant.reservation.config;

import com.restaurant.reservation.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class SchedulingConfig {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

    private final ReservationService reservationService;

    public SchedulingConfig(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void processExpiredReservations() {
        logger.info("Running scheduled task to process expired reservations");
        reservationService.processExpiredReservations();
    }
}