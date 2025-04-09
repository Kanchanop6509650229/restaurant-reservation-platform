package com.restaurant.reservation.dto;

import java.time.LocalTime;

/**
 * Data Transfer Object (DTO) for updating restaurant schedule information.
 * This class represents the request payload for modifying an existing schedule,
 * containing only the fields that can be updated by the client.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class ScheduleUpdateRequest {

    /** Flag indicating if the restaurant should be marked as closed */
    private boolean closed;

    /** New opening time for the restaurant */
    private LocalTime openTime;

    /** New closing time for the restaurant */
    private LocalTime closeTime;

    /** Description of special hours or events (e.g., "Holiday Hours", "Special Event") */
    private String specialHoursDescription;

    /** New total seating capacity for the restaurant */
    private int totalCapacity;

    /**
     * Checks if the restaurant should be marked as closed.
     *
     * @return true if the restaurant should be closed, false otherwise
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Sets whether the restaurant should be marked as closed.
     *
     * @param closed true to mark the restaurant as closed, false otherwise
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Gets the new opening time for the restaurant.
     *
     * @return The opening time to be set
     */
    public LocalTime getOpenTime() {
        return openTime;
    }

    /**
     * Sets the new opening time for the restaurant.
     *
     * @param openTime The opening time to set
     */
    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    /**
     * Gets the new closing time for the restaurant.
     *
     * @return The closing time to be set
     */
    public LocalTime getCloseTime() {
        return closeTime;
    }

    /**
     * Sets the new closing time for the restaurant.
     *
     * @param closeTime The closing time to set
     */
    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    /**
     * Gets the description of special hours or events.
     *
     * @return The special hours description
     */
    public String getSpecialHoursDescription() {
        return specialHoursDescription;
    }

    /**
     * Sets the description of special hours or events.
     *
     * @param specialHoursDescription The description to set (e.g., "Holiday Hours", "Special Event")
     */
    public void setSpecialHoursDescription(String specialHoursDescription) {
        this.specialHoursDescription = specialHoursDescription;
    }

    /**
     * Gets the new total seating capacity for the restaurant.
     *
     * @return The total capacity to be set
     */
    public int getTotalCapacity() {
        return totalCapacity;
    }

    /**
     * Sets the new total seating capacity for the restaurant.
     *
     * @param totalCapacity The total capacity to set
     */
    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }
}