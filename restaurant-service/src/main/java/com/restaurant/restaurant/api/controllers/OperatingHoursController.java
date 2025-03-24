package com.restaurant.restaurant.api.controllers;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.restaurant.dto.OperatingHoursDTO;
import com.restaurant.restaurant.dto.OperatingHoursUpdateRequest;
import com.restaurant.restaurant.service.OperatingHoursService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/operating-hours")
public class OperatingHoursController {

    private final OperatingHoursService operatingHoursService;

    public OperatingHoursController(OperatingHoursService operatingHoursService) {
        this.operatingHoursService = operatingHoursService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<OperatingHoursDTO>>> getOperatingHoursByRestaurantId(
            @PathVariable String restaurantId) {
        List<OperatingHoursDTO> hours = operatingHoursService.getOperatingHoursByRestaurantId(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(hours));
    }

    @PutMapping("/{day}")
    public ResponseEntity<ResponseDTO<OperatingHoursDTO>> updateOperatingHours(
            @PathVariable String restaurantId,
            @PathVariable DayOfWeek day,
            @Valid @RequestBody OperatingHoursUpdateRequest updateRequest) {
        OperatingHoursDTO hours = operatingHoursService.updateOperatingHours(restaurantId, day, updateRequest);
        return ResponseEntity.ok(ResponseDTO.success(hours, "Operating hours updated successfully"));
    }
}