package com.restaurant.common.events.reservation;

/**
 * Base interface for all reservation-related events.
 * This interface defines the common contract for events that are related to
 * restaurant reservations, ensuring that all reservation events can provide
 * their associated reservation ID.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public interface ReservationEvent {
    /**
     * Gets the unique identifier of the reservation associated with this event.
     *
     * @return The reservation ID
     */
    String getReservationId();
}