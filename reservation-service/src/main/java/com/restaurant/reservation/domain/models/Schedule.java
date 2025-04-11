package com.restaurant.reservation.domain.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity class representing a restaurant's daily schedule.
 * This class maps to the 'schedules' table in the database and contains
 * information about a restaurant's operating hours and capacity for a specific date.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "schedules",
       uniqueConstraints = @UniqueConstraint(columnNames = {"restaurant_id", "date"}))
public class Schedule {

    /** Unique identifier for the schedule */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** ID of the restaurant this schedule belongs to */
    @NotBlank(message = "Restaurant ID is required")
    @Column(name = "restaurant_id", nullable = false)
    private String restaurantId;

    /** Date for which this schedule is defined */
    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    /** Flag indicating if custom opening time is set for this date */
    @Column(nullable = false)
    private boolean customOpenTime = false;

    /** Custom opening time for this date (if customOpenTime is true) */
    private LocalTime openTime;

    /** Flag indicating if custom closing time is set for this date */
    @Column(nullable = false)
    private boolean customCloseTime = false;

    /** Custom closing time for this date (if customCloseTime is true) */
    private LocalTime closeTime;

    /** Flag indicating if the restaurant is closed on this date */
    @Column(nullable = false)
    private boolean closed = false;

    /** Description of special hours or events for this date */
    @Size(max = 500, message = "Special hours description must be at most 500 characters")
    @Column(length = 500)
    private String specialHoursDescription;

    /** Total seating capacity of the restaurant for this date */
    @Min(value = 0, message = "Total capacity cannot be negative")
    private int totalCapacity;

    /** Available seating capacity for this date */
    @Min(value = 0, message = "Available capacity cannot be negative")
    private int availableCapacity;

    /** Number of seats already booked for this date */
    @Min(value = 0, message = "Booked capacity cannot be negative")
    private int bookedCapacity;

    /** Number of tables already booked for this date */
    @Min(value = 0, message = "Booked tables cannot be negative")
    private int bookedTables;

    /**
     * Default constructor required by JPA.
     */
    public Schedule() {
    }

    /**
     * Creates a new schedule for a specific restaurant and date.
     *
     * @param restaurantId ID of the restaurant
     * @param date Date for which the schedule is created
     */
    public Schedule(String restaurantId, LocalDate date) {
        this.restaurantId = restaurantId;
        this.date = date;
        this.closed = false;
        this.totalCapacity = 0;
        this.availableCapacity = 0;
        this.bookedCapacity = 0;
        this.bookedTables = 0;
    }

    /**
     * Calculates the operating hours duration in minutes.
     * Returns 0 if the restaurant is closed or if opening/closing times are not set.
     *
     * @return The duration of operating hours in minutes
     */
    public long getOperatingHoursDuration() {
        if (closed || openTime == null || closeTime == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(openTime, closeTime);
    }

    /**
     * Gets a formatted string representation of the operating hours.
     * Returns "Closed" if the restaurant is closed.
     *
     * @return The formatted operating hours (e.g., "10:00 AM - 10:00 PM")
     */
    public String getFormattedOperatingHours() {
        if (closed) {
            return "Closed";
        }

        if (openTime == null || closeTime == null) {
            return "Hours not set";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return openTime.format(formatter) + " - " + closeTime.format(formatter);
    }

    /**
     * Updates the available capacity based on booked capacity.
     * This method ensures that available capacity is correctly calculated
     * as the difference between total and booked capacity.
     */
    public void updateAvailableCapacity() {
        this.availableCapacity = Math.max(0, this.totalCapacity - this.bookedCapacity);
    }

    // Getters and setters with JavaDoc comments
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
     * Checks if custom opening time is set for this date.
     *
     * @return true if custom opening time is set, false otherwise
     */
    public boolean isCustomOpenTime() {
        return customOpenTime;
    }

    /**
     * Sets whether custom opening time is used for this date.
     *
     * @param customOpenTime true to use custom opening time, false otherwise
     */
    public void setCustomOpenTime(boolean customOpenTime) {
        this.customOpenTime = customOpenTime;
    }

    /**
     * Gets the custom opening time for this date.
     *
     * @return The custom opening time
     */
    public LocalTime getOpenTime() {
        return openTime;
    }

    /**
     * Sets the custom opening time for this date.
     *
     * @param openTime The opening time to set
     */
    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    /**
     * Checks if custom closing time is set for this date.
     *
     * @return true if custom closing time is set, false otherwise
     */
    public boolean isCustomCloseTime() {
        return customCloseTime;
    }

    /**
     * Sets whether custom closing time is used for this date.
     *
     * @param customCloseTime true to use custom closing time, false otherwise
     */
    public void setCustomCloseTime(boolean customCloseTime) {
        this.customCloseTime = customCloseTime;
    }

    /**
     * Gets the custom closing time for this date.
     *
     * @return The custom closing time
     */
    public LocalTime getCloseTime() {
        return closeTime;
    }

    /**
     * Sets the custom closing time for this date.
     *
     * @param closeTime The closing time to set
     */
    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
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
     * Gets the total seating capacity of the restaurant for this date.
     *
     * @return The total capacity
     */
    public int getTotalCapacity() {
        return totalCapacity;
    }

    /**
     * Sets the total seating capacity of the restaurant for this date.
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
}