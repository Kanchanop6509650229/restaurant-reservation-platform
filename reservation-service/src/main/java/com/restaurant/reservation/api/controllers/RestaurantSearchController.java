package com.restaurant.reservation.api.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.common.dto.restaurant.RestaurantDTO;
import com.restaurant.reservation.dto.RestaurantSearchCriteriaDTO;
import com.restaurant.reservation.service.RestaurantSearchService;

import jakarta.validation.Valid;

/**
 * REST Controller for searching restaurants based on reservation criteria.
 * Provides endpoints for finding available restaurants that match specific criteria.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/restaurants/search")
public class RestaurantSearchController {

    /** Logger for this controller */
    private static final Logger logger = LoggerFactory.getLogger(RestaurantSearchController.class);

    /** Service for restaurant search operations */
    private final RestaurantSearchService restaurantSearchService;

    /**
     * Constructs a new RestaurantSearchController with required dependencies.
     *
     * @param restaurantSearchService Service for restaurant search operations
     */
    public RestaurantSearchController(RestaurantSearchService restaurantSearchService) {
        this.restaurantSearchService = restaurantSearchService;
    }

    /**
     * Searches for available restaurants based on the provided criteria.
     * This endpoint accepts search criteria including date, time, party size,
     * cuisine type, location, etc., and returns a list of matching restaurants.
     *
     * @param criteria The search criteria
     * @return ResponseEntity containing a list of matching restaurants
     */
    @PostMapping
    public ResponseEntity<ResponseDTO<List<RestaurantDTO>>> searchRestaurants(
            @Valid @RequestBody RestaurantSearchCriteriaDTO criteria) {
        logger.info("Searching restaurants with criteria: date={}, time={}, partySize={}",
                criteria.getDate(), criteria.getTime(), criteria.getPartySize());

        try {
            // Send the search request and wait for the response
            CompletableFuture<List<RestaurantDTO>> future = restaurantSearchService.searchRestaurants(criteria);
            List<RestaurantDTO> restaurants = future.get(15, TimeUnit.SECONDS);

            if (restaurants.isEmpty()) {
                logger.info("No restaurants found matching the criteria");
                return ResponseEntity.ok(ResponseDTO.success(restaurants, "No restaurants found matching the criteria"));
            } else {
                logger.info("Found {} restaurants matching the criteria", restaurants.size());
                return ResponseEntity.ok(ResponseDTO.success(restaurants, "Found " + restaurants.size() + " restaurants matching the criteria"));
            }
        } catch (TimeoutException e) {
            logger.error("Restaurant search timed out: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(ResponseDTO.error("Restaurant search timed out. Please try again later."));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Restaurant search interrupted: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error("Restaurant search was interrupted. Please try again."));
        } catch (ExecutionException e) {
            logger.error("Error during restaurant search: {}", e.getCause().getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error("Error during restaurant search: " + e.getCause().getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during restaurant search: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error("Unexpected error during restaurant search. Please try again later."));
        }
    }
}
