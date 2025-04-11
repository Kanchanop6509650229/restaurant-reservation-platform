package com.restaurant.reservation.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing a request to update an existing reservation.
 * Contains the fields that can be modified for an existing reservation.
 * All fields are optional - only the fields that need to be updated should be set.
 *
 * This class includes validation constraints to ensure that:
 * - Numeric values are within acceptable ranges (party size, duration)
 * - Email addresses are properly formatted
 * - Phone numbers follow a consistent format
 * - Text fields don't exceed maximum lengths
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
public class ReservationUpdateRequest {

    /** New date and time for the reservation (optional) */
    private LocalDateTime reservationTime;

    /** New number of people in the party (optional, minimum 1 if provided) */
    @Min(value = 1, message = "Party size must be at least 1")
    @jakarta.validation.constraints.Max(value = 100, message = "Party size cannot exceed 100 people")
    private int partySize;

    /** New duration of the reservation in minutes (optional) */
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @jakarta.validation.constraints.Max(value = 480, message = "Duration cannot exceed 8 hours (480 minutes)")
    private int durationMinutes;

    /** New name of the customer (optional) */
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    /** New phone number of the customer (optional) */
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$",
             message = "Phone number must be in a valid format (e.g., +1 555-123-4567)")
    private String customerPhone;

    /** New email address of the customer (optional, must be valid if provided) */
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String customerEmail;

    /** New special requests or notes for the reservation (optional) */
    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;

    /** New setting for whether reminders should be enabled (optional) */
    private boolean remindersEnabled;

    /**
     * Gets the new date and time for the reservation.
     *
     * @return The new reservation time
     */
    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    /**
     * Sets the new date and time for the reservation.
     *
     * @param reservationTime The new reservation time to set
     */
    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    /**
     * Gets the new number of people in the party.
     *
     * @return The new party size
     */
    public int getPartySize() {
        return partySize;
    }

    /**
     * Sets the new number of people in the party.
     *
     * @param partySize The new party size to set (must be at least 1 and no more than 100)
     */
    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    /**
     * Gets the new duration of the reservation in minutes.
     *
     * @return The new duration in minutes
     */
    public int getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Sets the new duration of the reservation in minutes.
     *
     * @param durationMinutes The new duration to set (must be at least 15 minutes and no more than 8 hours)
     */
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    /**
     * Gets the new name of the customer.
     *
     * @return The new customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the new name of the customer.
     *
     * @param customerName The new customer name to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the new phone number of the customer.
     *
     * @return The new customer phone number
     */
    public String getCustomerPhone() {
        return customerPhone;
    }

    /**
     * Sets the new phone number of the customer.
     *
     * @param customerPhone The new phone number to set
     */
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    /**
     * Gets the new email address of the customer.
     *
     * @return The new customer email
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Sets the new email address of the customer.
     *
     * @param customerEmail The new email to set (must be valid if provided)
     */
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    /**
     * Gets the new special requests or notes for the reservation.
     *
     * @return The new special requests
     */
    public String getSpecialRequests() {
        return specialRequests;
    }

    /**
     * Sets the new special requests or notes for the reservation.
     *
     * @param specialRequests The new special requests to set
     */
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    /**
     * Checks if reminders should be enabled for this reservation.
     *
     * @return true if reminders should be enabled, false otherwise
     */
    public boolean isRemindersEnabled() {
        return remindersEnabled;
    }

    /**
     * Sets whether reminders should be enabled for this reservation.
     *
     * @param remindersEnabled true to enable reminders, false to disable
     */
    public void setRemindersEnabled(boolean remindersEnabled) {
        this.remindersEnabled = remindersEnabled;
    }
}