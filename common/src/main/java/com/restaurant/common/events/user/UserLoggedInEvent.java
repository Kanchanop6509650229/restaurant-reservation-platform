package com.restaurant.common.events.user;

import java.time.LocalDateTime;

import com.restaurant.common.events.BaseEvent;

public class UserLoggedInEvent extends BaseEvent implements UserEvent {
    private final String userId;
    private final String username;
    private final LocalDateTime loginTime;
    private final String loginIp;
    
    public UserLoggedInEvent(String userId, String username, String loginIp) {
        super("USER_LOGGED_IN");
        this.userId = userId;
        this.username = username;
        this.loginTime = LocalDateTime.now();
        this.loginIp = loginIp;
    }
    
    @Override
    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public LocalDateTime getLoginTime() {
        return loginTime;
    }
    
    public String getLoginIp() {
        return loginIp;
    }
}
