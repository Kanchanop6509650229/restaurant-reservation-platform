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

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ResponseDTO<List<ScheduleDTO>>> getScheduleForRestaurant(
            @PathVariable String restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ScheduleDTO> schedules = scheduleService.getScheduleForRestaurant(restaurantId, startDate, endDate);
        return ResponseEntity.ok(ResponseDTO.success(schedules));
    }

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