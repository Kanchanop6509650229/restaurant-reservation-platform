package com.restaurant.reservation.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.restaurant.common.constants.ErrorCodes;
import com.restaurant.common.exceptions.BaseException;

/**
 * Exception thrown when a reservation request conflicts with existing reservations
 * or violates reservation rules. This includes time conflicts, table availability,
 * and deadline violations.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class ReservationConflictException extends BaseException {
    
    /** Formatter for displaying dates in a user-friendly format (e.g., "Monday, January 1, 2024") */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    
    /** Formatter for displaying times in a user-friendly format (e.g., "2:30 PM") */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    
    /**
     * Creates a new ReservationConflictException with the specified message.
     *
     * @param message The detail message explaining the reservation conflict
     */
    public ReservationConflictException(String message) {
        super(message, ErrorCodes.RESERVATION_CONFLICT);
    }
    
    /**
     * Creates an exception indicating a time conflict for a specific reservation time.
     *
     * @param reservationTime The time that conflicts with existing reservations
     * @return A new ReservationConflictException with a formatted message
     */
    public static ReservationConflictException timeConflict(LocalDateTime reservationTime) {
        String formattedDate = reservationTime.format(DATE_FORMATTER);
        String formattedTime = reservationTime.format(TIME_FORMATTER);
        
        return new ReservationConflictException(
            String.format("There is already a reservation at %s on %s. Please select a different time.", 
                formattedTime, formattedDate)
        );
    }
    
    /**
     * Creates an exception indicating that a specific table is already reserved
     * during the requested time period.
     *
     * @param tableId The ID of the table that is already reserved
     * @param startTime The start time of the conflicting reservation
     * @param endTime The end time of the conflicting reservation
     * @return A new ReservationConflictException with a formatted message
     */
    public static ReservationConflictException tableAlreadyReserved(String tableId, LocalDateTime startTime, LocalDateTime endTime) {
        String formattedDate = startTime.format(DATE_FORMATTER);
        String formattedStartTime = startTime.format(TIME_FORMATTER);
        String formattedEndTime = endTime.format(TIME_FORMATTER);
        
        return new ReservationConflictException(
            String.format("The requested table is already reserved between %s and %s on %s. Please select a different time.", 
                formattedStartTime, formattedEndTime, formattedDate)
        );
    }
    
    /**
     * Creates an exception indicating that a reservation confirmation deadline has passed.
     *
     * @param deadline The deadline that has passed
     * @return A new ReservationConflictException with a formatted message
     */
    public static ReservationConflictException pastDeadline(LocalDateTime deadline) {
        String formattedDeadline = deadline.format(DateTimeFormatter.ofPattern("h:mm a 'on' EEEE, MMMM d, yyyy"));
        
        return new ReservationConflictException(
            String.format("The confirmation deadline (%s) has passed. Please create a new reservation.", formattedDeadline)
        );
    }
}