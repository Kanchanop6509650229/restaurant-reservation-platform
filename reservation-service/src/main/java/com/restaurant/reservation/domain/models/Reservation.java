package com.restaurant.reservation.domain.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.restaurant.common.constants.StatusCodes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity class representing a restaurant reservation.
 * This class maps to the 'reservations' table in the database and contains
 * all the information about a restaurant reservation.
 *
 * Features:
 * - Complete reservation lifecycle management (creation, confirmation, cancellation, completion)
 * - History tracking for all changes
 * - Status-based state management
 * - Validation constraints for data integrity
 * - Automatic timestamp management
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
@Entity
@Table(name = "reservations",
       indexes = {
           @Index(name = "idx_reservation_user_id", columnList = "userId"),
           @Index(name = "idx_reservation_restaurant_id", columnList = "restaurantId"),
           @Index(name = "idx_reservation_status", columnList = "status"),
           @Index(name = "idx_reservation_time", columnList = "reservationTime")
       })
public class Reservation {

    /** Unique identifier for the reservation */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** ID of the user who made the reservation */
    @NotBlank(message = "User ID is required")
    @Column(nullable = false)
    private String userId;

    /** ID of the restaurant where the reservation is made */
    @NotBlank(message = "Restaurant ID is required")
    @Column(nullable = false)
    private String restaurantId;

    /** ID of the table assigned to the reservation */
    private String tableId;

    /** Date and time of the reservation */
    @NotNull(message = "Reservation time is required")
    @Column(nullable = false)
    private LocalDateTime reservationTime;

    /** Number of people in the party */
    @Min(value = 1, message = "Party size must be at least 1")
    @Column(nullable = false)
    private int partySize;

    /** Duration of the reservation in minutes */
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Column(nullable = false)
    private int durationMinutes;

    /** Current status of the reservation */
    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private String status = StatusCodes.RESERVATION_PENDING;

    /** Name of the customer making the reservation */
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 255, message = "Customer name must be between 2 and 255 characters")
    @Column(nullable = false)
    private String customerName;

    /** Phone number of the customer */
    @Size(max = 20, message = "Phone number must be at most 20 characters")
    private String customerPhone;

    /** Email address of the customer */
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String customerEmail;

    /** Any special requests or notes for the reservation */
    @Size(max = 1000, message = "Special requests must be at most 1000 characters")
    @Column(length = 1000)
    private String specialRequests;

    /** Flag indicating if reminders are enabled for this reservation */
    @Column(nullable = false)
    private boolean remindersEnabled = true;

    /** Set of history records tracking changes to the reservation */
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ReservationHistory> history = new HashSet<>();

    /** Set of menu items selected for this reservation */
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ReservationMenuItem> menuItems = new HashSet<>();

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
    @Size(max = 500, message = "Cancellation reason must be at most 500 characters")
    @Column(length = 500)
    private String cancellationReason;

    /**
     * Sets the creation timestamp before persisting a new reservation.
     * This method is automatically called by JPA before saving a new entity.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        // Set default status if not already set
        if (this.status == null) {
            this.status = StatusCodes.RESERVATION_PENDING;
        }
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
     * Adds a menu item to the reservation.
     *
     * @param menuItem The menu item to add
     */
    public void addMenuItem(ReservationMenuItem menuItem) {
        menuItems.add(menuItem);
        menuItem.setReservation(this);
    }

    /**
     * Removes a menu item from the reservation.
     *
     * @param menuItem The menu item to remove
     */
    public void removeMenuItem(ReservationMenuItem menuItem) {
        menuItems.remove(menuItem);
        menuItem.setReservation(null);
    }

    /**
     * Gets the menu items selected for this reservation.
     *
     * @return The set of menu items
     */
    public Set<ReservationMenuItem> getMenuItems() {
        return menuItems;
    }

    /**
     * Calculates the expected end time of the reservation.
     *
     * @return The end time of the reservation
     */
    public LocalDateTime getEndTime() {
        return reservationTime != null ? reservationTime.plusMinutes(durationMinutes) : null;
    }

    /**
     * Checks if the reservation is in a final state (completed, cancelled, or no-show).
     *
     * @return true if the reservation is in a final state, false otherwise
     */
    public boolean isInFinalState() {
        if (status == null) {
            return false;
        }
        return StatusCodes.RESERVATION_COMPLETED.equals(status) ||
               StatusCodes.RESERVATION_CANCELLED.equals(status) ||
               StatusCodes.RESERVATION_NO_SHOW.equals(status);
    }

    /**
     * Checks if the reservation is active (pending or confirmed).
     *
     * @return true if the reservation is active, false otherwise
     */
    public boolean isActive() {
        if (status == null) {
            return false;
        }
        return StatusCodes.RESERVATION_PENDING.equals(status) ||
               StatusCodes.RESERVATION_CONFIRMED.equals(status);
    }

    /**
     * Checks if the reservation is confirmed.
     *
     * @return true if the reservation is confirmed, false otherwise
     */
    public boolean isConfirmed() {
        return StatusCodes.RESERVATION_CONFIRMED.equals(status);
    }

    /**
     * Checks if the reservation is pending.
     *
     * @return true if the reservation is pending, false otherwise
     */
    public boolean isPending() {
        return StatusCodes.RESERVATION_PENDING.equals(status);
    }

    /**
     * Checks if the reservation is cancelled.
     *
     * @return true if the reservation is cancelled, false otherwise
     */
    public boolean isCancelled() {
        return StatusCodes.RESERVATION_CANCELLED.equals(status);
    }

    /**
     * Checks if the reservation is completed.
     *
     * @return true if the reservation is completed, false otherwise
     */
    public boolean isCompleted() {
        return StatusCodes.RESERVATION_COMPLETED.equals(status);
    }

    /**
     * Checks if the reservation is marked as no-show.
     *
     * @return true if the reservation is marked as no-show, false otherwise
     */
    public boolean isNoShow() {
        return StatusCodes.RESERVATION_NO_SHOW.equals(status);
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
     * @param durationMinutes The duration in minutes to set
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
     * @param customerPhone The customer phone number to set
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
     * @param customerEmail The customer email to set
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
     * @param remindersEnabled true to enable reminders, false otherwise
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
     * @param updatedAt The last update timestamp to set
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
     * @param confirmationDeadline The confirmation deadline to set
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", reservationTime=" + reservationTime +
                ", partySize=" + partySize +
                ", status='" + status + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}