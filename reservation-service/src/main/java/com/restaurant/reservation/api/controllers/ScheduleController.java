package com.restaurant.reservation.api.controllers;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.reservation.dto.ScheduleDTO;
import com.restaurant.reservation.dto.ScheduleUpdateRequest;
import com.restaurant.reservation.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing restaurant schedules.
 * Provides endpoints for retrieving and updating restaurant operating schedules.
 * Handles date-based operations for restaurant availability and operating hours.
 */
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

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
        List<ScheduleDTO> schedules = scheduleService.getScheduleForRestaurant(restaurantId, startDate, endDate);
        return ResponseEntity.ok(ResponseDTO.success(schedules));
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
        ScheduleDTO schedule = scheduleService.updateSchedule(restaurantId, date, updateRequest);
        return ResponseEntity.ok(ResponseDTO.success(schedule, "Schedule updated successfully"));
    }
}