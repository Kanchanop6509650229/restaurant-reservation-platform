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
 * Entity class representing a table in a restaurant.
 * This class provides:
 * - Table identification and capacity management
 * - Status tracking (available, occupied, reserved)
 * - Location and accessibility information
 * - Special features and configuration options
 * 
 * The entity is mapped to the 'restaurant_tables' table in the database
 * and includes relationships with the Restaurant entity.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {

    /** Unique identifier for the table */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** The restaurant this table belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    /** Unique number or identifier for the table within the restaurant */
    @Column(nullable = false)
    private String tableNumber;

    /** Maximum number of people the table can accommodate */
    @Column(nullable = false)
    private int capacity;

    /** Current status of the table (AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE) */
    @Column(nullable = false)
    private String status;

    /** Location of the table (INDOOR, OUTDOOR, PRIVATE_ROOM, etc.) */
    private String location;

    /** Flag indicating if the table is accessible for people with disabilities */
    @Column(name = "`accessible`")
    private boolean accessible;

    /** Active status of the table */
    @Column(nullable = false)
    private boolean active = true;

    /** Shape of the table (SQUARE, ROUND, RECTANGULAR, etc.) */
    private String shape;

    /** Minimum number of people recommended for this table */
    private int minCapacity;

    /** Flag indicating if this table can be combined with others */
    private boolean combinable;

    /** Special features or characteristics of the table (e.g., "Near window", "Quiet corner") */
    private String specialFeatures;

    /**
     * Default constructor for JPA.
     */
    public RestaurantTable() {
    }

    /**
     * Constructs a new RestaurantTable with basic information.
     *
     * @param restaurant The restaurant this table belongs to
     * @param tableNumber The unique table number
     * @param capacity The maximum capacity of the table
     * @param status The initial status of the table
     */
    public RestaurantTable(Restaurant restaurant, String tableNumber, int capacity, String status) {
        this.restaurant = restaurant;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
    }

    /**
     * Gets the unique identifier of the table.
     *
     * @return The table ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the table.
     *
     * @param id The table ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the restaurant this table belongs to.
     *
     * @return The associated restaurant
     */
    public Restaurant getRestaurant() {
        return restaurant;
    }

    /**
     * Sets the restaurant this table belongs to.
     *
     * @param restaurant The associated restaurant
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    /**
     * Gets the unique table number.
     *
     * @return The table number
     */
    public String getTableNumber() {
        return tableNumber;
    }

    /**
     * Sets the unique table number.
     *
     * @param tableNumber The table number
     */
    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    /**
     * Gets the maximum capacity of the table.
     *
     * @return The table capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the maximum capacity of the table.
     *
     * @param capacity The table capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the current status of the table.
     *
     * @return The table status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the table.
     *
     * @param status The table status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the location of the table.
     *
     * @return The table location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the table.
     *
     * @param location The table location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Checks if the table is accessible.
     *
     * @return true if the table is accessible, false otherwise
     */
    public boolean isAccessible() {
        return accessible;
    }

    /**
     * Sets whether the table is accessible.
     *
     * @param accessible The accessibility status
     */
    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    /**
     * Checks if the table is active.
     *
     * @return true if the table is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether the table is active.
     *
     * @param active The active status
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the shape of the table.
     *
     * @return The table shape
     */
    public String getShape() {
        return shape;
    }

    /**
     * Sets the shape of the table.
     *
     * @param shape The table shape
     */
    public void setShape(String shape) {
        this.shape = shape;
    }

    /**
     * Gets the minimum capacity of the table.
     *
     * @return The minimum capacity
     */
    public int getMinCapacity() {
        return minCapacity;
    }

    /**
     * Sets the minimum capacity of the table.
     *
     * @param minCapacity The minimum capacity
     */
    public void setMinCapacity(int minCapacity) {
        this.minCapacity = minCapacity;
    }

    /**
     * Checks if the table can be combined with others.
     *
     * @return true if the table is combinable, false otherwise
     */
    public boolean isCombinable() {
        return combinable;
    }

    /**
     * Sets whether the table can be combined with others.
     *
     * @param combinable The combinable status
     */
    public void setCombinable(boolean combinable) {
        this.combinable = combinable;
    }

    /**
     * Gets the special features of the table.
     *
     * @return The special features
     */
    public String getSpecialFeatures() {
        return specialFeatures;
    }

    /**
     * Sets the special features of the table.
     *
     * @param specialFeatures The special features
     */
    public void setSpecialFeatures(String specialFeatures) {
        this.specialFeatures = specialFeatures;
    }
}