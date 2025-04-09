package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents a change in a restaurant's seating capacity.
 * This event is published whenever a restaurant's total capacity is modified,
 * which could be due to various reasons such as table layout changes,
 * temporary restrictions, or seasonal adjustments.
 * Extends BaseEvent with type "CAPACITY_CHANGED" and implements RestaurantEvent interface.
 */
public class CapacityChangedEvent extends BaseEvent implements RestaurantEvent {
    /**
     * The unique identifier of the restaurant whose capacity changed.
     */
    private final String restaurantId;
    
    /**
     * The previous total seating capacity of the restaurant before the change.
     */
    private final int oldCapacity;
    
    /**
     * The new total seating capacity of the restaurant after the change.
     */
    private final int newCapacity;
    
    /**
     * A description explaining why the capacity was changed.
     * For example: "Added outdoor seating", "Temporary COVID-19 restrictions", etc.
     */
    private final String reason;
    
    /**
     * Constructs a new CapacityChangedEvent with details about the capacity change.
     *
     * @param restaurantId The ID of the restaurant whose capacity changed
     * @param oldCapacity The previous total seating capacity
     * @param newCapacity The new total seating capacity
     * @param reason     A description of why the capacity was changed
     */
    public CapacityChangedEvent(String restaurantId, int oldCapacity, int newCapacity, String reason) {
        super("CAPACITY_CHANGED");
        this.restaurantId = restaurantId;
        this.oldCapacity = oldCapacity;
        this.newCapacity = newCapacity;
        this.reason = reason;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Gets the previous total seating capacity before the change.
     *
     * @return The old capacity as an integer
     */
    public int getOldCapacity() {
        return oldCapacity;
    }
    
    /**
     * Gets the new total seating capacity after the change.
     *
     * @return The new capacity as an integer
     */
    public int getNewCapacity() {
        return newCapacity;
    }
    
    /**
     * Gets the reason for the capacity change.
     *
     * @return A description explaining why the capacity was changed
     */
    public String getReason() {
        return reason;
    }
}