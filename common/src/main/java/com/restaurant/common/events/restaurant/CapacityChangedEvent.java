package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

public class CapacityChangedEvent extends BaseEvent implements RestaurantEvent {
    private final String restaurantId;
    private final int oldCapacity;
    private final int newCapacity;
    private final String reason;
    
    public CapacityChangedEvent(String restaurantId, int oldCapacity, int newCapacity, String reason) {
        super("CAPACITY_CHANGED");
        this.restaurantId = restaurantId;
        this.oldCapacity = oldCapacity;
        this.newCapacity = newCapacity;
        this.reason = reason;
    }
    
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public int getOldCapacity() {
        return oldCapacity;
    }
    
    public int getNewCapacity() {
        return newCapacity;
    }
    
    public String getReason() {
        return reason;
    }
}