package com.restaurant.reservation.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing a request to create a new reservation.
 * Contains all the necessary information required to create a restaurant reservation.
 *
 * This class includes validation constraints to ensure that:
 * - Required fields are provided (restaurant ID, reservation time, customer name)
 * - Numeric values are within acceptable ranges (party size, duration)
 * - Email addresses are properly formatted
 * - Phone numbers follow a consistent format
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
public class ReservationCreateRequest {

    /** ID of the restaurant where the reservation should be made */
    @NotBlank(message = "Restaurant ID is required")
    @Size(min = 36, max = 36, message = "Restaurant ID must be a valid UUID")
    private String restaurantId;

    /** Date and time for the requested reservation */
    @NotNull(message = "Reservation time is required")
    private LocalDateTime reservationTime;

    /** Number of people in the party (minimum 1) */
    @Min(value = 1, message = "Party size must be at least 1")
    @jakarta.validation.constraints.Max(value = 100, message = "Party size cannot exceed 100 people")
    private int partySize;

    /** Duration of the reservation in minutes (optional) */
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @jakarta.validation.constraints.Max(value = 480, message = "Duration cannot exceed 8 hours (480 minutes)")
    private int durationMinutes = 90; // Default to 90 minutes if not specified

    /** Name of the customer making the reservation */
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    /** Phone number of the customer (optional) */
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$",
             message = "Phone number must be in a valid format (e.g., +1 555-123-4567)")
    private String customerPhone;

    /** Email address of the customer (optional, must be valid if provided) */
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String customerEmail;

    /** Any special requests or notes for the reservation (optional) */
    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;

    /** Flag indicating if reminders should be enabled (defaults to true) */
    private boolean remindersEnabled = true;

    /** List of menu items to be included in the reservation (optional) */
    @Valid
    private List<MenuItemSelectionDTO> menuItems = new ArrayList<>();

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
     * @param partySize The party size to set (must be at least 1 and no more than 100)
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
     * @param durationMinutes The duration to set (must be at least 15 minutes and no more than 8 hours)
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

    /**
     * Gets the list of menu items to be included in the reservation.
     *
     * @return The list of menu items
     */
    public List<MenuItemSelectionDTO> getMenuItems() {
        return menuItems;
    }

    /**
     * Sets the list of menu items to be included in the reservation.
     *
     * @param menuItems The list of menu items to set
     */
    public void setMenuItems(List<MenuItemSelectionDTO> menuItems) {
        this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
    }

    /**
     * Adds a menu item to the reservation.
     *
     * @param menuItem The menu item to add
     */
    public void addMenuItem(MenuItemSelectionDTO menuItem) {
        if (menuItem != null) {
            this.menuItems.add(menuItem);
        }
    }
}