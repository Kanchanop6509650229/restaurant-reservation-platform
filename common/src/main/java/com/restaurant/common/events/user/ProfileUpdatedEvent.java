package com.restaurant.common.events.user;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents an update to a user's profile information.
 * This event is published whenever a user modifies their profile details,
 * capturing both the old and new values for auditing and synchronization purposes.
 * Extends BaseEvent with type "PROFILE_UPDATED" and implements UserEvent interface.
 */
public class ProfileUpdatedEvent extends BaseEvent implements UserEvent {
    /**
     * The unique identifier of the user whose profile was updated.
     */
    private final String userId;
    
    /**
     * The name of the field that was updated in the user's profile.
     * For example: "email", "phoneNumber", "address", etc.
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
     * Constructs a new ProfileUpdatedEvent with details about the profile update.
     *
     * @param userId       The ID of the user whose profile was updated
     * @param fieldUpdated The name of the field that was modified
     * @param oldValue    The previous value of the field
     * @param newValue    The new value of the field
     */
    public ProfileUpdatedEvent(String userId, String fieldUpdated, String oldValue, String newValue) {
        super("PROFILE_UPDATED");
        this.userId = userId;
        this.fieldUpdated = fieldUpdated;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserId() {
        return userId;
    }
    
    /**
     * Gets the name of the field that was updated in the user's profile.
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