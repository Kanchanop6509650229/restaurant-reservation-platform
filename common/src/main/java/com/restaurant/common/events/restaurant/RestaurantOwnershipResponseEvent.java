package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event sent in response to a restaurant ownership validation request.
 * This event is published to Kafka by the restaurant service and consumed by the reservation service.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class RestaurantOwnershipResponseEvent extends BaseEvent implements RestaurantEvent {

    /** ID of the restaurant that was checked */
    private String restaurantId;

    /** ID of the user that was checked */
    private String userId;

    /** Correlation ID for tracking the request-response pair */
    private String correlationId;

    /** Flag indicating if the user is the owner of the restaurant */
    private boolean isOwner;

    /** Error message if any error occurred during validation */
    private String errorMessage;

    /**
     * Default constructor required for serialization.
     */
    public RestaurantOwnershipResponseEvent() {
        super();
    }

    /**
     * Creates a new restaurant ownership validation response.
     *
     * @param restaurantId ID of the restaurant that was checked
     * @param userId ID of the user that was checked
     * @param correlationId Correlation ID for tracking the request-response pair
     * @param isOwner Flag indicating if the user is the owner of the restaurant
     */
    public RestaurantOwnershipResponseEvent(String restaurantId, String userId, String correlationId, boolean isOwner) {
        super("RESTAURANT_OWNERSHIP_RESPONSE");
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.correlationId = correlationId;
        this.isOwner = isOwner;
    }

    /**
     * Gets the restaurant ID.
     *
     * @return The restaurant ID
     */
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the restaurant ID.
     *
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Gets the user ID.
     *
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId The user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the correlation ID.
     *
     * @return The correlation ID
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the correlation ID.
     *
     * @param correlationId The correlation ID to set
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Checks if the user is the owner of the restaurant.
     *
     * @return true if the user is the owner, false otherwise
     */
    public boolean isOwner() {
        return isOwner;
    }

    /**
     * Sets whether the user is the owner of the restaurant.
     *
     * @param isOwner true if the user is the owner, false otherwise
     */
    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    /**
     * Gets the error message.
     *
     * @return The error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage The error message to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
