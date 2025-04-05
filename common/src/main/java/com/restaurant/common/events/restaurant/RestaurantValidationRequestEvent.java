package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event to request validation of a restaurant.
 */
public class RestaurantValidationRequestEvent extends BaseEvent {
    
    private String restaurantId;
    private String correlationId;
    
    public RestaurantValidationRequestEvent() {
        super();
    }
    
    public RestaurantValidationRequestEvent(String restaurantId, String correlationId) {
        super();
        this.restaurantId = restaurantId;
        this.correlationId = correlationId;
    }
    
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}