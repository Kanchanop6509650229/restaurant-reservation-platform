package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

public class ReservationModifiedEvent extends BaseEvent implements ReservationEvent {
    
    private final String reservationId;
    private final String restaurantId;
    private final String userId;
    private final String oldTime;
    private final String newTime;
    private final int oldPartySize;
    private final int newPartySize;
    
    public ReservationModifiedEvent(String reservationId, String restaurantId, String userId,
                                   String oldTime, String newTime, int oldPartySize, int newPartySize) {
        super("RESERVATION_MODIFIED");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.oldTime = oldTime;
        this.newTime = newTime;
        this.oldPartySize = oldPartySize;
        this.newPartySize = newPartySize;
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
    
    public String getOldTime() {
        return oldTime;
    }
    
    public String getNewTime() {
        return newTime;
    }
    
    public int getOldPartySize() {
        return oldPartySize;
    }
    
    public int getNewPartySize() {
        return newPartySize;
    }
}