package com.restaurant.restaurant.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.restaurant.dto.RestaurantStatisticsDTO;
import com.restaurant.restaurant.service.RestaurantStatisticsService;

/**
 * REST Controller for managing restaurant statistics.
 * This controller provides endpoints for:
 * - Retrieving comprehensive statistics about a restaurant's performance
 * - Analyzing reservation patterns and customer behavior
 * - Monitoring table utilization and revenue metrics
 * 
 * All endpoints are prefixed with '/api/restaurants/{restaurantId}/statistics'.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/statistics")
public class RestaurantStatisticsController {

    /** Service layer for restaurant statistics operations */
    private final RestaurantStatisticsService statisticsService;

    /**
     * Constructs a new RestaurantStatisticsController with required dependencies.
     *
     * @param statisticsService Service layer for restaurant statistics operations
     */
    public RestaurantStatisticsController(RestaurantStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Retrieves comprehensive statistics for a specific restaurant.
     * The statistics include:
     * - Reservation metrics (total, completed, cancelled)
     * - Table utilization rates
     * - Customer demographics
     * - Revenue analytics
     * - Peak hours and days
     *
     * @param restaurantId The ID of the restaurant
     * @return ResponseEntity containing the RestaurantStatisticsDTO
     */
    @GetMapping
    public ResponseEntity<ResponseDTO<RestaurantStatisticsDTO>> getRestaurantStatistics(
            @PathVariable String restaurantId) {
        RestaurantStatisticsDTO statistics = statisticsService.getRestaurantStatistics(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(statistics));
    }
}