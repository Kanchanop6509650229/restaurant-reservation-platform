package com.restaurant.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseEvent {
    private final String eventId;
    private final LocalDateTime eventTime;
    private final String eventType;
    
    protected BaseEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventTime = LocalDateTime.now();
        this.eventType = eventType;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public LocalDateTime getEventTime() {
        return eventTime;
    }
    
    public String getEventType() {
        return eventType;
    }
}