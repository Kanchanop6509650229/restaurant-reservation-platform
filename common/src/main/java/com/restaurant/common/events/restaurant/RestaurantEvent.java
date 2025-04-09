package com.restaurant.common.events.restaurant;

/**
 * Base interface for all restaurant-related events in the system.
 * This interface defines the common contract for events that are related to restaurant operations.
 * All restaurant events must implement this interface to ensure they provide the restaurant ID.
 */
public interface RestaurantEvent {
    /**
     * Retrieves the unique identifier of the restaurant associated with this event.
     * 
     * @return The restaurant ID as a String
     */
    String getRestaurantId();
}