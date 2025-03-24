package com.restaurant.restaurant.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.restaurant.dto.RestaurantStatisticsDTO;
import com.restaurant.restaurant.service.RestaurantStatisticsService;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/statistics")
public class RestaurantStatisticsController {

    private final RestaurantStatisticsService statisticsService;

    public RestaurantStatisticsController(RestaurantStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<RestaurantStatisticsDTO>> getRestaurantStatistics(
            @PathVariable String restaurantId) {
        RestaurantStatisticsDTO statistics = statisticsService.getRestaurantStatistics(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(statistics));
    }
}