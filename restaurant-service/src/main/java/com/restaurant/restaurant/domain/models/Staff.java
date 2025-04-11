package com.restaurant.restaurant.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity class representing a staff member in a restaurant.
 * This class provides:
 * - Staff member identification and contact information
 * - Position and role management
 * - Restaurant association
 * - Active status tracking
 * 
 * The entity is mapped to the 'staff' table in the database
 * and includes relationships with the Restaurant entity.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "staff")
public class Staff {

    /** Unique identifier for the staff member */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** The restaurant this staff member belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    /** Full name of the staff member */
    @Column(nullable = false)
    private String name;

    /** Position or role of the staff member (e.g., MANAGER, CHEF, WAITER, HOST) */
    @Column(nullable = false)
    private String position;

    /** Contact phone number of the staff member */
    private String phoneNumber;

    /** Email address of the staff member */
    private String email;
    
    /** Active status of the staff member */
    @Column(nullable = false)
    private boolean active = true;

    /** User account ID if linked to a user account */
    private String userId;

    /** Additional notes or information about the staff member */
    private String notes;

    /**
     * Default constructor for JPA.
     */
    public Staff() {
    }

    /**
     * Constructs a new Staff member with basic information.
     *
     * @param restaurant The restaurant this staff member belongs to
     * @param name Full name of the staff member
     * @param position Position or role of the staff member
     */
    public Staff(Restaurant restaurant, String name, String position) {
        this.restaurant = restaurant;
        this.name = name;
        this.position = position;
    }

    /**
     * Gets the unique identifier of the staff member.
     *
     * @return The staff member's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the staff member.
     *
     * @param id The staff member's ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the restaurant this staff member belongs to.
     *
     * @return The associated restaurant
     */
    public Restaurant getRestaurant() {
        return restaurant;
    }

    /**
     * Sets the restaurant this staff member belongs to.
     *
     * @param restaurant The associated restaurant
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    /**
     * Gets the full name of the staff member.
     *
     * @return The staff member's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the full name of the staff member.
     *
     * @param name The staff member's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the position or role of the staff member.
     *
     * @return The staff member's position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the position or role of the staff member.
     *
     * @param position The staff member's position
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Gets the contact phone number of the staff member.
     *
     * @return The staff member's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the contact phone number of the staff member.
     *
     * @param phoneNumber The staff member's phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the email address of the staff member.
     *
     * @return The staff member's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the staff member.
     *
     * @param email The staff member's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Checks if the staff member is active.
     *
     * @return true if the staff member is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of the staff member.
     *
     * @param active The staff member's active status
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the user account ID if linked to a user account.
     *
     * @return The linked user account ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user account ID if linked to a user account.
     *
     * @param userId The linked user account ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets additional notes or information about the staff member.
     *
     * @return Notes about the staff member
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets additional notes or information about the staff member.
     *
     * @param notes Notes about the staff member
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}