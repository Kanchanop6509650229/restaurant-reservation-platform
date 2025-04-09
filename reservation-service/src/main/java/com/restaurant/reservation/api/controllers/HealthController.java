package com.restaurant.reservation.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller for the Reservation Service.
 * Provides endpoints for monitoring the service's health and availability.
 * Used by load balancers and monitoring systems to verify service status.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

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
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "reservation-service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}