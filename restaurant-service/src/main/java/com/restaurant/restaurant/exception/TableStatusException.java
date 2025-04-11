package com.restaurant.restaurant.exception;

import com.restaurant.common.constants.ErrorCodes;
import com.restaurant.common.exceptions.BaseException;

/**
 * Exception class for handling table status-related errors.
 * This class provides:
 * - Table availability validation
 * - Status transition validation
 * - Reservation conflict handling
 * - Maintenance status tracking
 * 
 * Used to handle and communicate table status-related errors
 * throughout the application.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class TableStatusException extends BaseException {
    
    /**
     * Constructs a new TableStatusException with the specified message.
     * Uses a default error code of "TABLE_STATUS_ERROR".
     *
     * @param message The detail message describing the error
     */
    public TableStatusException(String message) {
        super(message, "TABLE_STATUS_ERROR");
    }
    
    /**
     * Constructs a new TableStatusException with the specified message and error code.
     *
     * @param message The detail message describing the error
     * @param errorCode The specific error code for this exception
     */
    public TableStatusException(String message, String errorCode) {
        super(message, errorCode);
    }
    
    /**
     * Creates an exception for when a table is not available.
     * This could be due to being occupied, reserved, or under maintenance.
     *
     * @param tableNumber The number of the unavailable table
     * @return A new TableStatusException with appropriate message and error code
     */
    public static TableStatusException tableNotAvailable(String tableNumber) {
        return new TableStatusException(
            String.format("Table %s is not available. It may be occupied, reserved, or under maintenance.", tableNumber),
            ErrorCodes.TABLE_NOT_AVAILABLE
        );
    }
    
    /**
     * Creates an exception for invalid table status transitions.
     * For example, trying to change from "OCCUPIED" to "AVAILABLE" without proper steps.
     *
     * @param tableNumber The number of the table
     * @param currentStatus The current status of the table
     * @param newStatus The attempted new status
     * @return A new TableStatusException with appropriate message
     */
    public static TableStatusException invalidStatusTransition(String tableNumber, String currentStatus, String newStatus) {
        return new TableStatusException(
            String.format("Cannot change table %s status from '%s' to '%s'. This is an invalid status transition.", 
                tableNumber, currentStatus, newStatus)
        );
    }
    
    /**
     * Creates an exception for when a table is already reserved.
     *
     * @param tableNumber The number of the reserved table
     * @return A new TableStatusException with appropriate message and error code
     */
    public static TableStatusException tableReserved(String tableNumber) {
        return new TableStatusException(
            String.format("Table %s is already reserved for another party.", tableNumber),
            ErrorCodes.RESERVATION_CONFLICT
        );
    }
    
    /**
     * Creates an exception for when a table is under maintenance.
     *
     * @param tableNumber The number of the table under maintenance
     * @return A new TableStatusException with appropriate message and error code
     */
    public static TableStatusException tableInMaintenance(String tableNumber) {
        return new TableStatusException(
            String.format("Table %s is currently under maintenance and unavailable for reservations.", tableNumber),
            ErrorCodes.TABLE_NOT_AVAILABLE
        );
    }
    
    /**
     * Creates an exception for when a table is currently occupied.
     *
     * @param tableNumber The number of the occupied table
     * @return A new TableStatusException with appropriate message and error code
     */
    public static TableStatusException tableOccupied(String tableNumber) {
        return new TableStatusException(
            String.format("Table %s is currently occupied.", tableNumber),
            ErrorCodes.TABLE_NOT_AVAILABLE
        );
    }
}