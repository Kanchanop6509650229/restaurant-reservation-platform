package com.restaurant.reservation.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object (DTO) representing a restaurant reservation.
 * This class is used to transfer reservation data between different layers of the application.
 *
 * Features:
 * - Complete reservation details including customer information
 * - Temporal data with proper formatting
 * - Reservation status tracking
 * - History of changes to the reservation
 * - Null fields are excluded from JSON serialization
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationDTO {

    /** Unique identifier for the reservation */
    private String id;

    /** ID of the user who made the reservation */
    private String userId;

    /** ID of the restaurant where the reservation is made */
    private String restaurantId;

    /** ID of the table assigned to the reservation */
    private String tableId;

    /** Scheduled date and time of the reservation */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reservationTime;

    /** Expected end time of the reservation */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    /** Number of people in the party */
    private int partySize;

    /** Duration of the reservation in minutes */
    private int durationMinutes;

    /** Current status of the reservation */
    private String status;

    /** Name of the customer making the reservation */
    private String customerName;

    /** Phone number of the customer */
    private String customerPhone;

    /** Email address of the customer */
    private String customerEmail;

    /** Any special requests or notes for the reservation */
    private String specialRequests;

    /** Flag indicating if reminders are enabled for this reservation */
    private boolean remindersEnabled;

    /** Date and time when the reservation was created */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /** Date and time when the reservation was last updated */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    /** Deadline for confirming the reservation */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime confirmationDeadline;

    /** List of history records tracking changes to the reservation */
    private List<HistoryRecord> historyRecords = new ArrayList<>();

    /** List of menu items included in the reservation */
    private List<ReservationMenuItemDTO> menuItems = new ArrayList<>();

    /**
     * Nested class representing a history record for the reservation.
     * Tracks changes and actions performed on the reservation.
     */
    public static class HistoryRecord {
        /** Type of action performed (e.g., CREATED, UPDATED, CANCELLED) */
        private String action;

        /** Date and time when the action was performed */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime timestamp;

        /** Detailed description of the action */
        private String details;

        /** Default constructor */
        public HistoryRecord() {
        }

        /**
         * Creates a new history record with the specified details.
         *
         * @param action Type of action performed
         * @param timestamp When the action was performed
         * @param details Description of the action
         */
        public HistoryRecord(String action, LocalDateTime timestamp, String details) {
            this.action = action;
            this.timestamp = timestamp;
            this.details = details;
        }

        /**
         * Gets the type of action performed.
         *
         * @return The action type
         */
        public String getAction() {
            return action;
        }

        /**
         * Sets the type of action performed.
         *
         * @param action The action type to set
         */
        public void setAction(String action) {
            this.action = action;
        }

        /**
         * Gets the timestamp of when the action was performed.
         *
         * @return The timestamp
         */
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        /**
         * Sets the timestamp of when the action was performed.
         *
         * @param timestamp The timestamp to set
         */
        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        /**
         * Gets the detailed description of the action.
         *
         * @return The action details
         */
        public String getDetails() {
            return details;
        }

        /**
         * Sets the detailed description of the action.
         *
         * @param details The details to set
         */
        public void setDetails(String details) {
            this.details = details;
        }
    }

    // Getters and setters with JavaDoc comments
    /**
     * Gets the unique identifier of the reservation.
     *
     * @return The reservation ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the reservation.
     *
     * @param id The ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the ID of the user who made the reservation.
     *
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who made the reservation.
     *
     * @param userId The user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the ID of the restaurant where the reservation is made.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the ID of the restaurant where the reservation is made.
     *
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Gets the ID of the table assigned to the reservation.
     *
     * @return The table ID
     */
    public String getTableId() {
        return tableId;
    }

    /**
     * Sets the ID of the table assigned to the reservation.
     *
     * @param tableId The table ID to set
     */
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    /**
     * Gets the scheduled date and time of the reservation.
     *
     * @return The reservation time
     */
    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    /**
     * Sets the scheduled date and time of the reservation.
     *
     * @param reservationTime The reservation time to set
     */
    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    /**
     * Gets the expected end time of the reservation.
     *
     * @return The end time
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the expected end time of the reservation.
     *
     * @param endTime The end time to set
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
     * @param partySize The party size to set
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
     * Gets the current status of the reservation.
     *
     * @return The reservation status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the reservation.
     *
     * @param status The status to set
     */
    public void setStatus(String status) {
        this.status = status;
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
     * @param customerEmail The email to set
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
     * Sets whether reminders are enabled for this reservation.
     *
     * @param remindersEnabled true to enable reminders, false to disable
     */
    public void setRemindersEnabled(boolean remindersEnabled) {
        this.remindersEnabled = remindersEnabled;
    }

    /**
     * Gets the date and time when the reservation was created.
     *
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the date and time when the reservation was created.
     *
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the date and time when the reservation was last updated.
     *
     * @return The last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the date and time when the reservation was last updated.
     *
     * @param updatedAt The update timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the deadline for confirming the reservation.
     *
     * @return The confirmation deadline
     */
    public LocalDateTime getConfirmationDeadline() {
        return confirmationDeadline;
    }

    /**
     * Sets the deadline for confirming the reservation.
     *
     * @param confirmationDeadline The deadline to set
     */
    public void setConfirmationDeadline(LocalDateTime confirmationDeadline) {
        this.confirmationDeadline = confirmationDeadline;
    }

    /**
     * Gets the list of history records tracking changes to the reservation.
     *
     * @return The list of history records
     */
    public List<HistoryRecord> getHistoryRecords() {
        return historyRecords;
    }

    /**
     * Sets the list of history records tracking changes to the reservation.
     *
     * @param historyRecords The list of history records to set
     */
    public void setHistoryRecords(List<HistoryRecord> historyRecords) {
        this.historyRecords = historyRecords;
    }

    /**
     * Gets the list of menu items included in the reservation.
     *
     * @return The list of menu items
     */
    public List<ReservationMenuItemDTO> getMenuItems() {
        return menuItems;
    }

    /**
     * Sets the list of menu items included in the reservation.
     *
     * @param menuItems The menu items to set
     */
    public void setMenuItems(List<ReservationMenuItemDTO> menuItems) {
        this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationDTO that = (ReservationDTO) o;
        return partySize == that.partySize &&
               durationMinutes == that.durationMinutes &&
               remindersEnabled == that.remindersEnabled &&
               Objects.equals(id, that.id) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(restaurantId, that.restaurantId) &&
               Objects.equals(tableId, that.tableId) &&
               Objects.equals(reservationTime, that.reservationTime) &&
               Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, restaurantId, tableId, reservationTime,
                          partySize, durationMinutes, status, remindersEnabled);
    }

    @Override
    public String toString() {
        return "ReservationDTO{" +
               "id='" + id + '\'' +
               ", restaurantId='" + restaurantId + '\'' +
               ", tableId='" + tableId + '\'' +
               ", reservationTime=" + reservationTime +
               ", status='" + status + '\'' +
               ", partySize=" + partySize +
               ", customerName='" + customerName + '\'' +
               '}';
    }
}