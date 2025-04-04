package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

public class TableStatusEvent extends BaseEvent implements ReservationEvent {
    
    private final String reservationId;
    private final String restaurantId;
    private final String tableId;
    private final String oldStatus;
    private final String newStatus;
    private final String reason;
    
    public TableStatusEvent(String reservationId, String restaurantId, String tableId, 
                          String oldStatus, String newStatus, String reason) {
        super("TABLE_STATUS_CHANGED");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.reason = reason;
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
    
    public String getOldStatus() {
        return oldStatus;
    }
    
    public String getNewStatus() {
        return newStatus;
    }
    
    public String getReason() {
        return reason;
    }
}