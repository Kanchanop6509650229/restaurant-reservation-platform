package com.restaurant.common.dto.restaurant;

public class TableDTO {
    private String id;
    private String restaurantId;
    private String tableNumber;
    private int capacity;
    private String status; // AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE
    private String location; // e.g., "INDOOR", "OUTDOOR", "PRIVATE_ROOM"
    private boolean accessible;
    
    // Constructors
    public TableDTO() {
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }
}