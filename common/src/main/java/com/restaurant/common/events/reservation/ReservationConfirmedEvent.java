package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

/**
 * Event representing the confirmation of a restaurant reservation.
 * This event is published when a reservation is confirmed by the restaurant,
 * indicating that the table has been successfully allocated.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class ReservationConfirmedEvent extends BaseEvent implements ReservationEvent {
    
    /** Unique identifier of the confirmed reservation */
    private final String reservationId;
    
    /** ID of the restaurant where the reservation was made */
    private final String restaurantId;
    
    /** ID of the user who made the reservation */
    private final String userId;
    
    /** ID of the table assigned to the reservation */
    private final String tableId;
    
    /**
     * Creates a new reservation confirmed event with all required details.
     *
     * @param reservationId Unique identifier of the reservation
     * @param restaurantId ID of the restaurant
     * @param userId ID of the user who made the reservation
     * @param tableId ID of the assigned table
     */
    public ReservationConfirmedEvent(String reservationId, String restaurantId, 
                                    String userId, String tableId) {
        super("RESERVATION_CONFIRMED");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.tableId = tableId;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getReservationId() {
        return reservationId;
    }
    
    /**
     * Gets the ID of the restaurant where the reservation was made.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Gets the ID of the user who made the reservation.
     *
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * Gets the ID of the table assigned to the reservation.
     *
     * @return The table ID
     */
    public String getTableId() {
        return tableId;
    }
}