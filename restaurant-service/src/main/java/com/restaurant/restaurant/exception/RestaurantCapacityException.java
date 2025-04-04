package com.restaurant.restaurant.exception;

import com.restaurant.common.constants.ErrorCodes;
import com.restaurant.common.exceptions.BaseException;

public class RestaurantCapacityException extends BaseException {
    
    public RestaurantCapacityException(String message) {
        super(message, ErrorCodes.RESTAURANT_FULLY_BOOKED);
    }
    
    public static RestaurantCapacityException noAvailableCapacity() {
        return new RestaurantCapacityException(
            "The restaurant has no available capacity at this time. Please select a different time or date."
        );
    }
    
    public static RestaurantCapacityException partyTooLarge(int partySize, int maxCapacity) {
        return new RestaurantCapacityException(
            String.format("Cannot accommodate a party of %d people. The maximum party size is %d.", partySize, maxCapacity)
        );
    }
    
    public static RestaurantCapacityException noSuitableTables(int partySize) {
        return new RestaurantCapacityException(
            String.format("No suitable tables available for a party of %d. Please select a different time or modify your party size.", partySize)
        );
    }
}