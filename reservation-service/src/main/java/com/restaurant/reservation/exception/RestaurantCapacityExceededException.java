package com.restaurant.reservation.exception;

import com.restaurant.common.constants.ErrorCodes;
import com.restaurant.common.exceptions.BaseException;

/**
 * Exception thrown when a restaurant's capacity is exceeded.
 * This can happen when trying to make a reservation when the restaurant is already fully booked,
 * or when the requested party size exceeds the available capacity.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class RestaurantCapacityExceededException extends BaseException {

    private static final long serialVersionUID = 1L;
    
    private final String restaurantId;
    private final String date;
    private final String time;
    private final int requestedSize;
    private final int availableCapacity;

    /**
     * Constructs a new RestaurantCapacityExceededException with the specified detail message.
     * 
     * @param message The detail message
     * @param restaurantId The ID of the restaurant
     * @param date The date of the reservation
     * @param time The time of the reservation
     * @param requestedSize The requested party size
     * @param availableCapacity The available capacity
     */
    public RestaurantCapacityExceededException(String message, String restaurantId, String date, String time, 
            int requestedSize, int availableCapacity) {
        super(message, ErrorCodes.RESTAURANT_FULLY_BOOKED);
        this.restaurantId = restaurantId;
        this.date = date;
        this.time = time;
        this.requestedSize = requestedSize;
        this.availableCapacity = availableCapacity;
    }

    /**
     * Constructs a new RestaurantCapacityExceededException with a formatted message.
     * 
     * @param restaurantId The ID of the restaurant
     * @param date The date of the reservation
     * @param time The time of the reservation
     * @param requestedSize The requested party size
     * @param availableCapacity The available capacity
     * @return A new RestaurantCapacityExceededException with a formatted message
     */
    public static RestaurantCapacityExceededException forRestaurant(String restaurantId, String date, String time, 
            int requestedSize, int availableCapacity) {
        String message;
        if (availableCapacity <= 0) {
            message = String.format("Restaurant %s is fully booked on %s at %s", restaurantId, date, time);
        } else {
            message = String.format("Restaurant %s has only %d seats available on %s at %s, but %d were requested", 
                    restaurantId, availableCapacity, date, time, requestedSize);
        }
        return new RestaurantCapacityExceededException(message, restaurantId, date, time, requestedSize, availableCapacity);
    }

    /**
     * Gets the ID of the restaurant.
     * 
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Gets the date of the reservation.
     * 
     * @return The date
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the time of the reservation.
     * 
     * @return The time
     */
    public String getTime() {
        return time;
    }

    /**
     * Gets the requested party size.
     * 
     * @return The requested party size
     */
    public int getRequestedSize() {
        return requestedSize;
    }

    /**
     * Gets the available capacity.
     * 
     * @return The available capacity
     */
    public int getAvailableCapacity() {
        return availableCapacity;
    }
}
