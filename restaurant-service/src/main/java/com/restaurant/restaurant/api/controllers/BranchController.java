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

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/branches")
public class BranchController {

    private final BranchRepository branchRepository;

    public BranchController(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<Branch>>> getBranchesByRestaurantId(
            @PathVariable String restaurantId) {
        List<Branch> branches = branchRepository.findByRestaurantIdAndActiveTrue(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(branches));
    }

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