package com.restaurant.restaurant.api.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for health check and service status monitoring.
 * This controller provides endpoints for:
 * - Checking the health status of the restaurant service
 * - Monitoring service availability
 * - Providing basic service information
 * 
 * All endpoints are prefixed with '/api/health'.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * Performs a health check of the restaurant service.
     * This endpoint returns:
     * - Current service status
     * - Service name
     * - Timestamp of the check
     * 
     * @return ResponseEntity containing health check information
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "restaurant-service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}