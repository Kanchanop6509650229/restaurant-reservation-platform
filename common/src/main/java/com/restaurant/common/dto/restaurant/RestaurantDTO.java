package com.restaurant.common.dto.restaurant;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for restaurant information.
 * This class represents a restaurant's complete information including
 * basic details, location, capacity, and operating hours.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class RestaurantDTO {
    /** Unique identifier for the restaurant */
    private String id;
    
    /** Name of the restaurant */
    private String name;
    
    /** Description of the restaurant */
    private String description;
    
    /** Physical address of the restaurant */
    private String address;
    
    /** Contact phone number */
    private String phoneNumber;
    
    /** Contact email address */
    private String email;
    
    /** Restaurant's website URL */
    private String website;
    
    /** Geographic latitude coordinate */
    private double latitude;
    
    /** Geographic longitude coordinate */
    private double longitude;
    
    /** Type of cuisine served */
    private String cuisineType;
    
    /** Maximum seating capacity */
    private int capacity;
    
    /** Average customer rating */
    private BigDecimal averageRating;
    
    /** Whether the restaurant is currently active */
    private boolean active;
    
    /** List of operating hours for different days */
    private List<OperatingHoursDTO> operatingHours;
    
    /** ID of the restaurant owner */
    private String ownerId;
    
    /**
     * Default constructor.
     */
    public RestaurantDTO() {
    }
    
    /**
     * Gets the restaurant's unique identifier.
     *
     * @return The restaurant ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the restaurant's unique identifier.
     *
     * @param id The restaurant ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the restaurant's name.
     *
     * @return The restaurant name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the restaurant's name.
     *
     * @param name The restaurant name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the restaurant's description.
     *
     * @return The restaurant description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the restaurant's description.
     *
     * @param description The restaurant description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the restaurant's address.
     *
     * @return The restaurant address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the restaurant's address.
     *
     * @param address The restaurant address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the restaurant's phone number.
     *
     * @return The restaurant phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the restaurant's phone number.
     *
     * @param phoneNumber The restaurant phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the restaurant's email address.
     *
     * @return The restaurant email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the restaurant's email address.
     *
     * @param email The restaurant email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the restaurant's website URL.
     *
     * @return The restaurant website URL
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the restaurant's website URL.
     *
     * @param website The restaurant website URL to set
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Gets the restaurant's latitude coordinate.
     *
     * @return The restaurant latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the restaurant's latitude coordinate.
     *
     * @param latitude The restaurant latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the restaurant's longitude coordinate.
     *
     * @return The restaurant longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the restaurant's longitude coordinate.
     *
     * @param longitude The restaurant longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the restaurant's cuisine type.
     *
     * @return The restaurant cuisine type
     */
    public String getCuisineType() {
        return cuisineType;
    }

    /**
     * Sets the restaurant's cuisine type.
     *
     * @param cuisineType The restaurant cuisine type to set
     */
    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    /**
     * Gets the restaurant's seating capacity.
     *
     * @return The restaurant capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the restaurant's seating capacity.
     *
     * @param capacity The restaurant capacity to set
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the restaurant's average rating.
     *
     * @return The restaurant average rating
     */
    public BigDecimal getAverageRating() {
        return averageRating;
    }

    /**
     * Sets the restaurant's average rating.
     *
     * @param averageRating The restaurant average rating to set
     */
    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Checks if the restaurant is active.
     *
     * @return true if the restaurant is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the restaurant's active status.
     *
     * @param active The restaurant active status to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the restaurant's operating hours.
     *
     * @return List of operating hours for different days
     */
    public List<OperatingHoursDTO> getOperatingHours() {
        return operatingHours;
    }

    /**
     * Sets the restaurant's operating hours.
     *
     * @param operatingHours List of operating hours to set
     */
    public void setOperatingHours(List<OperatingHoursDTO> operatingHours) {
        this.operatingHours = operatingHours;
    }

    /**
     * Gets the restaurant owner's ID.
     *
     * @return The restaurant owner ID
     */
    public String getOwnerId() {
        return ownerId;
    }
    
    /**
     * Sets the restaurant owner's ID.
     *
     * @param ownerId The restaurant owner ID to set
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}