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

/**
 * REST Controller for managing restaurant operations.
 * This controller provides endpoints for:
 * - Public access to restaurant information
 * - Restaurant creation and management by owners
 * - Restaurant search and filtering
 * - Location-based restaurant discovery
 * 
 * All endpoints are prefixed with '/api/restaurants'.
 * Public endpoints are further prefixed with '/public'.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    /** Service layer for restaurant operations */
    private final RestaurantService restaurantService;

    /**
     * Constructs a new RestaurantController with required dependencies.
     *
     * @param restaurantService Service layer for restaurant operations
     */
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Retrieves all restaurants in the system.
     * This endpoint is publicly accessible and returns a list of all restaurants
     * without pagination.
     *
     * @return ResponseEntity containing a list of RestaurantDTOs
     */
    @GetMapping("/public/all")
    public ResponseEntity<ResponseDTO<List<RestaurantDTO>>> getAllRestaurants() {
        List<RestaurantDTO> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(ResponseDTO.success(restaurants));
    }

    /**
     * Retrieves restaurants with pagination support.
     * This endpoint is publicly accessible and returns a page of restaurants
     * with default page size of 10.
     *
     * @param pageable Pagination parameters (page number, size, sort)
     * @return ResponseEntity containing a page of RestaurantDTOs
     */
    @GetMapping("/public")
    public ResponseEntity<ResponseDTO<Page<RestaurantDTO>>> getRestaurantsPaged(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<RestaurantDTO> restaurants = restaurantService.getAllRestaurantsPaged(pageable);
        return ResponseEntity.ok(ResponseDTO.success(restaurants));
    }

    /**
     * Retrieves a specific restaurant by its ID.
     * This endpoint is publicly accessible.
     *
     * @param id The ID of the restaurant to retrieve
     * @return ResponseEntity containing the requested RestaurantDTO
     */
    @GetMapping("/public/{id}")
    public ResponseEntity<ResponseDTO<RestaurantDTO>> getRestaurantById(@PathVariable String id) {
        RestaurantDTO restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(ResponseDTO.success(restaurant));
    }

    /**
     * Searches restaurants based on provided criteria.
     * This endpoint is publicly accessible and supports pagination.
     *
     * @param criteria Search criteria (name, cuisine, location, etc.)
     * @param pageable Pagination parameters
     * @return ResponseEntity containing a page of matching RestaurantDTOs
     */
    @GetMapping("/public/search")
    public ResponseEntity<ResponseDTO<Page<RestaurantDTO>>> searchRestaurants(
            @Valid RestaurantSearchCriteria criteria,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<RestaurantDTO> restaurants = restaurantService.searchRestaurants(criteria, pageable);
        return ResponseEntity.ok(ResponseDTO.success(restaurants));
    }

    /**
     * Finds restaurants near a specified location.
     * This endpoint is publicly accessible and returns restaurants within
     * a specified distance (in kilometers) from the given coordinates.
     *
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param distance Maximum distance in kilometers (default: 5.0)
     * @return ResponseEntity containing a list of nearby RestaurantDTOs
     */
    @GetMapping("/public/nearby")
    public ResponseEntity<ResponseDTO<List<RestaurantDTO>>> findNearbyRestaurants(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double distance) {
        List<RestaurantDTO> restaurants = restaurantService.findNearbyRestaurants(latitude, longitude, distance);
        return ResponseEntity.ok(ResponseDTO.success(restaurants));
    }

    /**
     * Creates a new restaurant.
     * This endpoint requires authentication and sets the current user as the owner.
     *
     * @param createRequest The restaurant creation request
     * @param userId The ID of the current authenticated user
     * @return ResponseEntity containing the created RestaurantDTO
     */
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

    /**
     * Updates an existing restaurant.
     * This endpoint requires authentication and ownership verification.
     *
     * @param id The ID of the restaurant to update
     * @param updateRequest The restaurant update request
     * @param userId The ID of the current authenticated user
     * @return ResponseEntity containing the updated RestaurantDTO
     * @throws ValidationException if the user is not the owner of the restaurant
     */
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

    /**
     * Toggles the active status of a restaurant.
     * This endpoint requires authentication and ownership verification.
     *
     * @param id The ID of the restaurant to update
     * @param active The new active status
     * @param userId The ID of the current authenticated user
     * @return ResponseEntity with success message
     * @throws ValidationException if the user is not the owner of the restaurant
     */
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

    /**
     * Deletes a restaurant.
     * This endpoint requires authentication and ownership verification.
     *
     * @param id The ID of the restaurant to delete
     * @param userId The ID of the current authenticated user
     * @return ResponseEntity with success message
     * @throws ValidationException if the user is not the owner of the restaurant
     */
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