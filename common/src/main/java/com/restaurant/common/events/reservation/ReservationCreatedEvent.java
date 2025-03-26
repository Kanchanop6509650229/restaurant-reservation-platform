package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

public class ReservationCreatedEvent extends BaseEvent implements ReservationEvent {
    
    private final String reservationId;
    private final String restaurantId;
    private final String userId;
    private final String reservationTime;
    private final int partySize;
    private final String tableId;
    
    public ReservationCreatedEvent(String reservationId, String restaurantId, String userId,
                                  String reservationTime, int partySize, String tableId) {
        super("RESERVATION_CREATED");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.reservationTime = reservationTime;
        this.partySize = partySize;
        this.tableId = tableId;
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
    
    public String getReservationTime() {
        return reservationTime;
    }
    
    public int getPartySize() {
        return partySize;
    }
    
    public String getTableId() {
        return tableId;
    }
}