package com.restaurant.reservation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.restaurant.common.dto.restaurant.RestaurantDTO;
import com.restaurant.common.events.restaurant.RestaurantSearchResponseEvent;
import com.restaurant.reservation.dto.RestaurantSearchCriteriaDTO;
import com.restaurant.reservation.kafka.producers.RestaurantEventProducer;

/**
 * Service responsible for searching restaurants based on reservation criteria.
 * This service provides mock data for testing purposes.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Service
public class RestaurantSearchService {

    /** Logger for this service */
    private static final Logger logger = LoggerFactory.getLogger(RestaurantSearchService.class);

    /** Producer for sending restaurant search requests */
    private final RestaurantEventProducer eventProducer;

    /**
     * Constructs a new RestaurantSearchService with required dependencies.
     *
     * @param eventProducer Producer for sending restaurant search requests
     */
    public RestaurantSearchService(RestaurantEventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    /**
     * Searches for available restaurants based on the provided criteria.
     * This method returns mock data for testing purposes.
     *
     * @param criteria The search criteria including date, time, party size, etc.
     * @return CompletableFuture containing a list of matching restaurants
     */
    public CompletableFuture<List<RestaurantDTO>> searchRestaurants(RestaurantSearchCriteriaDTO criteria) {
        // Log search criteria based on what's provided
        if (criteria.getRestaurantId() != null) {
            logger.info("Searching for restaurant by ID: {}", criteria.getRestaurantId());
        } else if (criteria.getRestaurantName() != null) {
            logger.info("Searching for restaurants by name: {}", criteria.getRestaurantName());
        } else {
            logger.info("Searching restaurants with criteria: date={}, time={}, partySize={}, cuisineType={}",
                    criteria.getDate(), criteria.getTime(), criteria.getPartySize(), criteria.getCuisineType());
        }

        // For testing/development, return mock data immediately
        CompletableFuture<List<RestaurantDTO>> future = new CompletableFuture<>();

        // Create mock restaurant data
        List<RestaurantDTO> mockRestaurants = createMockRestaurants(criteria);

        // Complete the future with mock data
        future.complete(mockRestaurants);

        return future;
    }

    /**
     * Creates mock restaurant data for testing purposes.
     *
     * @param criteria The search criteria
     * @return List of mock restaurants
     */
    private List<RestaurantDTO> createMockRestaurants(RestaurantSearchCriteriaDTO criteria) {
        List<RestaurantDTO> restaurants = new ArrayList<>();

        // Create fixed IDs for consistent lookup
        String[] fixedIds = {
            "f47ac10b-58cc-4372-a567-0e02b2c3d479",
            "550e8400-e29b-41d4-a716-446655440000",
            "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
        };

        // Create 3 mock restaurants
        for (int i = 1; i <= 3; i++) {
            RestaurantDTO restaurant = new RestaurantDTO();
            restaurant.setId(fixedIds[i-1]);
            restaurant.setName("Restaurant " + i);
            restaurant.setDescription("A great place to eat with delicious food");
            restaurant.setAddress("123 Main St, City " + i);
            restaurant.setPhoneNumber("123-456-789" + i);
            restaurant.setEmail("info@restaurant" + i + ".com");
            restaurant.setWebsite("https://www.restaurant" + i + ".com");
            restaurant.setLatitude(40.7128 + (i * 0.01));
            restaurant.setLongitude(-74.0060 + (i * 0.01));
            restaurant.setCuisineType(criteria.getCuisineType() != null ? criteria.getCuisineType() : "Italian");
            restaurant.setCapacity(50 + (i * 10));
            restaurant.setAverageRating(new BigDecimal("4." + i));
            restaurant.setActive(true);

            restaurants.add(restaurant);
        }

        // Filter by ID if provided
        if (criteria.getRestaurantId() != null && !criteria.getRestaurantId().isEmpty()) {
            String searchId = criteria.getRestaurantId();
            return restaurants.stream()
                    .filter(r -> r.getId().equals(searchId))
                    .toList();
        }

        // Filter by name if provided
        if (criteria.getRestaurantName() != null && !criteria.getRestaurantName().isEmpty()) {
            String searchName = criteria.getRestaurantName().toLowerCase();
            return restaurants.stream()
                    .filter(r -> r.getName().toLowerCase().contains(searchName))
                    .toList();
        }

        logger.info("Created {} mock restaurants for testing", restaurants.size());
        return restaurants;
    }

    /**
     * Processes a restaurant search response event.
     * This method is a placeholder for future implementation when Kafka communication is set up.
     *
     * @param event The restaurant search response event
     */
    public void processSearchResponse(RestaurantSearchResponseEvent event) {
        logger.info("Received restaurant search response: correlationId={}", event.getCorrelationId());
        // This method is a placeholder for future implementation
    }
}
