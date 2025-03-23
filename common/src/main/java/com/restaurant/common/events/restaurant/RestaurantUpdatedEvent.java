package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

public class RestaurantUpdatedEvent extends BaseEvent implements RestaurantEvent {
    private final String restaurantId;
    private final String fieldUpdated;
    private final String oldValue;
    private final String newValue;
    
    public RestaurantUpdatedEvent(String restaurantId, String fieldUpdated, String oldValue, String newValue) {
        super("RESTAURANT_UPDATED");
        this.restaurantId = restaurantId;
        this.fieldUpdated = fieldUpdated;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public String getFieldUpdated() {
        return fieldUpdated;
    }
    
    public String getOldValue() {
        return oldValue;
    }
    
    public String getNewValue() {
        return newValue;
    }
}