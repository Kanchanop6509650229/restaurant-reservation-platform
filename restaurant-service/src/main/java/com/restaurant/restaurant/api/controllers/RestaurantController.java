package com.restaurant.restaurant.api.controllers;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.common.dto.restaurant.RestaurantDTO;
import com.restaurant.restaurant.dto.RestaurantCreateRequest;
import com.restaurant.restaurant.dto.RestaurantSearchCriteria;
import com.restaurant.restaurant.dto.RestaurantUpdateRequest;
import com.restaurant.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @Valid @RequestBody RestaurantCreateRequest createRequest) {
        RestaurantDTO restaurant = restaurantService.createRestaurant(createRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.success(restaurant, "Restaurant created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<RestaurantDTO>> updateRestaurant(
            @PathVariable String id,
            @Valid @RequestBody RestaurantUpdateRequest updateRequest) {
        RestaurantDTO restaurant = restaurantService.updateRestaurant(id, updateRequest);
        return ResponseEntity.ok(ResponseDTO.success(restaurant, "Restaurant updated successfully"));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ResponseDTO<Void>> toggleRestaurantActive(
            @PathVariable String id,
            @RequestParam boolean active) {
        restaurantService.toggleRestaurantActive(id, active);
        String message = active ? "Restaurant activated successfully" : "Restaurant deactivated successfully";
        return ResponseEntity.ok(ResponseDTO.success(null, message));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteRestaurant(@PathVariable String id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok(ResponseDTO.success(null, "Restaurant deleted successfully"));
    }
}