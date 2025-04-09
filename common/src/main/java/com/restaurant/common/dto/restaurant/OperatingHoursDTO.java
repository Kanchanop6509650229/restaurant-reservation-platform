package com.restaurant.common.dto.restaurant;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Data Transfer Object for restaurant operating hours.
 * This class represents the operating hours for a specific day of the week,
 * including opening and closing times, and whether the restaurant is closed.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class OperatingHoursDTO {
    /** Unique identifier for the operating hours record */
    private String id;
    
    /** ID of the restaurant these operating hours belong to */
    private String restaurantId;
    
    /** Day of the week these operating hours apply to */
    private DayOfWeek dayOfWeek;
    
    /** Time when the restaurant opens */
    private LocalTime openTime;
    
    /** Time when the restaurant closes */
    private LocalTime closeTime;
    
    /** Whether the restaurant is closed on this day */
    private boolean closed;
    
    /**
     * Default constructor.
     */
    public OperatingHoursDTO() {
    }
    
    /**
     * Gets the operating hours record's unique identifier.
     *
     * @return The operating hours ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the operating hours record's unique identifier.
     *
     * @param id The operating hours ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the ID of the restaurant these operating hours belong to.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the ID of the restaurant these operating hours belong to.
     *
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Gets the day of the week these operating hours apply to.
     *
     * @return The day of the week
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Sets the day of the week these operating hours apply to.
     *
     * @param dayOfWeek The day of the week to set
     */
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Gets the time when the restaurant opens.
     *
     * @return The opening time
     */
    public LocalTime getOpenTime() {
        return openTime;
    }

    /**
     * Sets the time when the restaurant opens.
     *
     * @param openTime The opening time to set
     */
    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    /**
     * Gets the time when the restaurant closes.
     *
     * @return The closing time
     */
    public LocalTime getCloseTime() {
        return closeTime;
    }

    /**
     * Sets the time when the restaurant closes.
     *
     * @param closeTime The closing time to set
     */
    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    /**
     * Checks if the restaurant is closed on this day.
     *
     * @return true if the restaurant is closed, false otherwise
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Sets whether the restaurant is closed on this day.
     *
     * @param closed true if the restaurant is closed, false otherwise
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
