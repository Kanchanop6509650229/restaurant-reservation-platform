package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event with the response of a restaurant validation request.
 */
public class RestaurantValidationResponseEvent extends BaseEvent {
    
    private String restaurantId;
    private String correlationId;
    private boolean exists;
    private boolean active;
    private String errorMessage;
    
    public RestaurantValidationResponseEvent() {
        super();
    }
    
    public RestaurantValidationResponseEvent(String restaurantId, String correlationId, 
                                           boolean exists, boolean active) {
        super();
        this.restaurantId = restaurantId;
        this.correlationId = correlationId;
        this.exists = exists;
        this.active = active;
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
    
    public boolean isExists() {
        return exists;
    }
    
    public void setExists(boolean exists) {
        this.exists = exists;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}