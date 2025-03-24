package com.restaurant.restaurant.dto;

import java.math.BigDecimal;

public class RestaurantStatisticsDTO {
    private String restaurantId;
    private String restaurantName;
    private int totalTables;
    private int totalCapacity;
    private int availableTables;
    private int reservedTables;
    private int occupiedTables;
    private BigDecimal averageRating;
    private int totalRatings;
    
    // Constructors
    public RestaurantStatisticsDTO() {
    }
    
    public RestaurantStatisticsDTO(String restaurantId, String restaurantName) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }
    
    // Getters and setters
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public int getTotalTables() {
        return totalTables;
    }

    public void setTotalTables(int totalTables) {
        this.totalTables = totalTables;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getAvailableTables() {
        return availableTables;
    }

    public void setAvailableTables(int availableTables) {
        this.availableTables = availableTables;
    }

    public int getReservedTables() {
        return reservedTables;
    }

    public void setReservedTables(int reservedTables) {
        this.reservedTables = reservedTables;
    }

    public int getOccupiedTables() {
        return occupiedTables;
    }

    public void setOccupiedTables(int occupiedTables) {
        this.occupiedTables = occupiedTables;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }
}