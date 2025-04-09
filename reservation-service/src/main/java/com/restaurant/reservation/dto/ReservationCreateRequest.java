package com.restaurant.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a request to create a new reservation.
 * Contains all the necessary information required to create a restaurant reservation.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class ReservationCreateRequest {

    /** ID of the restaurant where the reservation should be made */
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    /** Date and time for the requested reservation */
    @NotNull(message = "Reservation time is required")
    private LocalDateTime reservationTime;

    /** Number of people in the party (minimum 1) */
    @Min(value = 1, message = "Party size must be at least 1")
    private int partySize;

    /** Duration of the reservation in minutes (optional) */
    private int durationMinutes;

    /** Name of the customer making the reservation */
    @NotBlank(message = "Customer name is required")
    private String customerName;

    /** Phone number of the customer (optional) */
    private String customerPhone;

    /** Email address of the customer (optional, must be valid if provided) */
    @Email(message = "Email must be valid")
    private String customerEmail;

    /** Any special requests or notes for the reservation (optional) */
    private String specialRequests;

    /** Flag indicating if reminders should be enabled (defaults to true) */
    private boolean remindersEnabled = true;

    /**
     * Gets the ID of the restaurant where the reservation should be made.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the ID of the restaurant where the reservation should be made.
     *
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Gets the date and time for the requested reservation.
     *
     * @return The reservation time
     */
    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    /**
     * Sets the date and time for the requested reservation.
     *
     * @param reservationTime The reservation time to set
     */
    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    /**
     * Gets the number of people in the party.
     *
     * @return The party size
     */
    public int getPartySize() {
        return partySize;
    }

    /**
     * Sets the number of people in the party.
     *
     * @param partySize The party size to set (must be at least 1)
     */
    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    /**
     * Gets the duration of the reservation in minutes.
     *
     * @return The duration in minutes
     */
    public int getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Sets the duration of the reservation in minutes.
     *
     * @param durationMinutes The duration to set
     */
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    /**
     * Gets the name of the customer making the reservation.
     *
     * @return The customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the name of the customer making the reservation.
     *
     * @param customerName The customer name to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the phone number of the customer.
     *
     * @return The customer phone number
     */
    public String getCustomerPhone() {
        return customerPhone;
    }

    /**
     * Sets the phone number of the customer.
     *
     * @param customerPhone The phone number to set
     */
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    /**
     * Gets the email address of the customer.
     *
     * @return The customer email
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Sets the email address of the customer.
     *
     * @param customerEmail The email to set (must be valid if provided)
     */
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    /**
     * Gets any special requests or notes for the reservation.
     *
     * @return The special requests
     */
    public String getSpecialRequests() {
        return specialRequests;
    }

    /**
     * Sets any special requests or notes for the reservation.
     *
     * @param specialRequests The special requests to set
     */
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    /**
     * Checks if reminders are enabled for this reservation.
     *
     * @return true if reminders are enabled, false otherwise
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