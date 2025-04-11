package com.restaurant.reservation.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for updating restaurant schedule information.
 * This class represents the request payload for modifying an existing schedule,
 * containing only the fields that can be updated by the client.
 *
 * This class includes validation constraints to ensure that:
 * - Time values are properly formatted
 * - Text fields don't exceed maximum lengths
 * - Numeric values are within acceptable ranges
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
public class ScheduleUpdateRequest {

    /** Flag indicating if the restaurant should be marked as closed */
    private boolean closed;

    /** New opening time for the restaurant */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime openTime;

    /** New closing time for the restaurant */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime closeTime;

    /** Description of special hours or events (e.g., "Holiday Hours", "Special Event") */
    @Size(max = 200, message = "Special hours description cannot exceed 200 characters")
    private String specialHoursDescription;

    /** New total seating capacity for the restaurant */
    @Min(value = 1, message = "Total capacity must be at least 1")
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

    @Override
    public String toString() {
        return "ScheduleUpdateRequest{" +
               "closed=" + closed +
               ", openTime=" + openTime +
               ", closeTime=" + closeTime +
               ", specialHoursDescription='" + specialHoursDescription + '\'' +
               ", totalCapacity=" + totalCapacity +
               '}';
    }
}