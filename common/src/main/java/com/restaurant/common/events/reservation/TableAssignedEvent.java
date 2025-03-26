package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

public class TableAssignedEvent extends BaseEvent implements ReservationEvent {
    
    private final String reservationId;
    private final String restaurantId;
    private final String tableId;
    private final String reservationTime;
    
    public TableAssignedEvent(String reservationId, String restaurantId, 
                             String tableId, String reservationTime) {
        super("TABLE_ASSIGNED");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.reservationTime = reservationTime;
    }
    
    @Override
    public String getReservationId() {
        return reservationId;
    }
    
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public String getTableId() {
        return tableId;
    }
    
    public String getReservationTime() {
        return reservationTime;
    }
}