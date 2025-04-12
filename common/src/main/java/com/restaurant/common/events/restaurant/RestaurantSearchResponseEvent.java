package com.restaurant.common.events.restaurant;

import java.util.List;

import com.restaurant.common.dto.restaurant.RestaurantDTO;
import com.restaurant.common.events.BaseEvent;

/**
 * Event representing the response to a restaurant search request.
 * This event is published in response to a RestaurantSearchRequestEvent,
 * containing the results of the search for available restaurants.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class RestaurantSearchResponseEvent extends BaseEvent implements RestaurantEvent {
    
    /** List of restaurants matching the search criteria */
    private List<RestaurantDTO> restaurants;
    
    /** Whether the search was successful */
    private boolean success;
    
    /** Error message in case of failure */
    private String errorMessage;
    
    /** Correlation ID matching the original request */
    private String correlationId;
    
    /** Restaurant ID if the search was for a specific restaurant */
    private String restaurantId;

    /**
     * Default constructor.
     * Initializes a new restaurant search response event.
     */
    public RestaurantSearchResponseEvent() {
        super("RESTAURANT_SEARCH_RESPONSE");
    }

    /**
     * Creates a new restaurant search response event with all required details.
     *
     * @param restaurants List of restaurants matching the search criteria
     * @param success Whether the search was successful
     * @param errorMessage Error message in case of failure
     * @param correlationId Correlation ID matching the original request
     */
    public RestaurantSearchResponseEvent(List<RestaurantDTO> restaurants, boolean success, 
                                       String errorMessage, String correlationId) {
        super("RESTAURANT_SEARCH_RESPONSE");
        this.restaurants = restaurants;
        this.success = success;
        this.errorMessage = errorMessage;
        this.correlationId = correlationId;
        this.restaurantId = null; // Not for a specific restaurant
    }

    /**
     * Gets the list of restaurants matching the search criteria.
     *
     * @return The list of restaurants
     */
    public List<RestaurantDTO> getRestaurants() {
        return restaurants;
    }

    /**
     * Sets the list of restaurants matching the search criteria.
     *
     * @param restaurants The list of restaurants to set
     */
    public void setRestaurants(List<RestaurantDTO> restaurants) {
        this.restaurants = restaurants;
    }

    /**
     * Gets whether the search was successful.
     *
     * @return true if the search was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets whether the search was successful.
     *
     * @param success true if the search was successful, false otherwise
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the error message in case of failure.
     *
     * @return The error message, or null if the search was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message in case of failure.
     *
     * @param errorMessage The error message to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the correlation ID matching the original request.
     *
     * @return The correlation ID
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the correlation ID matching the original request.
     *
     * @param correlationId The correlation ID to set
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Gets the restaurant ID if the search was for a specific restaurant.
     *
     * @return The restaurant ID, or null if the search was not for a specific restaurant
     */
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the restaurant ID if the search was for a specific restaurant.
     *
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
