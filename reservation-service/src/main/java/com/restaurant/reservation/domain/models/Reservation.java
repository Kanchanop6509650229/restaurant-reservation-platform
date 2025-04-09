package com.restaurant.reservation.domain.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a restaurant reservation.
 * This class maps to the 'reservations' table in the database and contains
 * all the information about a restaurant reservation.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    /** Unique identifier for the reservation */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** ID of the user who made the reservation */
    @Column(nullable = false)
    private String userId;

    /** ID of the restaurant where the reservation is made */
    @Column(nullable = false)
    private String restaurantId;

    /** ID of the table assigned to the reservation */
    private String tableId;

    /** Date and time of the reservation */
    @Column(nullable = false)
    private LocalDateTime reservationTime;

    /** Number of people in the party */
    @Column(nullable = false)
    private int partySize;

    /** Duration of the reservation in minutes */
    @Column(nullable = false)
    private int durationMinutes;

    /** Current status of the reservation (PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW) */
    @Column(nullable = false)
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW

    /** Name of the customer making the reservation */
    @Column(nullable = false)
    private String customerName;

    /** Phone number of the customer */
    private String customerPhone;

    /** Email address of the customer */
    private String customerEmail;

    /** Any special requests or notes for the reservation */
    private String specialRequests;

    /** Flag indicating if reminders are enabled for this reservation */
    private boolean remindersEnabled;

    /** Set of history records tracking changes to the reservation */
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservationHistory> history = new HashSet<>();

    /** Date and time when the reservation was created */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Date and time when the reservation was last updated */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** Deadline for confirming the reservation */
    private LocalDateTime confirmationDeadline;

    /** Date and time when the reservation was confirmed */
    private LocalDateTime confirmedAt;

    /** Date and time when the reservation was cancelled */
    private LocalDateTime cancelledAt;

    /** Date and time when the reservation was completed */
    private LocalDateTime completedAt;

    /** Reason for cancellation if the reservation was cancelled */
    private String cancellationReason;

    /**
     * Sets the creation timestamp before persisting a new reservation.
     * This method is automatically called by JPA before saving a new entity.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Updates the modification timestamp before updating a reservation.
     * This method is automatically called by JPA before updating an entity.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Adds a new history record to track changes to the reservation.
     *
     * @param record The history record to add
     */
    public void addHistoryRecord(ReservationHistory record) {
        history.add(record);
        record.setReservation(this);
    }

    /**
     * Calculates the expected end time of the reservation.
     *
     * @return The end time of the reservation
     */
    public LocalDateTime getEndTime() {
        return reservationTime.plusMinutes(durationMinutes);
    }

    /** Default constructor required by JPA */
    public Reservation() {
    }

    /**
     * Creates a new reservation with the specified details.
     *
     * @param userId ID of the user making the reservation
     * @param restaurantId ID of the restaurant
     * @param reservationTime Date and time of the reservation
     * @param partySize Number of people in the party
     * @param durationMinutes Duration of the reservation in minutes
     * @param customerName Name of the customer
     */
    public Reservation(String userId, String restaurantId, LocalDateTime reservationTime, 
                      int partySize, int durationMinutes, String customerName) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.reservationTime = reservationTime;
        this.partySize = partySize;
        this.durationMinutes = durationMinutes;
        this.customerName = customerName;
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
     * Gets the date and time of the reservation.
     *
     * @return The reservation time
     */
    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    /**
     * Sets the date and time of the reservation.
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
     * Gets the set of history records tracking changes to the reservation.
     *
     * @return The set of history records
     */
    public Set<ReservationHistory> getHistory() {
        return history;
    }

    /**
     * Sets the set of history records tracking changes to the reservation.
     *
     * @param history The set of history records to set
     */
    public void setHistory(Set<ReservationHistory> history) {
        this.history = history;
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
     * Gets the date and time when the reservation was confirmed.
     *
     * @return The confirmation timestamp
     */
    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    /**
     * Sets the date and time when the reservation was confirmed.
     *
     * @param confirmedAt The confirmation timestamp to set
     */
    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    /**
     * Gets the date and time when the reservation was cancelled.
     *
     * @return The cancellation timestamp
     */
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    /**
     * Sets the date and time when the reservation was cancelled.
     *
     * @param cancelledAt The cancellation timestamp to set
     */
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    /**
     * Gets the date and time when the reservation was completed.
     *
     * @return The completion timestamp
     */
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    /**
     * Sets the date and time when the reservation was completed.
     *
     * @param completedAt The completion timestamp to set
     */
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    /**
     * Gets the reason for cancellation if the reservation was cancelled.
     *
     * @return The cancellation reason
     */
    public String getCancellationReason() {
        return cancellationReason;
    }

    /**
     * Sets the reason for cancellation if the reservation was cancelled.
     *
     * @param cancellationReason The cancellation reason to set
     */
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}