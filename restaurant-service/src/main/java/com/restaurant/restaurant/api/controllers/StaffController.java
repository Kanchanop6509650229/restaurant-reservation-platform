package com.restaurant.restaurant.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.restaurant.domain.models.Staff;
import com.restaurant.restaurant.domain.repositories.StaffRepository;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/staff")
public class StaffController {

    private final StaffRepository staffRepository;

    public StaffController(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<Staff>>> getStaffByRestaurantId(
            @PathVariable String restaurantId) {
        List<Staff> staff = staffRepository.findByRestaurantId(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(staff));
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<ResponseDTO<List<Staff>>> getStaffByPosition(
            @PathVariable String restaurantId,
            @PathVariable String position) {
        List<Staff> staff = staffRepository.findByRestaurantIdAndPosition(restaurantId, position);
        return ResponseEntity.ok(ResponseDTO.success(staff));
    }

    // Add more endpoints as needed for staff management
}