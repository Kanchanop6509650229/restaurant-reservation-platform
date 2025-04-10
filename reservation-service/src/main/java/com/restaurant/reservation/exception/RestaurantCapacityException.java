package com.restaurant.reservation.exception;

import com.restaurant.common.constants.ErrorCodes;
import com.restaurant.common.exceptions.BaseException;

/**
 * Exception thrown when a restaurant cannot accommodate a reservation request
 * due to capacity constraints. This includes cases where the restaurant is fully booked,
 * the party size is too large, or no suitable tables are available.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class RestaurantCapacityException extends BaseException {
    
    /**
     * Creates a new RestaurantCapacityException with the specified message.
     *
     * @param message The detail message explaining the capacity constraint
     */
    public RestaurantCapacityException(String message) {
        super(message, ErrorCodes.RESTAURANT_FULLY_BOOKED);
    }
    
    /**
     * Creates an exception indicating that the restaurant is fully booked
     * for a specific date and time.
     *
     * @param date The date that is fully booked
     * @param time The time slot that is fully booked
     * @return A new RestaurantCapacityException with a formatted message
     */
    public static RestaurantCapacityException noAvailability(String date, String time) {
        return new RestaurantCapacityException(
            String.format("The restaurant is fully booked at %s on %s. Please select a different date or time.", time, date)
        );
    }
    
    /**
     * Creates an exception indicating that the requested party size
     * exceeds the restaurant's maximum capacity.
     *
     * @param partySize The size of the party that cannot be accommodated
     * @param maxCapacity The maximum capacity of the restaurant
     * @return A new RestaurantCapacityException with a formatted message
     */
    public static RestaurantCapacityException partyTooLarge(int partySize, int maxCapacity) {
        return new RestaurantCapacityException(
            String.format("Cannot accommodate a party of %d people. The maximum party size is %d.", partySize, maxCapacity)
        );
    }
    
    /**
     * Creates an exception indicating that no suitable tables are available
     * for the requested party size.
     *
     * @param partySize The size of the party that cannot be accommodated
     * @return A new RestaurantCapacityException with a formatted message
     */
    public static RestaurantCapacityException noSuitableTables(int partySize) {
        return new RestaurantCapacityException(
            String.format("No suitable tables available for a party of %d. Please select a different time or modify your party size.", partySize)
        );
    }
}