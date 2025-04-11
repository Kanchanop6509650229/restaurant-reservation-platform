package com.restaurant.restaurant.dto;

import jakarta.validation.constraints.Min;

/**
 * Data Transfer Object for updating an existing restaurant table.
 * This class provides:
 * - Table identification and capacity updates
 * - Location and accessibility information updates
 * - Special features and configuration updates
 * - Input validation constraints
 * 
 * Used to validate and transfer table update data from the presentation layer
 * to the service layer.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class TableUpdateRequest {

    /** Unique number or identifier for the table */
    private String tableNumber;
    
    /** Maximum number of people the table can accommodate (minimum 1) */
    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;
    
    /** Location of the table (INDOOR, OUTDOOR, PRIVATE_ROOM, etc.) */
    private String location;

    /** Flag indicating if the table is accessible for people with disabilities */
    private boolean accessible;

    /** Shape of the table (SQUARE, ROUND, RECTANGULAR, etc.) */
    private String shape;
    
    /** Minimum number of people recommended for this table (non-negative) */
    @Min(value = 0, message = "Minimum capacity cannot be negative")
    private int minCapacity;
    
    /** Flag indicating if this table can be combined with others */
    private boolean combinable;

    /** Special features or characteristics of the table (e.g., "Near window", "Quiet corner") */
    private String specialFeatures;

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