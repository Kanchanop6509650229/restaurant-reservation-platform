package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

/**
 * Event representing the cancellation of a restaurant reservation.
 * This event is published when a reservation is cancelled, containing
 * details about the cancellation including the reason and previous status.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class ReservationCancelledEvent extends BaseEvent implements ReservationEvent {
    
    /** Unique identifier of the cancelled reservation */
    private final String reservationId;
    
    /** ID of the restaurant where the reservation was made */
    private final String restaurantId;
    
    /** ID of the user who cancelled the reservation */
    private final String userId;
    
    /** Status of the reservation before cancellation */
    private final String previousStatus;
    
    /** Reason for cancelling the reservation */
    private final String reason;
    
    /**
     * Creates a new reservation cancelled event with all required details.
     *
     * @param reservationId Unique identifier of the reservation
     * @param restaurantId ID of the restaurant
     * @param userId ID of the user cancelling the reservation
     * @param previousStatus Status of the reservation before cancellation
     * @param reason Reason for cancelling the reservation
     */
    public ReservationCancelledEvent(String reservationId, String restaurantId, 
                                    String userId, String previousStatus, String reason) {
        super("RESERVATION_CANCELLED");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.previousStatus = previousStatus;
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
     * Gets the ID of the restaurant where the reservation was made.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Gets the ID of the user who cancelled the reservation.
     *
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * Gets the status of the reservation before it was cancelled.
     *
     * @return The previous status
     */
    public String getPreviousStatus() {
        return previousStatus;
    }
    
    /**
     * Gets the reason for cancelling the reservation.
     *
     * @return The cancellation reason
     */
    public String getReason() {
        return reason;
    }
}