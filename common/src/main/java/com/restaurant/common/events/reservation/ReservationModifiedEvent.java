package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

/**
 * Event representing the modification of a restaurant reservation.
 * This event is published when a reservation is modified, containing
 * both the old and new values of the changed attributes.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class ReservationModifiedEvent extends BaseEvent implements ReservationEvent {
    
    /** Unique identifier of the modified reservation */
    private final String reservationId;
    
    /** ID of the restaurant where the reservation was made */
    private final String restaurantId;
    
    /** ID of the user who modified the reservation */
    private final String userId;
    
    /** Original reservation time before modification */
    private final String oldTime;
    
    /** New reservation time after modification */
    private final String newTime;
    
    /** Original party size before modification */
    private final int oldPartySize;
    
    /** New party size after modification */
    private final int newPartySize;
    
    /**
     * Creates a new reservation modified event with all required details.
     *
     * @param reservationId Unique identifier of the reservation
     * @param restaurantId ID of the restaurant
     * @param userId ID of the user modifying the reservation
     * @param oldTime Original reservation time
     * @param newTime New reservation time
     * @param oldPartySize Original party size
     * @param newPartySize New party size
     */
    public ReservationModifiedEvent(String reservationId, String restaurantId, String userId,
                                   String oldTime, String newTime, int oldPartySize, int newPartySize) {
        super("RESERVATION_MODIFIED");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.oldTime = oldTime;
        this.newTime = newTime;
        this.oldPartySize = oldPartySize;
        this.newPartySize = newPartySize;
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
     * Gets the ID of the user who modified the reservation.
     *
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * Gets the original reservation time before modification.
     *
     * @return The old reservation time
     */
    public String getOldTime() {
        return oldTime;
    }
    
    /**
     * Gets the new reservation time after modification.
     *
     * @return The new reservation time
     */
    public String getNewTime() {
        return newTime;
    }
    
    /**
     * Gets the original party size before modification.
     *
     * @return The old party size
     */
    public int getOldPartySize() {
        return oldPartySize;
    }
    
    /**
     * Gets the new party size after modification.
     *
     * @return The new party size
     */
    public int getNewPartySize() {
        return newPartySize;
    }
}