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

/**
 * REST Controller for managing restaurant staff information.
 * This controller provides endpoints for:
 * - Retrieving staff members by restaurant
 * - Filtering staff by position
 * - Managing staff assignments and roles
 * 
 * All endpoints are prefixed with '/api/restaurants/{restaurantId}/staff'.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/staff")
public class StaffController {

    /** Repository for staff data access */
    private final StaffRepository staffRepository;

    /**
     * Constructs a new StaffController with required dependencies.
     *
     * @param staffRepository Repository for staff data access
     */
    public StaffController(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    /**
     * Retrieves all staff members for a specific restaurant.
     * This endpoint returns a list of all staff members associated
     * with the given restaurant, including their roles and contact information.
     *
     * @param restaurantId The ID of the restaurant
     * @return ResponseEntity containing a list of Staff objects
     */
    @GetMapping
    public ResponseEntity<ResponseDTO<List<Staff>>> getStaffByRestaurantId(
            @PathVariable String restaurantId) {
        List<Staff> staff = staffRepository.findByRestaurantId(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(staff));
    }

    /**
     * Retrieves staff members by their position at a specific restaurant.
     * This endpoint allows filtering staff members based on their role
     * (e.g., manager, chef, waiter, etc.).
     *
     * @param restaurantId The ID of the restaurant
     * @param position The position/role to filter by
     * @return ResponseEntity containing a list of Staff objects matching the position
     */
    @GetMapping("/position/{position}")
    public ResponseEntity<ResponseDTO<List<Staff>>> getStaffByPosition(
            @PathVariable String restaurantId,
            @PathVariable String position) {
        List<Staff> staff = staffRepository.findByRestaurantIdAndPosition(restaurantId, position);
        return ResponseEntity.ok(ResponseDTO.success(staff));
    }

    // Add more endpoints as needed for staff management
}