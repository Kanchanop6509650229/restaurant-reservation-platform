package com.restaurant.reservation.domain.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

/**
 * Entity class representing the reservation quota for a specific time slot at a restaurant.
 * This class manages the maximum number of reservations and capacity that can be
 * accommodated during a particular time slot on a given date, while also tracking
 * current usage and implementing threshold-based availability checks.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "reservation_quotas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"restaurant_id", "date", "time_slot"}))
public class ReservationQuota {

    /** Unique identifier for the quota record */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** ID of the restaurant this quota belongs to */
    @NotBlank(message = "Restaurant ID is required")
    @Column(name = "restaurant_id", nullable = false)
    private String restaurantId;

    /** Date for which this quota is defined */
    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    /** Time slot for which this quota is defined */
    @NotNull(message = "Time slot is required")
    @Column(name = "time_slot", nullable = false)
    private LocalTime timeSlot;

    /** Maximum number of reservations allowed for this time slot */
    @Min(value = 1, message = "Maximum reservations must be at least 1")
    @Column(nullable = false)
    private int maxReservations;

    /** Current number of reservations made for this time slot */
    @Column(nullable = false)
    private int currentReservations;

    /** Maximum seating capacity for this time slot */
    @Min(value = 1, message = "Maximum capacity must be at least 1")
    @Column(nullable = false)
    private int maxCapacity;

    /** Current number of seats reserved for this time slot */
    @Column(nullable = false)
    private int currentCapacity;

    /**
     * Percentage threshold for capacity utilization.
     * When capacity reaches this percentage, the time slot is considered full.
     * Default is 100% but can be adjusted for operational flexibility.
     */
    @Min(value = 1, message = "Threshold percentage must be at least 1")
    @Column(nullable = false)
    private int thresholdPercentage = 100;

    /**
     * Default constructor required by JPA.
     */
    public ReservationQuota() {
    }

    /**
     * Creates a new reservation quota for a specific time slot.
     *
     * @param restaurantId ID of the restaurant
     * @param date Date for which the quota is created
     * @param timeSlot Time slot for which the quota is created
     * @param maxReservations Maximum number of reservations allowed
     * @param maxCapacity Maximum seating capacity
     */
    public ReservationQuota(String restaurantId, LocalDate date, LocalTime timeSlot,
                            int maxReservations, int maxCapacity) {
        this.restaurantId = restaurantId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.maxReservations = maxReservations;
        this.maxCapacity = maxCapacity;
        this.currentReservations = 0;
        this.currentCapacity = 0;
        this.thresholdPercentage = 100; // Default to 100%
    }

    /**
     * Checks if there is availability for new reservations.
     * Availability is determined by both the maximum number of reservations
     * and the capacity threshold percentage.
     *
     * @return true if there is availability, false otherwise
     */
    public boolean hasAvailability() {
        // Prevent division by zero
        if (maxCapacity == 0) {
            return false;
        }

        return currentReservations < maxReservations &&
               (thresholdPercentage == 100 ||
                (currentCapacity * 100 / maxCapacity) < thresholdPercentage);
    }

    /**
     * Checks if a party of the specified size can be accommodated.
     *
     * @param partySize Number of people in the party
     * @return true if the party can be accommodated, false otherwise
     */
    public boolean canAccommodateParty(int partySize) {
        if (partySize <= 0) {
            return false;
        }
        return (currentCapacity + partySize) <= maxCapacity;
    }

    /**
     * Gets the current capacity utilization as a percentage.
     *
     * @return The capacity utilization percentage (0-100)
     */
    public int getCapacityUtilizationPercentage() {
        if (maxCapacity == 0) {
            return 100; // Prevent division by zero
        }
        return (currentCapacity * 100) / maxCapacity;
    }

    /**
     * Gets a formatted string representation of the time slot.
     *
     * @return The formatted time slot (e.g., "7:00 PM")
     */
    public String getFormattedTimeSlot() {
        if (timeSlot == null) {
            return "";
        }
        return timeSlot.format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    // Getters and setters with JavaDoc comments
    /**
     * Gets the unique identifier of the quota record.
     *
     * @return The quota record ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the quota record.
     *
     * @param id The ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the ID of the restaurant this quota belongs to.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the ID of the restaurant this quota belongs to.
     *
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Gets the date for which this quota is defined.
     *
     * @return The quota date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date for which this quota is defined.
     *
     * @param date The date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the time slot for which this quota is defined.
     *
     * @return The time slot
     */
    public LocalTime getTimeSlot() {
        return timeSlot;
    }

    /**
     * Sets the time slot for which this quota is defined.
     *
     * @param timeSlot The time slot to set
     */
    public void setTimeSlot(LocalTime timeSlot) {
        this.timeSlot = timeSlot;
    }

    /**
     * Gets the maximum number of reservations allowed.
     *
     * @return The maximum number of reservations
     */
    public int getMaxReservations() {
        return maxReservations;
    }

    /**
     * Sets the maximum number of reservations allowed.
     *
     * @param maxReservations The maximum number of reservations to set
     */
    public void setMaxReservations(int maxReservations) {
        this.maxReservations = maxReservations;
    }

    /**
     * Gets the current number of reservations made.
     *
     * @return The current number of reservations
     */
    public int getCurrentReservations() {
        return currentReservations;
    }

    /**
     * Sets the current number of reservations made.
     *
     * @param currentReservations The current number of reservations to set
     */
    public void setCurrentReservations(int currentReservations) {
        this.currentReservations = currentReservations;
    }

    /**
     * Gets the maximum seating capacity.
     *
     * @return The maximum capacity
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Sets the maximum seating capacity.
     *
     * @param maxCapacity The maximum capacity to set
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * Gets the current number of seats reserved.
     *
     * @return The current capacity
     */
    public int getCurrentCapacity() {
        return currentCapacity;
    }

    /**
     * Sets the current number of seats reserved.
     *
     * @param currentCapacity The current capacity to set
     */
    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    /**
     * Gets the capacity utilization threshold percentage.
     *
     * @return The threshold percentage
     */
    public int getThresholdPercentage() {
        return thresholdPercentage;
    }

    /**
     * Sets the capacity utilization threshold percentage.
     *
     * @param thresholdPercentage The threshold percentage to set
     */
    public void setThresholdPercentage(int thresholdPercentage) {
        this.thresholdPercentage = thresholdPercentage;
    }
}