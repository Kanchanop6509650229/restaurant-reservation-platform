package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event with the response of a reservation time validation request.
 */
public class ReservationTimeValidationResponseEvent extends BaseEvent {
    
    private String restaurantId;
    private String correlationId;
    private boolean valid;
    private String errorMessage;
    
    public ReservationTimeValidationResponseEvent() {
        super();
    }
    
    public ReservationTimeValidationResponseEvent(String restaurantId, String correlationId, 
                                                boolean valid) {
        super();
        this.restaurantId = restaurantId;
        this.correlationId = correlationId;
        this.valid = valid;
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
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}