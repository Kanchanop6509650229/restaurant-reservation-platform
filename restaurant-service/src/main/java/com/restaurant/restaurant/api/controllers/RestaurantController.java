package com.restaurant.restaurant.api.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.common.dto.restaurant.RestaurantDTO;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.restaurant.dto.RestaurantCreateRequest;
import com.restaurant.restaurant.dto.RestaurantSearchCriteria;
import com.restaurant.restaurant.dto.RestaurantUpdateRequest;
import com.restaurant.restaurant.security.CurrentUser;
import com.restaurant.restaurant.service.RestaurantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/public/all")
    public ResponseEntity<ResponseDTO<List<RestaurantDTO>>> getAllRestaurants() {
        List<RestaurantDTO> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(ResponseDTO.success(restaurants));
    }

    @GetMapping("/public")
    public ResponseEntity<ResponseDTO<Page<RestaurantDTO>>> getRestaurantsPaged(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<RestaurantDTO> restaurants = restaurantService.getAllRestaurantsPaged(pageable);
        return ResponseEntity.ok(ResponseDTO.success(restaurants));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ResponseDTO<RestaurantDTO>> getRestaurantById(@PathVariable String id) {
        RestaurantDTO restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(ResponseDTO.success(restaurant));
    }

    @GetMapping("/public/search")
    public ResponseEntity<ResponseDTO<Page<RestaurantDTO>>> searchRestaurants(
            @Valid RestaurantSearchCriteria criteria,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<RestaurantDTO> restaurants = restaurantService.searchRestaurants(criteria, pageable);
        return ResponseEntity.ok(ResponseDTO.success(restaurants));
    }

    @GetMapping("/public/nearby")
    public ResponseEntity<ResponseDTO<List<RestaurantDTO>>> findNearbyRestaurants(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double distance) {
        List<RestaurantDTO> restaurants = restaurantService.findNearbyRestaurants(latitude, longitude, distance);
        return ResponseEntity.ok(ResponseDTO.success(restaurants));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<RestaurantDTO>> createRestaurant(
            @Valid @RequestBody RestaurantCreateRequest createRequest,
            @CurrentUser String userId) {
        // Set the owner ID to the current user's ID
        createRequest.setOwnerId(userId);
        
        RestaurantDTO restaurant = restaurantService.createRestaurant(createRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.success(restaurant, "Restaurant created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<RestaurantDTO>> updateRestaurant(
            @PathVariable String id,
            @Valid @RequestBody RestaurantUpdateRequest updateRequest,
            @CurrentUser String userId) {
        // Check if the user is the owner
        RestaurantDTO restaurant = restaurantService.getRestaurantById(id);
        
        if (!restaurant.getOwnerId().equals(userId)) {
            throw new ValidationException("ownerId", "You don't have permission to update this restaurant");
        }
        
        RestaurantDTO updatedRestaurant = restaurantService.updateRestaurant(id, updateRequest);
        return ResponseEntity.ok(ResponseDTO.success(updatedRestaurant, "Restaurant updated successfully"));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ResponseDTO<Void>> toggleRestaurantActive(
            @PathVariable String id,
            @RequestParam boolean active,
            @CurrentUser String userId) {
        // Check if the user is the owner
        RestaurantDTO restaurant = restaurantService.getRestaurantById(id);
        
        if (!restaurant.getOwnerId().equals(userId)) {
            throw new ValidationException("ownerId", "You don't have permission to update this restaurant");
        }
        
        restaurantService.toggleRestaurantActive(id, active);
        String message = active ? "Restaurant activated successfully" : "Restaurant deactivated successfully";
        return ResponseEntity.ok(ResponseDTO.success(null, message));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteRestaurant(
            @PathVariable String id,
            @CurrentUser String userId) {
        // Check if the user is the owner
        RestaurantDTO restaurant = restaurantService.getRestaurantById(id);
        
        if (!restaurant.getOwnerId().equals(userId)) {
            throw new ValidationException("ownerId", "You don't have permission to delete this restaurant");
        }
        
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok(ResponseDTO.success(null, "Restaurant deleted successfully"));
    }
}