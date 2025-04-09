package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

/**
 * Event representing a change in a table's status.
 * This event is published when a table's status changes (e.g., from AVAILABLE to OCCUPIED),
 * including the reason for the change and both old and new status values.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class TableStatusEvent extends BaseEvent implements ReservationEvent {
    
    /** Unique identifier of the associated reservation */
    private final String reservationId;
    
    /** ID of the restaurant where the table is located */
    private final String restaurantId;
    
    /** ID of the table whose status changed */
    private final String tableId;
    
    /** Previous status of the table */
    private final String oldStatus;
    
    /** New status of the table */
    private final String newStatus;
    
    /** Reason for the status change */
    private final String reason;
    
    /**
     * Creates a new table status event with all required details.
     *
     * @param reservationId Unique identifier of the reservation
     * @param restaurantId ID of the restaurant
     * @param tableId ID of the table
     * @param oldStatus Previous status of the table
     * @param newStatus New status of the table
     * @param reason Reason for the status change
     */
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getReservationId() {
        return reservationId;
    }
    
    /**
     * Gets the ID of the restaurant where the table is located.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Gets the ID of the table whose status changed.
     *
     * @return The table ID
     */
    public String getTableId() {
        return tableId;
    }
    
    /**
     * Gets the previous status of the table.
     *
     * @return The old status
     */
    public String getOldStatus() {
        return oldStatus;
    }
    
    /**
     * Gets the new status of the table.
     *
     * @return The new status
     */
    public String getNewStatus() {
        return newStatus;
    }
    
    /**
     * Gets the reason for the status change.
     *
     * @return The reason for the change
     */
    public String getReason() {
        return reason;
    }
}