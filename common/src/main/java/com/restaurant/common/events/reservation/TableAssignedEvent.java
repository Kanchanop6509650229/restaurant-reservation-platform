package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

/**
 * Event representing the assignment of a table to a reservation.
 * This event is published when a specific table is allocated to a reservation,
 * indicating that the table has been reserved for the specified time.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class TableAssignedEvent extends BaseEvent implements ReservationEvent {
    
    /** Unique identifier of the reservation */
    private final String reservationId;
    
    /** ID of the restaurant where the table is located */
    private final String restaurantId;
    
    /** ID of the table that was assigned */
    private final String tableId;
    
    /** Time for which the table is reserved */
    private final String reservationTime;
    
    /**
     * Creates a new table assigned event with all required details.
     *
     * @param reservationId Unique identifier of the reservation
     * @param restaurantId ID of the restaurant
     * @param tableId ID of the assigned table
     * @param reservationTime Time for which the table is reserved
     */
    public TableAssignedEvent(String reservationId, String restaurantId, 
                             String tableId, String reservationTime) {
        super("TABLE_ASSIGNED");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.reservationTime = reservationTime;
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
     * Gets the ID of the table that was assigned.
     *
     * @return The table ID
     */
    public String getTableId() {
        return tableId;
    }
    
    /**
     * Gets the time for which the table is reserved.
     *
     * @return The reservation time
     */
    public String getReservationTime() {
        return reservationTime;
    }
}