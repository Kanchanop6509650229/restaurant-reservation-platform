package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents a request to validate a proposed reservation time against a restaurant's operating hours.
 * This event is typically sent to the restaurant service to check if the requested time slot is valid and available.
 * Extends BaseEvent to inherit common event functionality.
 */
public class ReservationTimeValidationRequestEvent extends BaseEvent implements RestaurantEvent {
    
    /**
     * The unique identifier of the restaurant for which the reservation time needs to be validated.
     */
    private String restaurantId;
    
    /**
     * A correlation identifier used to track and match this request with its corresponding response.
     */
    private String correlationId;
    
    /**
     * The proposed reservation time that needs to be validated, formatted as an ISO-8601 datetime string.
     */
    private String reservationTime;
    
    /**
     * Default constructor required for serialization/deserialization.
     */
    public ReservationTimeValidationRequestEvent() {
        super();
    }
    
    /**
     * Constructs a new ReservationTimeValidationRequestEvent with all required fields.
     *
     * @param restaurantId    The ID of the restaurant for validation
     * @param correlationId   The correlation ID to track the request-response cycle
     * @param reservationTime The proposed reservation time to validate
     */
    public ReservationTimeValidationRequestEvent(String restaurantId, String correlationId, 
                                               String reservationTime) {
        super();
        this.restaurantId = restaurantId;
        this.correlationId = correlationId;
        this.reservationTime = reservationTime;
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
     * @param restaurantId The unique identifier of the restaurant
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
    
    /**
     * Gets the proposed reservation time that needs validation.
     *
     * @return The reservation time as an ISO-8601 datetime string
     */
    public String getReservationTime() {
        return reservationTime;
    }
    
    /**
     * Sets the proposed reservation time for validation.
     *
     * @param reservationTime The reservation time as an ISO-8601 datetime string
     */
    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }
}