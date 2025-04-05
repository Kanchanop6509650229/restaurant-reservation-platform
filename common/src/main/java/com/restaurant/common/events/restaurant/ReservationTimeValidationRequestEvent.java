package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event to request validation of a reservation time based on restaurant hours.
 */
public class ReservationTimeValidationRequestEvent extends BaseEvent {
    
    private String restaurantId;
    private String correlationId;
    private String reservationTime;
    
    public ReservationTimeValidationRequestEvent() {
        super();
    }
    
    public ReservationTimeValidationRequestEvent(String restaurantId, String correlationId, 
                                               String reservationTime) {
        super();
        this.restaurantId = restaurantId;
        this.correlationId = correlationId;
        this.reservationTime = reservationTime;
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
    
    public String getReservationTime() {
        return reservationTime;
    }
    
    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }
}