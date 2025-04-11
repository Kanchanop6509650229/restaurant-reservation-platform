package com.restaurant.reservation.exception;

import java.time.LocalDateTime;

import com.restaurant.common.constants.ErrorCodes;
import com.restaurant.common.exceptions.BaseException;
import com.restaurant.common.utils.DateTimeUtils;

/**
 * Exception thrown when a reservation time is invalid.
 * This can happen when trying to make a reservation for a time when the restaurant is closed,
 * or when the reservation time is in the past, or when it doesn't meet other time-related constraints.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class InvalidReservationTimeException extends BaseException {

    private static final long serialVersionUID = 1L;
    
    private final LocalDateTime requestedTime;
    private final String reason;

    /**
     * Constructs a new InvalidReservationTimeException with the specified detail message.
     * 
     * @param message The detail message
     * @param requestedTime The invalid reservation time
     * @param reason The reason why the time is invalid
     */
    public InvalidReservationTimeException(String message, LocalDateTime requestedTime, String reason) {
        super(message, ErrorCodes.INVALID_RESERVATION_TIME);
        this.requestedTime = requestedTime;
        this.reason = reason;
    }

    /**
     * Constructs a new InvalidReservationTimeException for a time in the past.
     * 
     * @param requestedTime The requested reservation time
     * @return A new InvalidReservationTimeException with a formatted message
     */
    public static InvalidReservationTimeException forPastTime(LocalDateTime requestedTime) {
        String formattedTime = DateTimeUtils.formatDateTime(requestedTime);
        return new InvalidReservationTimeException(
                String.format("Cannot make a reservation for a time in the past: %s", formattedTime),
                requestedTime, "PAST_TIME");
    }
    
    /**
     * Constructs a new InvalidReservationTimeException for a time when the restaurant is closed.
     * 
     * @param requestedTime The requested reservation time
     * @param restaurantId The ID of the restaurant
     * @return A new InvalidReservationTimeException with a formatted message
     */
    public static InvalidReservationTimeException forClosedRestaurant(LocalDateTime requestedTime, String restaurantId) {
        String formattedTime = DateTimeUtils.formatDateTime(requestedTime);
        return new InvalidReservationTimeException(
                String.format("Restaurant %s is closed at the requested time: %s", restaurantId, formattedTime),
                requestedTime, "RESTAURANT_CLOSED");
    }
    
    /**
     * Constructs a new InvalidReservationTimeException for a time that doesn't meet advance notice requirements.
     * 
     * @param requestedTime The requested reservation time
     * @param requiredHours The required number of hours in advance
     * @return A new InvalidReservationTimeException with a formatted message
     */
    public static InvalidReservationTimeException forInsufficientAdvanceNotice(LocalDateTime requestedTime, int requiredHours) {
        String formattedTime = DateTimeUtils.formatDateTime(requestedTime);
        return new InvalidReservationTimeException(
                String.format("Reservations must be made at least %d hours in advance. Requested time: %s", 
                        requiredHours, formattedTime),
                requestedTime, "INSUFFICIENT_ADVANCE_NOTICE");
    }

    /**
     * Gets the requested reservation time.
     * 
     * @return The requested time
     */
    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }

    /**
     * Gets the reason why the time is invalid.
     * 
     * @return The reason
     */
    public String getReason() {
        return reason;
    }
}
