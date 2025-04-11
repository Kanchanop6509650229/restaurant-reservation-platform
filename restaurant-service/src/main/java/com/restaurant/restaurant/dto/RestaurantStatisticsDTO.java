package com.restaurant.restaurant.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for restaurant statistics and analytics.
 * This class provides:
 * - Restaurant identification
 * - Table capacity and availability metrics
 * - Table status tracking
 * - Customer rating statistics
 * 
 * Used to transfer restaurant performance and operational data
 * between the service and presentation layers.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class RestaurantStatisticsDTO {
    /** Unique identifier of the restaurant */
    private String restaurantId;

    /** Name of the restaurant */
    private String restaurantName;

    /** Total number of tables in the restaurant */
    private int totalTables;

    /** Total seating capacity across all tables */
    private int totalCapacity;

    /** Number of tables currently available for reservation */
    private int availableTables;

    /** Number of tables currently reserved */
    private int reservedTables;

    /** Number of tables currently occupied by customers */
    private int occupiedTables;

    /** Average customer rating of the restaurant */
    private BigDecimal averageRating;

    /** Total number of customer ratings received */
    private int totalRatings;
    
    /**
     * Default constructor for RestaurantStatisticsDTO.
     * Initializes a new instance with default values.
     */
    public RestaurantStatisticsDTO() {
    }
    
    /**
     * Constructor for RestaurantStatisticsDTO with basic restaurant information.
     *
     * @param restaurantId The unique identifier of the restaurant
     * @param restaurantName The name of the restaurant
     */
    public RestaurantStatisticsDTO(String restaurantId, String restaurantName) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }
    
    /**
     * Gets the unique identifier of the restaurant.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the unique identifier of the restaurant.
     *
     * @param restaurantId The restaurant ID
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Gets the name of the restaurant.
     *
     * @return The restaurant name
     */
    public String getRestaurantName() {
        return restaurantName;
    }

    /**
     * Sets the name of the restaurant.
     *
     * @param restaurantName The restaurant name
     */
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    /**
     * Gets the total number of tables in the restaurant.
     *
     * @return The total number of tables
     */
    public int getTotalTables() {
        return totalTables;
    }

    /**
     * Sets the total number of tables in the restaurant.
     *
     * @param totalTables The total number of tables
     */
    public void setTotalTables(int totalTables) {
        this.totalTables = totalTables;
    }

    /**
     * Gets the total seating capacity across all tables.
     *
     * @return The total seating capacity
     */
    public int getTotalCapacity() {
        return totalCapacity;
    }

    /**
     * Sets the total seating capacity across all tables.
     *
     * @param totalCapacity The total seating capacity
     */
    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    /**
     * Gets the number of tables currently available for reservation.
     *
     * @return The number of available tables
     */
    public int getAvailableTables() {
        return availableTables;
    }

    /**
     * Sets the number of tables currently available for reservation.
     *
     * @param availableTables The number of available tables
     */
    public void setAvailableTables(int availableTables) {
        this.availableTables = availableTables;
    }

    /**
     * Gets the number of tables currently reserved.
     *
     * @return The number of reserved tables
     */
    public int getReservedTables() {
        return reservedTables;
    }

    /**
     * Sets the number of tables currently reserved.
     *
     * @param reservedTables The number of reserved tables
     */
    public void setReservedTables(int reservedTables) {
        this.reservedTables = reservedTables;
    }

    /**
     * Gets the number of tables currently occupied by customers.
     *
     * @return The number of occupied tables
     */
    public int getOccupiedTables() {
        return occupiedTables;
    }

    /**
     * Sets the number of tables currently occupied by customers.
     *
     * @param occupiedTables The number of occupied tables
     */
    public void setOccupiedTables(int occupiedTables) {
        this.occupiedTables = occupiedTables;
    }

    /**
     * Gets the average customer rating of the restaurant.
     *
     * @return The average rating
     */
    public BigDecimal getAverageRating() {
        return averageRating;
    }

    /**
     * Sets the average customer rating of the restaurant.
     *
     * @param averageRating The average rating
     */
    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Gets the total number of customer ratings received.
     *
     * @return The total number of ratings
     */
    public int getTotalRatings() {
        return totalRatings;
    }

    /**
     * Sets the total number of customer ratings received.
     *
     * @param totalRatings The total number of ratings
     */
    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }
}