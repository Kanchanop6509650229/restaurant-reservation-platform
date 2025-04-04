package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

public class FindAvailableTableResponseEvent extends BaseEvent {
    
    private String reservationId;
    private String restaurantId;
    private String tableId;
    private boolean success;
    private String errorMessage;
    private String correlationId;

    public FindAvailableTableResponseEvent() {
        super("FIND_AVAILABLE_TABLE_RESPONSE");
    }

    public FindAvailableTableResponseEvent(String reservationId, String restaurantId, 
                                         String tableId, boolean success, 
                                         String errorMessage, String correlationId) {
        super("FIND_AVAILABLE_TABLE_RESPONSE");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.success = success;
        this.errorMessage = errorMessage;
        this.correlationId = correlationId;
    }
    
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
    
    public String getTableId() {
        return tableId;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public boolean isSuccess() {
        return success;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
}