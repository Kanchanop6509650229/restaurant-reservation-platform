package com.restaurant.common.events.user;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents a new user registration in the system.
 * This event is published whenever a new user successfully registers,
 * capturing their basic information for system integration and notification purposes.
 * Extends BaseEvent with type "USER_REGISTERED" and implements UserEvent interface.
 */
public class UserRegisteredEvent extends BaseEvent implements UserEvent {
    /**
     * The unique identifier assigned to the newly registered user.
     */
    private final String userId;
    
    /**
     * The username chosen by the user during registration.
     */
    private final String username;
    
    /**
     * The email address provided by the user during registration.
     */
    private final String email;
    
    /**
     * Constructs a new UserRegisteredEvent with the user's registration details.
     *
     * @param userId   The unique identifier assigned to the user
     * @param username The username chosen by the user
     * @param email    The email address provided by the user
     */
    public UserRegisteredEvent(String userId, String username, String email) {
        super("USER_REGISTERED");
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserId() {
        return userId;
    }
    
    /**
     * Gets the username chosen by the user during registration.
     *
     * @return The username as a String
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the email address provided by the user during registration.
     *
     * @return The email address as a String
     */
    public String getEmail() {
        return email;
    }
}