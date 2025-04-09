package com.restaurant.common.events.user;

/**
 * Base interface for all user-related events in the system.
 * This interface defines the common contract for events that are related to user operations.
 * All user events must implement this interface to ensure they provide the user ID.
 */
public interface UserEvent {
    /**
     * Retrieves the unique identifier of the user associated with this event.
     * 
     * @return The user ID as a String
     */
    String getUserId();
}