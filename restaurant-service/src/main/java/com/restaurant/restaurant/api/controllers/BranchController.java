package com.restaurant.restaurant.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.restaurant.domain.models.Branch;
import com.restaurant.restaurant.domain.repositories.BranchRepository;

/**
 * REST Controller for managing restaurant branch information.
 * This controller provides endpoints for:
 * - Retrieving branches for a specific restaurant
 * - Finding nearby branches based on location
 * - Managing branch information and locations
 * 
 * All endpoints are prefixed with '/api/restaurants/{restaurantId}/branches'.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/branches")
public class BranchController {

    /** Repository for branch data access */
    private final BranchRepository branchRepository;

    /**
     * Constructs a new BranchController with required dependencies.
     *
     * @param branchRepository Repository for branch data access
     */
    public BranchController(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    /**
     * Retrieves all active branches for a specific restaurant.
     * This endpoint returns a list of all active branches associated
     * with the given restaurant, including their location and contact information.
     *
     * @param restaurantId The ID of the restaurant
     * @return ResponseEntity containing a list of Branch objects
     */
    @GetMapping
    public ResponseEntity<ResponseDTO<List<Branch>>> getBranchesByRestaurantId(
            @PathVariable String restaurantId) {
        List<Branch> branches = branchRepository.findByRestaurantIdAndActiveTrue(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(branches));
    }

    /**
     * Finds branches near a specified location.
     * This endpoint returns branches within a specified distance
     * (in kilometers) from the given coordinates.
     * The distance is converted to meters for the database query.
     *
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param distance Maximum distance in kilometers (default: 5.0)
     * @return ResponseEntity containing a list of nearby Branch objects
     */
    @GetMapping("/nearby")
    public ResponseEntity<ResponseDTO<List<Branch>>> findNearbyBranches(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double distance) {
        // Convert km to meters
        double distanceInMeters = distance * 1000;
        List<Branch> branches = branchRepository.findNearbyBranches(latitude, longitude, distanceInMeters);
        return ResponseEntity.ok(ResponseDTO.success(branches));
    }

    // Add more endpoints as needed for branch management
}