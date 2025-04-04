package com.restaurant.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseEvent {
    private String eventId;
    private LocalDateTime eventTime;
    private String eventType;

    protected BaseEvent() {
        this.eventId = null;
        this.eventTime = null;
        this.eventType = null;
    }
    
    protected BaseEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventTime = LocalDateTime.now();
        this.eventType = eventType;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }
    
    public void setEventType(String eventType) {
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