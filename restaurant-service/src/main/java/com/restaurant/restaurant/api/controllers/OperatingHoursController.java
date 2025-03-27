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
import com.restaurant.restaurant.dto.OperatingHoursDTO;
import com.restaurant.restaurant.dto.OperatingHoursUpdateRequest;
import com.restaurant.restaurant.service.OperatingHoursService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/operating-hours")
public class OperatingHoursController {

    private final OperatingHoursService operatingHoursService;

    public OperatingHoursController(OperatingHoursService operatingHoursService) {
        this.operatingHoursService = operatingHoursService;
    }

    @GetMapping("/public")
    public ResponseEntity<ResponseDTO<List<OperatingHoursDTO>>> getOperatingHoursByRestaurantId(
            @PathVariable String restaurantId) {
        List<OperatingHoursDTO> hours = operatingHoursService.getOperatingHoursByRestaurantId(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(hours));
    }

    @GetMapping("/public/{day}")
    public ResponseEntity<ResponseDTO<OperatingHoursDTO>> getPublicOperatingHoursByDay(
            @PathVariable String restaurantId,
            @PathVariable DayOfWeek day) {
        // เพิ่มเมธอดในเซอร์วิสเพื่อดึงข้อมูลเวลาทำการของวันเฉพาะ
        OperatingHoursDTO hours = operatingHoursService.getOperatingHoursByDay(restaurantId, day);
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