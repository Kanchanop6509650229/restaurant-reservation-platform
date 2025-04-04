package com.restaurant.reservation.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.restaurant.common.constants.ErrorCodes;
import com.restaurant.common.exceptions.BaseException;

public class ReservationConflictException extends BaseException {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    
    public ReservationConflictException(String message) {
        super(message, ErrorCodes.RESERVATION_CONFLICT);
    }
    
    public static ReservationConflictException timeConflict(LocalDateTime reservationTime) {
        String formattedDate = reservationTime.format(DATE_FORMATTER);
        String formattedTime = reservationTime.format(TIME_FORMATTER);
        
        return new ReservationConflictException(
            String.format("There is already a reservation at %s on %s. Please select a different time.", 
                formattedTime, formattedDate)
        );
    }
    
    public static ReservationConflictException tableAlreadyReserved(String tableId, LocalDateTime startTime, LocalDateTime endTime) {
        String formattedDate = startTime.format(DATE_FORMATTER);
        String formattedStartTime = startTime.format(TIME_FORMATTER);
        String formattedEndTime = endTime.format(TIME_FORMATTER);
        
        return new ReservationConflictException(
            String.format("The requested table is already reserved between %s and %s on %s. Please select a different time.", 
                formattedStartTime, formattedEndTime, formattedDate)
        );
    }
    
    public static ReservationConflictException pastDeadline(LocalDateTime deadline) {
        String formattedDeadline = deadline.format(DateTimeFormatter.ofPattern("h:mm a 'on' EEEE, MMMM d, yyyy"));
        
        return new ReservationConflictException(
            String.format("The confirmation deadline (%s) has passed. Please create a new reservation.", formattedDeadline)
        );
    }
}