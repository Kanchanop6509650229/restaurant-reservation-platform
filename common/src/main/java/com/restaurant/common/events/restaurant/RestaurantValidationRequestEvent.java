package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents a request to validate a restaurant's existence and status.
 * This event is typically sent when a service needs to verify if a restaurant is active
 * and available in the system before performing operations related to it.
 * Extends BaseEvent to inherit common event functionality.
 */
public class RestaurantValidationRequestEvent extends BaseEvent implements RestaurantEvent {
    
    /**
     * The unique identifier of the restaurant that needs to be validated.
     */
    private String restaurantId;
    
    /**
     * A correlation identifier used to track and match this request with its corresponding response.
     */
    private String correlationId;
    
    /**
     * Default constructor required for serialization/deserialization.
     */
    public RestaurantValidationRequestEvent() {
        super();
    }
    
    /**
     * Constructs a new RestaurantValidationRequestEvent with all required fields.
     *
     * @param restaurantId   The ID of the restaurant to validate
     * @param correlationId  The correlation ID to track the request-response cycle
     */
    public RestaurantValidationRequestEvent(String restaurantId, String correlationId) {
        super();
        this.restaurantId = restaurantId;
        this.correlationId = correlationId;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Sets the restaurant ID for this validation request.
     *
     * @param restaurantId The unique identifier of the restaurant to validate
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    /**
     * Gets the correlation ID used to track this request.
     *
     * @return The correlation ID as a String
     */
    public String getCorrelationId() {
        return correlationId;
    }
    
    /**
     * Sets the correlation ID for this validation request.
     *
     * @param correlationId The correlation ID to set
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}