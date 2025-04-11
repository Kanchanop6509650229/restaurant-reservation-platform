package com.restaurant.restaurant.api.controllers;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.restaurant.dto.OperatingHoursBatchUpdateRequest;
import com.restaurant.restaurant.dto.OperatingHoursDTO;
import com.restaurant.restaurant.dto.OperatingHoursUpdateRequest;
import com.restaurant.restaurant.service.OperatingHoursService;

import jakarta.validation.Valid;

/**
 * REST Controller for managing restaurant operating hours.
 * This controller provides endpoints for:
 * - Retrieving operating hours for restaurants
 * - Updating operating hours for specific days
 * - Batch updating operating hours for all days
 * 
 * All endpoints are prefixed with '/api/restaurants/{restaurantId}/operating-hours'.
 * Public endpoints are further prefixed with '/public'.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/operating-hours")
public class OperatingHoursController {

    /** Service layer for operating hours operations */
    private final OperatingHoursService operatingHoursService;

    /**
     * Constructs a new OperatingHoursController with required dependencies.
     *
     * @param operatingHoursService Service layer for operating hours operations
     */
    public OperatingHoursController(OperatingHoursService operatingHoursService) {
        this.operatingHoursService = operatingHoursService;
    }

    /**
     * Retrieves all operating hours for a specific restaurant.
     * This endpoint is publicly accessible and returns operating hours
     * for all days of the week.
     *
     * @param restaurantId The ID of the restaurant
     * @return ResponseEntity containing a list of OperatingHoursDTOs
     */
    @GetMapping("/public")
    public ResponseEntity<ResponseDTO<List<OperatingHoursDTO>>> getOperatingHoursByRestaurantId(
            @PathVariable String restaurantId) {
        List<OperatingHoursDTO> hours = operatingHoursService.getOperatingHoursByRestaurantId(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(hours));
    }

    /**
     * Retrieves operating hours for a specific day of the week.
     * This endpoint is publicly accessible.
     *
     * @param restaurantId The ID of the restaurant
     * @param day The day of the week to retrieve hours for
     * @return ResponseEntity containing the OperatingHoursDTO for the specified day
     */
    @GetMapping("/public/{day}")
    public ResponseEntity<ResponseDTO<OperatingHoursDTO>> getPublicOperatingHoursByDay(
            @PathVariable String restaurantId,
            @PathVariable DayOfWeek day) {
        
        OperatingHoursDTO hours = operatingHoursService.getOperatingHoursByDay(restaurantId, day);
        return ResponseEntity.ok(ResponseDTO.success(hours));
    }

    /**
     * Updates operating hours for a specific day of the week.
     *
     * @param restaurantId The ID of the restaurant
     * @param day The day of the week to update
     * @param updateRequest The update request containing new operating hours
     * @return ResponseEntity containing the updated OperatingHoursDTO
     */
    @PutMapping("/{day}")
    public ResponseEntity<ResponseDTO<OperatingHoursDTO>> updateOperatingHours(
            @PathVariable String restaurantId,
            @PathVariable DayOfWeek day,
            @Valid @RequestBody OperatingHoursUpdateRequest updateRequest) {
        OperatingHoursDTO hours = operatingHoursService.updateOperatingHours(restaurantId, day, updateRequest);
        return ResponseEntity.ok(ResponseDTO.success(hours, "Operating hours updated successfully"));
    }

    /**
     * Updates operating hours for all days of the week in a single operation.
     * This endpoint allows batch updating of operating hours for efficiency.
     *
     * @param restaurantId The ID of the restaurant
     * @param updateRequest The batch update request containing operating hours for all days
     * @return ResponseEntity containing a list of updated OperatingHoursDTOs
     */
    @PutMapping
    public ResponseEntity<ResponseDTO<List<OperatingHoursDTO>>> updateAllOperatingHours(
            @PathVariable String restaurantId,
            @Valid @RequestBody OperatingHoursBatchUpdateRequest updateRequest) {
        List<OperatingHoursDTO> hours = operatingHoursService.updateAllOperatingHours(restaurantId, updateRequest);
        return ResponseEntity.ok(ResponseDTO.success(hours, "Operating hours updated successfully"));
    }
}