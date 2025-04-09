package com.restaurant.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object (DTO) for restaurant schedule information.
 * This class is used to transfer schedule data between different layers of the application
 * and includes both basic schedule information and derived/formatted data for display purposes.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class ScheduleDTO {

    /** Unique identifier for the schedule */
    private String id;

    /** ID of the restaurant this schedule belongs to */
    private String restaurantId;

    /** Date for which this schedule is defined */
    private LocalDate date;

    /** Day of the week (e.g., "Monday", "Tuesday") for easier display */
    private String dayOfWeek;

    /** Flag indicating if the restaurant is closed on this date */
    private boolean closed;

    /** Opening time for the restaurant on this date */
    private LocalTime openTime;

    /** Closing time for the restaurant on this date */
    private LocalTime closeTime;

    /** Flag indicating if a custom opening time is set for this date */
    private boolean customOpenTime;

    /** Flag indicating if a custom closing time is set for this date */
    private boolean customCloseTime;

    /** Description of special hours or events for this date */
    private String specialHoursDescription;

    /** Total seating capacity of the restaurant for this date */
    private int totalCapacity;

    /** Available seating capacity for this date */
    private int availableCapacity;

    /** Number of seats already booked for this date */
    private int bookedCapacity;

    /** Number of tables already booked for this date */
    private int bookedTables;

    /** 
     * Formatted string representing operating hours (e.g., "10:00 AM - 10:00 PM")
     * Used for display purposes
     */
    private String operatingHours;

    /** Formatted opening time string for display purposes */
    private String formattedOpenTime;

    /** Formatted closing time string for display purposes */
    private String formattedCloseTime;

    /**
     * Gets the unique identifier of the schedule.
     *
     * @return The schedule ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the schedule.
     *
     * @param id The ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the ID of the restaurant this schedule belongs to.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the ID of the restaurant this schedule belongs to.
     *
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Gets the date for which this schedule is defined.
     *
     * @return The schedule date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date for which this schedule is defined.
     *
     * @param date The date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the day of the week as a string.
     *
     * @return The day of the week (e.g., "Monday", "Tuesday")
     */
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Sets the day of the week.
     *
     * @param dayOfWeek The day of the week to set
     */
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Checks if the restaurant is closed on this date.
     *
     * @return true if the restaurant is closed, false otherwise
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Sets whether the restaurant is closed on this date.
     *
     * @param closed true if the restaurant is closed, false otherwise
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Gets the opening time for this date.
     *
     * @return The opening time
     */
    public LocalTime getOpenTime() {
        return openTime;
    }

    /**
     * Sets the opening time for this date.
     *
     * @param openTime The opening time to set
     */
    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    /**
     * Gets the closing time for this date.
     *
     * @return The closing time
     */
    public LocalTime getCloseTime() {
        return closeTime;
    }

    /**
     * Sets the closing time for this date.
     *
     * @param closeTime The closing time to set
     */
    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    /**
     * Checks if a custom opening time is set for this date.
     *
     * @return true if custom opening time is set, false otherwise
     */
    public boolean isCustomOpenTime() {
        return customOpenTime;
    }

    /**
     * Sets whether a custom opening time is used for this date.
     *
     * @param customOpenTime true to use custom opening time, false otherwise
     */
    public void setCustomOpenTime(boolean customOpenTime) {
        this.customOpenTime = customOpenTime;
    }

    /**
     * Checks if a custom closing time is set for this date.
     *
     * @return true if custom closing time is set, false otherwise
     */
    public boolean isCustomCloseTime() {
        return customCloseTime;
    }

    /**
     * Sets whether a custom closing time is used for this date.
     *
     * @param customCloseTime true to use custom closing time, false otherwise
     */
    public void setCustomCloseTime(boolean customCloseTime) {
        this.customCloseTime = customCloseTime;
    }

    /**
     * Gets the description of special hours or events for this date.
     *
     * @return The special hours description
     */
    public String getSpecialHoursDescription() {
        return specialHoursDescription;
    }

    /**
     * Sets the description of special hours or events for this date.
     *
     * @param specialHoursDescription The description to set
     */
    public void setSpecialHoursDescription(String specialHoursDescription) {
        this.specialHoursDescription = specialHoursDescription;
    }

    /**
     * Gets the total seating capacity for this date.
     *
     * @return The total capacity
     */
    public int getTotalCapacity() {
        return totalCapacity;
    }

    /**
     * Sets the total seating capacity for this date.
     *
     * @param totalCapacity The total capacity to set
     */
    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    /**
     * Gets the available seating capacity for this date.
     *
     * @return The available capacity
     */
    public int getAvailableCapacity() {
        return availableCapacity;
    }

    /**
     * Sets the available seating capacity for this date.
     *
     * @param availableCapacity The available capacity to set
     */
    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    /**
     * Gets the number of seats already booked for this date.
     *
     * @return The booked capacity
     */
    public int getBookedCapacity() {
        return bookedCapacity;
    }

    /**
     * Sets the number of seats already booked for this date.
     *
     * @param bookedCapacity The booked capacity to set
     */
    public void setBookedCapacity(int bookedCapacity) {
        this.bookedCapacity = bookedCapacity;
    }

    /**
     * Gets the number of tables already booked for this date.
     *
     * @return The number of booked tables
     */
    public int getBookedTables() {
        return bookedTables;
    }

    /**
     * Sets the number of tables already booked for this date.
     *
     * @param bookedTables The number of booked tables to set
     */
    public void setBookedTables(int bookedTables) {
        this.bookedTables = bookedTables;
    }

    /**
     * Gets the formatted operating hours string.
     *
     * @return The operating hours (e.g., "10:00 AM - 10:00 PM")
     */
    public String getOperatingHours() {
        return operatingHours;
    }

    /**
     * Sets the formatted operating hours string.
     *
     * @param operatingHours The operating hours string to set
     */
    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    /**
     * Gets the formatted opening time string.
     *
     * @return The formatted opening time
     */
    public String getFormattedOpenTime() {
        return formattedOpenTime;
    }

    /**
     * Sets the formatted opening time string.
     *
     * @param formattedOpenTime The formatted opening time to set
     */
    public void setFormattedOpenTime(String formattedOpenTime) {
        this.formattedOpenTime = formattedOpenTime;
    }

    /**
     * Gets the formatted closing time string.
     *
     * @return The formatted closing time
     */
    public String getFormattedCloseTime() {
        return formattedCloseTime;
    }

    /**
     * Sets the formatted closing time string.
     *
     * @param formattedCloseTime The formatted closing time to set
     */
    public void setFormattedCloseTime(String formattedCloseTime) {
        this.formattedCloseTime = formattedCloseTime;
    }
}