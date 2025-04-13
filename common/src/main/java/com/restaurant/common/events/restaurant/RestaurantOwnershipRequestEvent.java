package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event sent to validate if a user is the owner of a restaurant.
 * This event is published to Kafka and consumed by the restaurant service.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class RestaurantOwnershipRequestEvent extends BaseEvent implements RestaurantEvent {

    /** ID of the restaurant to check */
    private String restaurantId;

    /** ID of the user to check ownership for */
    private String userId;

    /** Correlation ID for tracking the request-response pair */
    private String correlationId;

    /**
     * Default constructor required for serialization.
     */
    public RestaurantOwnershipRequestEvent() {
        super();
    }

    /**
     * Creates a new restaurant ownership validation request.
     *
     * @param restaurantId ID of the restaurant to check
     * @param userId ID of the user to check ownership for
     * @param correlationId Correlation ID for tracking the request-response pair
     */
    public RestaurantOwnershipRequestEvent(String restaurantId, String userId, String correlationId) {
        super("RESTAURANT_OWNERSHIP_REQUEST");
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.correlationId = correlationId;
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
}
