package com.restaurant.restaurant.dto;

/**
 * Data Transfer Object for restaurant tables.
 * This class provides:
 * - Table identification and capacity management
 * - Status tracking (available, occupied, reserved)
 * - Location and accessibility information
 * - Special features and configuration options
 * 
 * Used to transfer table data between the service layer
 * and the presentation layer.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class TableDTO {

    /** Unique identifier for the table */
    private String id;

    /** ID of the restaurant this table belongs to */
    private String restaurantId;

    /** Unique number or identifier for the table within the restaurant */
    private String tableNumber;

    /** Maximum number of people the table can accommodate */
    private int capacity;

    /** Current status of the table (AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE) */
    private String status;

    /** Location of the table (INDOOR, OUTDOOR, PRIVATE_ROOM, etc.) */
    private String location;

    /** Flag indicating if the table is accessible for people with disabilities */
    private boolean accessible;

    /** Shape of the table (SQUARE, ROUND, RECTANGULAR, etc.) */
    private String shape;

    /** Minimum number of people recommended for this table */
    private int minCapacity;

    /** Flag indicating if this table can be combined with others */
    private boolean combinable;

    /** Special features or characteristics of the table (e.g., "Near window", "Quiet corner") */
    private String specialFeatures;

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
     * Gets the ID of the restaurant this table belongs to.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the ID of the restaurant this table belongs to.
     *
     * @param restaurantId The restaurant ID
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
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