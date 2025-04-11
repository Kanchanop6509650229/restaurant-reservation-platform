package com.restaurant.reservation.api.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller for the Reservation Service.
 * Provides endpoints for monitoring the service's health and availability.
 * Used by load balancers and monitoring systems to verify service status.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /** Logger for this controller */
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    /**
     * Performs a basic health check of the reservation service.
     * Returns the current status, service name, and timestamp.
     * This endpoint is typically used by:
     * - Load balancers for service discovery
     * - Monitoring systems for uptime checks
     * - Container orchestration platforms for health verification
     *
     * @return ResponseEntity containing health check information:
     *         - status: Current service status (UP/DOWN)
     *         - service: Service identifier
     *         - timestamp: Current system time in milliseconds
     *         - version: Service version information
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.debug("Health check requested");

        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "reservation-service");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0");

        logger.debug("Health check completed: status={}", "UP");
        return ResponseEntity.ok(response);
    }

    /**
     * Provides detailed health information about the service.
     * Includes memory usage, uptime, and other system metrics.
     *
     * @return ResponseEntity containing detailed health information
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        logger.debug("Detailed health check requested");

        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "reservation-service");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0");

        // Add system metrics
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("memory", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        metrics.put("totalMemory", Runtime.getRuntime().totalMemory());
        metrics.put("freeMemory", Runtime.getRuntime().freeMemory());
        metrics.put("processors", Runtime.getRuntime().availableProcessors());

        response.put("metrics", metrics);

        logger.debug("Detailed health check completed");
        return ResponseEntity.ok(response);
    }
}