package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

public class ReservationConfirmedEvent extends BaseEvent implements ReservationEvent {
    
    private final String reservationId;
    private final String restaurantId;
    private final String userId;
    private final String tableId;
    
    public ReservationConfirmedEvent(String reservationId, String restaurantId, 
                                    String userId, String tableId) {
        super("RESERVATION_CONFIRMED");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.userId = userId;
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
    
    public String getTableId() {
        return tableId;
    }
}