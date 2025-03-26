package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

public class ReservationCancelledEvent extends BaseEvent implements ReservationEvent {
    
    private final String reservationId;
    private final String restaurantId;
    private final String userId;
    private final String previousStatus;
    private final String reason;
    
    public ReservationCancelledEvent(String reservationId, String restaurantId, 
                                    String userId, String previousStatus, String reason) {
        super("RESERVATION_CANCELLED");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.previousStatus = previousStatus;
        this.reason = reason;
    }
    
    @Override
    public String getReservationId() {
        return reservationId;
    }
    
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getPreviousStatus() {
        return previousStatus;
    }
    
    public String getReason() {
        return reason;
    }
}