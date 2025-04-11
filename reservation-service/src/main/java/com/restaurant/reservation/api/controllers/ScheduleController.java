package com.restaurant.reservation.api.controllers;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.reservation.dto.ScheduleDTO;
import com.restaurant.reservation.dto.ScheduleUpdateRequest;
import com.restaurant.reservation.service.ScheduleService;

import jakarta.validation.Valid;



/**
 * REST Controller for managing restaurant schedules.
 * Provides endpoints for retrieving and updating restaurant operating schedules.
 * Handles date-based operations for restaurant availability and operating hours.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    /** Logger for this controller */
    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    /** Service for schedule operations */
    private final ScheduleService scheduleService;

    /**
     * Constructs a new ScheduleController with the specified ScheduleService.
     *
     * @param scheduleService The service responsible for schedule management
     */
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * Retrieves the schedule for a specific restaurant within a date range.
     * Returns all schedule entries including operating hours and special events.
     *
     * @param restaurantId The ID of the restaurant
     * @param startDate The start date of the schedule period (inclusive)
     * @param endDate The end date of the schedule period (inclusive)
     * @return ResponseEntity containing a list of schedule entries
     */
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ResponseDTO<List<ScheduleDTO>>> getScheduleForRestaurant(
            @PathVariable String restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("Fetching schedule for restaurant: {}, date range: {} to {}", restaurantId, startDate, endDate);

        try {
            List<ScheduleDTO> schedules = scheduleService.getScheduleForRestaurant(restaurantId, startDate, endDate);
            logger.debug("Found {} schedule entries for restaurant {}", schedules.size(), restaurantId);
            return ResponseEntity.ok(ResponseDTO.success(schedules));
        } catch (EntityNotFoundException e) {
            logger.warn("Restaurant not found: {}", restaurantId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.error("Restaurant not found with ID: " + restaurantId));
        } catch (ValidationException e) {
            logger.warn("Invalid schedule request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching schedule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error("Failed to fetch schedule: " + e.getMessage()));
        }
    }

    /**
     * Updates the schedule for a specific restaurant on a given date.
     * Requires ADMIN or RESTAURANT_OWNER role.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date for which to update the schedule
     * @param updateRequest The schedule update request containing new schedule details
     * @return ResponseEntity containing the updated schedule
     */
    @PutMapping("/restaurant/{restaurantId}/date/{date}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ResponseDTO<ScheduleDTO>> updateSchedule(
            @PathVariable String restaurantId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody ScheduleUpdateRequest updateRequest) {
        logger.info("Updating schedule for restaurant: {}, date: {}", restaurantId, date);

        try {
            ScheduleDTO schedule = scheduleService.updateSchedule(restaurantId, date, updateRequest);
            logger.info("Successfully updated schedule for restaurant: {}, date: {}", restaurantId, date);
            return ResponseEntity.ok(ResponseDTO.success(schedule, "Schedule updated successfully"));
        } catch (EntityNotFoundException e) {
            logger.warn("Restaurant not found for schedule update: {}", restaurantId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.error("Restaurant not found with ID: " + restaurantId));
        } catch (ValidationException e) {
            logger.warn("Invalid schedule update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating schedule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error("Failed to update schedule: " + e.getMessage()));
        }
    }
}