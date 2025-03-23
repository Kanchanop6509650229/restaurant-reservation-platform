package com.restaurant.common.events.user;

import com.restaurant.common.events.BaseEvent;

public class ProfileUpdatedEvent extends BaseEvent implements UserEvent {
    private final String userId;
    private final String fieldUpdated;
    private final String oldValue;
    private final String newValue;
    
    public ProfileUpdatedEvent(String userId, String fieldUpdated, String oldValue, String newValue) {
        super("PROFILE_UPDATED");
        this.userId = userId;
        this.fieldUpdated = fieldUpdated;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    @Override
    public String getUserId() {
        return userId;
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