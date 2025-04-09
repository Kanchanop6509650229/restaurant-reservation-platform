package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

/**
 * Event representing the creation of a new restaurant reservation.
 * This event is published when a customer successfully creates a new reservation,
 * containing all the essential details of the reservation.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class ReservationCreatedEvent extends BaseEvent implements ReservationEvent {
    
    /** Unique identifier of the reservation */
    private final String reservationId;
    
    /** ID of the restaurant where the reservation is made */
    private final String restaurantId;
    
    /** ID of the user who made the reservation */
    private final String userId;
    
    /** Scheduled time of the reservation */
    private final String reservationTime;
    
    /** Number of people in the party */
    private final int partySize;
    
    /** ID of the table assigned for the reservation */
    private final String tableId;
    
    /**
     * Creates a new reservation created event with all required details.
     *
     * @param reservationId Unique identifier of the reservation
     * @param restaurantId ID of the restaurant
     * @param userId ID of the user making the reservation
     * @param reservationTime Scheduled time of the reservation
     * @param partySize Number of people in the party
     * @param tableId ID of the assigned table
     */
    public ReservationCreatedEvent(String reservationId, String restaurantId, String userId,
                                  String reservationTime, int partySize, String tableId) {
        super("RESERVATION_CREATED");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.reservationTime = reservationTime;
        this.partySize = partySize;
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
     * Gets the ID of the restaurant where the reservation is made.
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
     * Gets the scheduled time of the reservation.
     *
     * @return The reservation time
     */
    public String getReservationTime() {
        return reservationTime;
    }
    
    /**
     * Gets the number of people in the party.
     *
     * @return The party size
     */
    public int getPartySize() {
        return partySize;
    }
    
    /**
     * Gets the ID of the table assigned for the reservation.
     *
     * @return The table ID
     */
    public String getTableId() {
        return tableId;
    }
}