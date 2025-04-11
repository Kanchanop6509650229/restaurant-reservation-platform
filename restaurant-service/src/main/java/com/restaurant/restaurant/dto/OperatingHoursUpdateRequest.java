package com.restaurant.restaurant.dto;

import java.time.LocalTime;

/**
 * Data Transfer Object for updating restaurant operating hours.
 * This class provides:
 * - Operating hours management
 * - Break period tracking
 * - Special hours handling
 * - Closed day status
 * 
 * Used to validate and transfer operating hours update data from the presentation layer
 * to the service layer.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class OperatingHoursUpdateRequest {

    /** Time when the restaurant opens */
    private LocalTime openTime;

    /** Time when the restaurant closes */
    private LocalTime closeTime;

    /** Flag indicating if the restaurant is closed on this day */
    private boolean closed;

    /** Start time of the break period */
    private LocalTime breakStartTime;

    /** End time of the break period */
    private LocalTime breakEndTime;

    /** Description of special hours (e.g., for holidays) */
    private String specialHoursDescription;

    /**
     * Gets the opening time of the restaurant.
     *
     * @return The opening time
     */
    public LocalTime getOpenTime() {
        return openTime;
    }

    /**
     * Sets the opening time of the restaurant.
     *
     * @param openTime The opening time
     */
    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    /**
     * Gets the closing time of the restaurant.
     *
     * @return The closing time
     */
    public LocalTime getCloseTime() {
        return closeTime;
    }

    /**
     * Sets the closing time of the restaurant.
     *
     * @param closeTime The closing time
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
     * @param closed The closed status
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Gets the start time of the break period.
     *
     * @return The break start time
     */
    public LocalTime getBreakStartTime() {
        return breakStartTime;
    }

    /**
     * Sets the start time of the break period.
     *
     * @param breakStartTime The break start time
     */
    public void setBreakStartTime(LocalTime breakStartTime) {
        this.breakStartTime = breakStartTime;
    }

    /**
     * Gets the end time of the break period.
     *
     * @return The break end time
     */
    public LocalTime getBreakEndTime() {
        return breakEndTime;
    }

    /**
     * Sets the end time of the break period.
     *
     * @param breakEndTime The break end time
     */
    public void setBreakEndTime(LocalTime breakEndTime) {
        this.breakEndTime = breakEndTime;
    }

    /**
     * Gets the description of special hours.
     *
     * @return The special hours description
     */
    public String getSpecialHoursDescription() {
        return specialHoursDescription;
    }

    /**
     * Sets the description of special hours.
     *
     * @param specialHoursDescription The special hours description
     */
    public void setSpecialHoursDescription(String specialHoursDescription) {
        this.specialHoursDescription = specialHoursDescription;
    }
}