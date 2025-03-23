package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

public class TableStatusChangedEvent extends BaseEvent implements RestaurantEvent {
    private final String restaurantId;
    private final String tableId;
    private final String oldStatus;
    private final String newStatus;
    private final String reservationId;
    
    public TableStatusChangedEvent(String restaurantId, String tableId, String oldStatus, String newStatus, String reservationId) {
        super("TABLE_STATUS_CHANGED");
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.reservationId = reservationId;
    }
    
    @Override
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
    
    public String getReservationId() {
        return reservationId;
    }
}