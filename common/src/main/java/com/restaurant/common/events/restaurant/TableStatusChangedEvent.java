package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents a change in a restaurant table's status.
 * This event is published whenever a table's status changes, such as from 'AVAILABLE' to 'RESERVED'
 * or from 'RESERVED' to 'OCCUPIED'. It captures the old and new status values, along with
 * relevant reservation information.
 * Extends BaseEvent with type "TABLE_STATUS_CHANGED" and implements RestaurantEvent interface.
 */
public class TableStatusChangedEvent extends BaseEvent implements RestaurantEvent {
    /**
     * The unique identifier of the restaurant where the table status changed.
     */
    private final String restaurantId;
    
    /**
     * The unique identifier of the table whose status changed.
     */
    private final String tableId;
    
    /**
     * The previous status of the table before the change.
     * Typical values include: 'AVAILABLE', 'RESERVED', 'OCCUPIED', 'OUT_OF_SERVICE'.
     */
    private final String oldStatus;
    
    /**
     * The new status of the table after the change.
     * Typical values include: 'AVAILABLE', 'RESERVED', 'OCCUPIED', 'OUT_OF_SERVICE'.
     */
    private final String newStatus;
    
    /**
     * The unique identifier of the reservation associated with this status change.
     * May be null if the status change is not related to a reservation
     * (e.g., marking a table as out of service).
     */
    private final String reservationId;
    
    /**
     * Constructs a new TableStatusChangedEvent with details about the status change.
     *
     * @param restaurantId   The ID of the restaurant where the table is located
     * @param tableId       The ID of the table whose status changed
     * @param oldStatus     The previous status of the table
     * @param newStatus     The new status of the table
     * @param reservationId The ID of the associated reservation (if any)
     */
    public TableStatusChangedEvent(String restaurantId, String tableId, String oldStatus, String newStatus, String reservationId) {
        super("TABLE_STATUS_CHANGED");
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.reservationId = reservationId;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Gets the unique identifier of the table whose status changed.
     *
     * @return The table ID
     */
    public String getTableId() {
        return tableId;
    }
    
    /**
     * Gets the previous status of the table before the change.
     *
     * @return The old table status
     */
    public String getOldStatus() {
        return oldStatus;
    }
    
    /**
     * Gets the new status of the table after the change.
     *
     * @return The new table status
     */
    public String getNewStatus() {
        return newStatus;
    }
    
    /**
     * Gets the unique identifier of the reservation associated with this status change.
     *
     * @return The reservation ID, or null if no reservation is associated with this change
     */
    public String getReservationId() {
        return reservationId;
    }
}