package com.restaurant.common.events.restaurant;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents an update to a restaurant's information.
 * This event is published whenever a field in a restaurant's data is modified,
 * capturing both the old and new values for auditing and synchronization purposes.
 * Extends BaseEvent with type "RESTAURANT_UPDATED" and implements RestaurantEvent interface.
 */
public class RestaurantUpdatedEvent extends BaseEvent implements RestaurantEvent {
    /**
     * The unique identifier of the restaurant that was updated.
     */
    private final String restaurantId;
    
    /**
     * The name of the field that was updated in the restaurant.
     * For example: "name", "address", "phoneNumber", etc.
     */
    private final String fieldUpdated;
    
    /**
     * The previous value of the updated field before the change.
     */
    private final String oldValue;
    
    /**
     * The new value of the updated field after the change.
     */
    private final String newValue;
    
    /**
     * Constructs a new RestaurantUpdatedEvent with details about the update.
     *
     * @param restaurantId  The ID of the restaurant that was updated
     * @param fieldUpdated  The name of the field that was modified
     * @param oldValue     The previous value of the field
     * @param newValue     The new value of the field
     */
    public RestaurantUpdatedEvent(String restaurantId, String fieldUpdated, String oldValue, String newValue) {
        super("RESTAURANT_UPDATED");
        this.restaurantId = restaurantId;
        this.fieldUpdated = fieldUpdated;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Gets the name of the field that was updated.
     *
     * @return The name of the updated field
     */
    public String getFieldUpdated() {
        return fieldUpdated;
    }
    
    /**
     * Gets the previous value of the updated field.
     *
     * @return The field's value before the update
     */
    public String getOldValue() {
        return oldValue;
    }
    
    /**
     * Gets the new value of the updated field.
     *
     * @return The field's value after the update
     */
    public String getNewValue() {
        return newValue;
    }
}