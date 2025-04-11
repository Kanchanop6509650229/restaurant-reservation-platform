package com.restaurant.restaurant.domain.models;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entity class representing operating hours for a restaurant.
 * This class provides:
 * - Daily operating hours management
 * - Break period tracking
 * - Special hours handling
 * - Restaurant association
 * 
 * The entity is mapped to the 'operating_hours' table in the database
 * with a unique constraint on restaurant_id and day_of_week.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "operating_hours", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"restaurant_id", "day_of_week"}))
public class OperatingHours {

    /** Unique identifier for the operating hours record */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** The restaurant these operating hours belong to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    /** Day of the week these hours apply to */
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    /** Time when the restaurant opens */
    @Column(nullable = false)
    private LocalTime openTime;

    /** Time when the restaurant closes */
    @Column(nullable = false)
    private LocalTime closeTime;

    /** Flag indicating if the restaurant is closed on this day */
    @Column(nullable = false)
    private boolean closed = false;

    /** Start time of the break period (for restaurants that close between lunch and dinner) */
    private LocalTime breakStartTime;

    /** End time of the break period */
    private LocalTime breakEndTime;

    /** Description of special hours (e.g., for holidays or special events) */
    private String specialHoursDescription;

    /**
     * Default constructor for JPA.
     */
    public OperatingHours() {
    }

    /**
     * Constructs a new OperatingHours record with basic information.
     *
     * @param restaurant The restaurant these hours belong to
     * @param dayOfWeek The day of the week
     * @param openTime The opening time
     * @param closeTime The closing time
     */
    public OperatingHours(Restaurant restaurant, DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime) {
        this.restaurant = restaurant;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    /**
     * Checks if the restaurant is open at a specific time on this day.
     * This method considers:
     * - Regular operating hours
     * - Break periods
     * - Closed status
     *
     * @param time The time to check
     * @return true if the restaurant is open at the specified time, false otherwise
     */
    public boolean isOpenAt(LocalTime time) {
        if (closed) {
            return false;
        }

        // If there's a break period
        if (breakStartTime != null && breakEndTime != null) {
            return (time.isAfter(openTime) || time.equals(openTime)) && 
                   (time.isBefore(breakStartTime)) || 
                   (time.isAfter(breakEndTime) || time.equals(breakEndTime)) && 
                   (time.isBefore(closeTime));
        }

        // Regular hours
        return (time.isAfter(openTime) || time.equals(openTime)) && time.isBefore(closeTime);
    }

    /**
     * Gets the unique identifier of the operating hours record.
     *
     * @return The operating hours ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the operating hours record.
     *
     * @param id The operating hours ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the restaurant these operating hours belong to.
     *
     * @return The associated restaurant
     */
    public Restaurant getRestaurant() {
        return restaurant;
    }

    /**
     * Sets the restaurant these operating hours belong to.
     *
     * @param restaurant The associated restaurant
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    /**
     * Gets the day of the week these hours apply to.
     *
     * @return The day of the week
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Sets the day of the week these hours apply to.
     *
     * @param dayOfWeek The day of the week
     */
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

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