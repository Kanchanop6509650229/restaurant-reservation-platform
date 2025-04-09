package com.restaurant.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base class for all events in the system.
 * This class provides common properties and functionality for all event types,
 * including event identification, timing, and type information.
 * 
 * @author Restaurant Team
 * @version 1.0
 */
public abstract class BaseEvent {
    /** Unique identifier for the event */
    private String eventId;

    /** Timestamp when the event occurred */
    private LocalDateTime eventTime;

    /** Type of the event */
    private String eventType;

    /**
     * Default constructor for JPA and deserialization.
     * Initializes all fields to null.
     */
    protected BaseEvent() {
        this.eventId = null;
        this.eventTime = null;
        this.eventType = null;
    }
    
    /**
     * Creates a new event with the specified type.
     * Automatically generates a unique event ID and sets the current time.
     *
     * @param eventType The type of the event
     */
    protected BaseEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventTime = LocalDateTime.now();
        this.eventType = eventType;
    }

    /**
     * Sets the unique identifier for this event.
     *
     * @param eventId The event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Sets the timestamp when this event occurred.
     *
     * @param eventTime The event time to set
     */
    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }
    
    /**
     * Sets the type of this event.
     *
     * @param eventType The event type to set
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    /**
     * Gets the unique identifier of this event.
     *
     * @return The event ID
     */
    public String getEventId() {
        return eventId;
    }
    
    /**
     * Gets the timestamp when this event occurred.
     *
     * @return The event time
     */
    public LocalDateTime getEventTime() {
        return eventTime;
    }
    
    /**
     * Gets the type of this event.
     *
     * @return The event type
     */
    public String getEventType() {
        return eventType;
    }
}