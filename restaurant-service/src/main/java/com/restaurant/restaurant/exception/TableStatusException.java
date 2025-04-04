package com.restaurant.restaurant.exception;

import com.restaurant.common.constants.ErrorCodes;
import com.restaurant.common.exceptions.BaseException;

public class TableStatusException extends BaseException {
    
    public TableStatusException(String message) {
        super(message, "TABLE_STATUS_ERROR");
    }
    
    public TableStatusException(String message, String errorCode) {
        super(message, errorCode);
    }
    
    public static TableStatusException tableNotAvailable(String tableNumber) {
        return new TableStatusException(
            String.format("Table %s is not available. It may be occupied, reserved, or under maintenance.", tableNumber),
            ErrorCodes.TABLE_NOT_AVAILABLE
        );
    }
    
    public static TableStatusException invalidStatusTransition(String tableNumber, String currentStatus, String newStatus) {
        return new TableStatusException(
            String.format("Cannot change table %s status from '%s' to '%s'. This is an invalid status transition.", 
                tableNumber, currentStatus, newStatus)
        );
    }
    
    public static TableStatusException tableReserved(String tableNumber) {
        return new TableStatusException(
            String.format("Table %s is already reserved for another party.", tableNumber),
            ErrorCodes.RESERVATION_CONFLICT
        );
    }
    
    public static TableStatusException tableInMaintenance(String tableNumber) {
        return new TableStatusException(
            String.format("Table %s is currently under maintenance and unavailable for reservations.", tableNumber),
            ErrorCodes.TABLE_NOT_AVAILABLE
        );
    }
    
    public static TableStatusException tableOccupied(String tableNumber) {
        return new TableStatusException(
            String.format("Table %s is currently occupied.", tableNumber),
            ErrorCodes.TABLE_NOT_AVAILABLE
        );
    }
}