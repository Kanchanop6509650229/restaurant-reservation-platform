package com.restaurant.restaurant.exception;

import com.restaurant.common.constants.ErrorCodes;
import com.restaurant.common.exceptions.BaseException;

/**
 * Exception class for handling restaurant capacity-related errors.
 * This class provides:
 * - Capacity availability validation
 * - Party size validation
 * - Table suitability checking
 * - Booking capacity management
 * 
 * Used to handle and communicate restaurant capacity-related errors
 * throughout the application.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class RestaurantCapacityException extends BaseException {
    
    /**
     * Constructs a new RestaurantCapacityException with the specified message.
     * Uses a default error code of "RESTAURANT_FULLY_BOOKED".
     *
     * @param message The detail message describing the error
     */
    public RestaurantCapacityException(String message) {
        super(message, ErrorCodes.RESTAURANT_FULLY_BOOKED);
    }
    
    /**
     * Creates an exception for when the restaurant has no available capacity.
     * This indicates that all tables are either occupied or reserved.
     *
     * @return A new RestaurantCapacityException with appropriate message
     */
    public static RestaurantCapacityException noAvailableCapacity() {
        return new RestaurantCapacityException(
            "The restaurant has no available capacity at this time. Please select a different time or date."
        );
    }
    
    /**
     * Creates an exception for when a party size exceeds the restaurant's maximum capacity.
     *
     * @param partySize The size of the party attempting to book
     * @param maxCapacity The maximum capacity allowed by the restaurant
     * @return A new RestaurantCapacityException with appropriate message
     */
    public static RestaurantCapacityException partyTooLarge(int partySize, int maxCapacity) {
        return new RestaurantCapacityException(
            String.format("Cannot accommodate a party of %d people. The maximum party size is %d.", partySize, maxCapacity)
        );
    }
    
    /**
     * Creates an exception for when no suitable tables are available for a party size.
     * This could be due to table combinations not being possible or all suitable tables being occupied.
     *
     * @param partySize The size of the party attempting to book
     * @return A new RestaurantCapacityException with appropriate message
     */
    public static RestaurantCapacityException noSuitableTables(int partySize) {
        return new RestaurantCapacityException(
            String.format("No suitable tables available for a party of %d. Please select a different time or modify your party size.", partySize)
        );
    }
}