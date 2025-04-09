package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents the response to a restaurant validation request.
 * This event contains information about whether the restaurant exists in the system,
 * whether it is currently active, and any potential error messages.
 * Extends BaseEvent to inherit common event functionality.
 */
public class RestaurantValidationResponseEvent extends BaseEvent implements RestaurantEvent {
    
    /**
     * The unique identifier of the restaurant that was validated.
     */
    private String restaurantId;
    
    /**
     * The correlation identifier matching this response to its original request.
     */
    private String correlationId;
    
    /**
     * Flag indicating whether the restaurant exists in the system.
     */
    private boolean exists;
    
    /**
     * Flag indicating whether the restaurant is currently active.
     * This is only relevant if the restaurant exists (exists = true).
     */
    private boolean active;
    
    /**
     * Optional error message providing details about any validation failures.
     * This field is typically null when both exists and active are true.
     */
    private String errorMessage;
    
    /**
     * Default constructor required for serialization/deserialization.
     */
    public RestaurantValidationResponseEvent() {
        super();
    }
    
    /**
     * Constructs a new RestaurantValidationResponseEvent with the validation results.
     *
     * @param restaurantId   The ID of the restaurant that was validated
     * @param correlationId  The correlation ID matching this response to its request
     * @param exists        Whether the restaurant exists in the system
     * @param active        Whether the restaurant is currently active
     */
    public RestaurantValidationResponseEvent(String restaurantId, String correlationId, 
                                           boolean exists, boolean active) {
        super();
        this.restaurantId = restaurantId;
        this.correlationId = correlationId;
        this.exists = exists;
        this.active = active;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Sets the restaurant ID for this validation response.
     *
     * @param restaurantId The unique identifier of the restaurant
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    /**
     * Gets the correlation ID used to match this response with its request.
     *
     * @return The correlation ID as a String
     */
    public String getCorrelationId() {
        return correlationId;
    }
    
    /**
     * Sets the correlation ID for this validation response.
     *
     * @param correlationId The correlation ID to set
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    
    /**
     * Checks if the restaurant exists in the system.
     *
     * @return true if the restaurant exists, false otherwise
     */
    public boolean isExists() {
        return exists;
    }
    
    /**
     * Sets whether the restaurant exists in the system.
     *
     * @param exists true if the restaurant exists, false otherwise
     */
    public void setExists(boolean exists) {
        this.exists = exists;
    }
    
    /**
     * Checks if the restaurant is currently active.
     * This is only meaningful if the restaurant exists.
     *
     * @return true if the restaurant is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets whether the restaurant is currently active.
     *
     * @param active true if the restaurant is active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Gets the error message explaining any validation failures.
     *
     * @return The error message as a String, or null if validation was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Sets an error message explaining any validation failures.
     *
     * @param errorMessage The error message to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}