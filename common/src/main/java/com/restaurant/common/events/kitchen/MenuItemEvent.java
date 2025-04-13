package com.restaurant.common.events.kitchen;

/**
 * Base interface for all menu item-related events in the system.
 * This interface defines the common contract for events that are related to menu item operations.
 * All menu item events must implement this interface to ensure they provide the restaurant ID.
 * 
 * @author Restaurant Team
 * @version 1.0
 */
public interface MenuItemEvent {
    /**
     * Retrieves the unique identifier of the restaurant associated with this menu item event.
     * 
     * @return The restaurant ID as a String
     */
    String getRestaurantId();
}
