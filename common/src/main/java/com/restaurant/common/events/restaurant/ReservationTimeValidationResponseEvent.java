package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents the response to a reservation time validation request.
 * This event contains the validation result and any potential error messages regarding
 * the proposed reservation time for a restaurant.
 * Extends BaseEvent to inherit common event functionality.
 */
public class ReservationTimeValidationResponseEvent extends BaseEvent implements RestaurantEvent {
    
    /**
     * The unique identifier of the restaurant for which the reservation time was validated.
     */
    private String restaurantId;
    
    /**
     * The correlation identifier matching this response to its original request.
     */
    private String correlationId;
    
    /**
     * Flag indicating whether the proposed reservation time is valid.
     * True if the time is valid according to the restaurant's operating hours and availability,
     * false otherwise.
     */
    private boolean valid;
    
    /**
     * Optional error message providing details about why the reservation time is invalid.
     * This field is typically null when valid is true.
     */
    private String errorMessage;
    
    /**
     * Default constructor required for serialization/deserialization.
     */
    public ReservationTimeValidationResponseEvent() {
        super();
    }
    
    /**
     * Constructs a new ReservationTimeValidationResponseEvent with the validation result.
     *
     * @param restaurantId   The ID of the restaurant that performed the validation
     * @param correlationId  The correlation ID matching this response to its request
     * @param valid         Whether the proposed reservation time is valid
     */
    public ReservationTimeValidationResponseEvent(String restaurantId, String correlationId, 
                                                boolean valid) {
        super();
        this.restaurantId = restaurantId;
        this.correlationId = correlationId;
        this.valid = valid;
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
     * Checks if the proposed reservation time is valid.
     *
     * @return true if the reservation time is valid, false otherwise
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Sets the validation status for the proposed reservation time.
     *
     * @param valid true if the time is valid, false otherwise
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    /**
     * Gets the error message explaining why the reservation time is invalid.
     *
     * @return The error message as a String, or null if the time is valid
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Sets an error message explaining why the reservation time is invalid.
     *
     * @param errorMessage The error message to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}