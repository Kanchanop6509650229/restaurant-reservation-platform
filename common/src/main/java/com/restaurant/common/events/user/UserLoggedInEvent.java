package com.restaurant.common.events.user;

import java.time.LocalDateTime;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents a user login event in the system.
 * This event is published whenever a user successfully logs into the system,
 * capturing details such as the login time and IP address for security and auditing purposes.
 * Extends BaseEvent with type "USER_LOGGED_IN" and implements UserEvent interface.
 */
public class UserLoggedInEvent extends BaseEvent implements UserEvent {
    /**
     * The unique identifier of the user who logged in.
     */
    private final String userId;
    
    /**
     * The username of the user who logged in.
     */
    private final String username;
    
    /**
     * The exact date and time when the login occurred.
     * This is automatically set to the current time when the event is created.
     */
    private final LocalDateTime loginTime;
    
    /**
     * The IP address from which the user logged in.
     * This is used for security monitoring and audit trails.
     */
    private final String loginIp;
    
    /**
     * Constructs a new UserLoggedInEvent with the user's login details.
     *
     * @param userId   The unique identifier of the user
     * @param username The username of the user
     * @param loginIp  The IP address from which the user logged in
     */
    public UserLoggedInEvent(String userId, String username, String loginIp) {
        super("USER_LOGGED_IN");
        this.userId = userId;
        this.username = username;
        this.loginTime = LocalDateTime.now();
        this.loginIp = loginIp;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserId() {
        return userId;
    }
    
    /**
     * Gets the username of the user who logged in.
     *
     * @return The username as a String
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the exact date and time when the login occurred.
     *
     * @return The login time as a LocalDateTime
     */
    public LocalDateTime getLoginTime() {
        return loginTime;
    }
    
    /**
     * Gets the IP address from which the user logged in.
     *
     * @return The login IP address as a String
     */
    public String getLoginIp() {
        return loginIp;
    }
}
