package com.restaurant.common.dto.restaurant;

/**
 * Data Transfer Object for restaurant table information.
 * This class represents a table's details including its capacity,
 * status, location, and accessibility features.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class TableDTO {
    /** Unique identifier for the table */
    private String id;
    
    /** ID of the restaurant this table belongs to */
    private String restaurantId;
    
    /** Table number or identifier within the restaurant */
    private String tableNumber;
    
    /** Maximum number of people that can be seated at the table */
    private int capacity;
    
    /** Current status of the table (AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE) */
    private String status;
    
    /** Physical location of the table (e.g., "INDOOR", "OUTDOOR", "PRIVATE_ROOM") */
    private String location;
    
    /** Whether the table is accessible for people with disabilities */
    private boolean accessible;
    
    /**
     * Default constructor.
     */
    public TableDTO() {
    }
    
    /**
     * Gets the table's unique identifier.
     *
     * @return The table ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the table's unique identifier.
     *
     * @param id The table ID to set
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
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Gets the table number or identifier.
     *
     * @return The table number
     */
    public String getTableNumber() {
        return tableNumber;
    }

    /**
     * Sets the table number or identifier.
     *
     * @param tableNumber The table number to set
     */
    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    /**
     * Gets the table's seating capacity.
     *
     * @return The maximum number of people that can be seated
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the table's seating capacity.
     *
     * @param capacity The maximum number of people that can be seated
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the current status of the table.
     *
     * @return The table status (AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the table.
     *
     * @param status The table status to set (AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE)
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the physical location of the table.
     *
     * @return The table location (e.g., "INDOOR", "OUTDOOR", "PRIVATE_ROOM")
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the physical location of the table.
     *
     * @param location The table location to set (e.g., "INDOOR", "OUTDOOR", "PRIVATE_ROOM")
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Checks if the table is accessible for people with disabilities.
     *
     * @return true if the table is accessible, false otherwise
     */
    public boolean isAccessible() {
        return accessible;
    }

    /**
     * Sets whether the table is accessible for people with disabilities.
     *
     * @param accessible true if the table is accessible, false otherwise
     */
    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }
}