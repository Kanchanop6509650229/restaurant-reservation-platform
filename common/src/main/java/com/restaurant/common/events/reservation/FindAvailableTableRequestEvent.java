package com.restaurant.common.events.reservation;

import java.time.LocalDateTime;

import com.restaurant.common.events.BaseEvent;

public class FindAvailableTableRequestEvent extends BaseEvent {

    private String reservationId;
    private String restaurantId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int partySize;
    private String correlationId;

    public FindAvailableTableRequestEvent() {
        super("FIND_AVAILABLE_TABLE_REQUEST");
    }

    public FindAvailableTableRequestEvent(String reservationId, String restaurantId,
            LocalDateTime startTime, LocalDateTime endTime,
            int partySize, String correlationId) {
        super("FIND_AVAILABLE_TABLE_REQUEST");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.partySize = partySize;
        this.correlationId = correlationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getPartySize() {
        return partySize;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}