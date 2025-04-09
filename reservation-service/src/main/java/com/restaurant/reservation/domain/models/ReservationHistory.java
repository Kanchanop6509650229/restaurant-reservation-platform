package com.restaurant.reservation.domain.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing the history of changes made to a reservation.
 * This class tracks all significant events and modifications that occur
 * throughout the lifecycle of a reservation, providing an audit trail
 * for accountability and tracking purposes.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "reservation_history")
public class ReservationHistory {

    /** Unique identifier for the history record */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** The reservation this history record belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    /** 
     * The type of action performed on the reservation.
     * Possible values: CREATED, CONFIRMED, CANCELLED, MODIFIED, COMPLETED, NO_SHOW
     */
    @Column(nullable = false)
    private String action;

    /** The date and time when the action was performed */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /** 
     * Additional details about the action performed.
     * Can include specific changes made, reasons for actions, or other relevant information
     */
    @Column(length = 1000)
    private String details;

    /** 
     * The ID of the user who performed the action.
     * Can be a customer, staff member, or system user
     */
    private String performedBy;

    /**
     * Default constructor required by JPA.
     * Initializes the timestamp to the current date and time.
     */
    public ReservationHistory() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a new history record for a reservation action.
     *
     * @param reservation The reservation being modified
     * @param action The type of action performed
     * @param details Additional information about the action
     * @param performedBy The ID of the user who performed the action
     */
    public ReservationHistory(Reservation reservation, String action, String details, String performedBy) {
        this.reservation = reservation;
        this.action = action;
        this.details = details;
        this.performedBy = performedBy;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters with JavaDoc comments
    /**
     * Gets the unique identifier of the history record.
     *
     * @return The history record ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the history record.
     *
     * @param id The ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the reservation this history record belongs to.
     *
     * @return The associated reservation
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * Sets the reservation this history record belongs to.
     *
     * @param reservation The reservation to associate with this history record
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    /**
     * Gets the type of action performed on the reservation.
     *
     * @return The action type (CREATED, CONFIRMED, CANCELLED, etc.)
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the type of action performed on the reservation.
     *
     * @param action The action type to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets the date and time when the action was performed.
     *
     * @return The timestamp of the action
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the date and time when the action was performed.
     *
     * @param timestamp The timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the additional details about the action performed.
     *
     * @return The action details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets the additional details about the action performed.
     *
     * @param details The details to set
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Gets the ID of the user who performed the action.
     *
     * @return The user ID
     */
    public String getPerformedBy() {
        return performedBy;
    }

    /**
     * Sets the ID of the user who performed the action.
     *
     * @param performedBy The user ID to set
     */
    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }
}
