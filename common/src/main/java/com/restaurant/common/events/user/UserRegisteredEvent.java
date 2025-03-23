package com.restaurant.common.events.user;

import com.restaurant.common.events.BaseEvent;

public class UserRegisteredEvent extends BaseEvent implements UserEvent {
    private final String userId;
    private final String username;
    private final String email;
    
    public UserRegisteredEvent(String userId, String username, String email) {
        super("USER_REGISTERED");
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
    
    @Override
    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
}