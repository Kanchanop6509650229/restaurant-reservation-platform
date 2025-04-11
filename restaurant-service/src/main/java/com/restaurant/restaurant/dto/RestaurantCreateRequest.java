package com.restaurant.restaurant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating a new restaurant.
 * This class provides:
 * - Restaurant basic information management
 * - Contact and location details
 * - Business configuration
 * - Input validation constraints
 * 
 * Used to validate and transfer restaurant creation data from the presentation layer
 * to the service layer.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class RestaurantCreateRequest {

    /** Name of the restaurant (required, max 255 characters) */
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    /** Detailed description of the restaurant (max 1000 characters) */
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    /** Street address of the restaurant (required, max 255 characters) */
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    /** City where the restaurant is located */
    private String city;

    /** State or province where the restaurant is located */
    private String state;

    /** Postal or ZIP code of the restaurant location */
    private String zipCode;

    /** Country where the restaurant is located */
    private String country;

    /** Contact phone number (required, max 20 characters) */
    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    /** Contact email address (must be valid email format) */
    @Email(message = "Email should be valid")
    private String email;

    /** Website URL of the restaurant */
    private String website;

    /** Latitude coordinate of the restaurant location */
    private double latitude;

    /** Longitude coordinate of the restaurant location */
    private double longitude;

    /** Type of cuisine served at the restaurant (required) */
    @NotBlank(message = "Cuisine type is required")
    private String cuisineType;

    /** ID of the restaurant owner (required) */
    @NotBlank(message = "Owner ID is required")
    private String ownerId;

    /** Total seating capacity of the restaurant */
    private int totalCapacity;

    /**
     * Gets the name of the restaurant.
     *
     * @return The restaurant name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the restaurant.
     *
     * @param name The restaurant name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the restaurant.
     *
     * @return The restaurant description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the restaurant.
     *
     * @param description The restaurant description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the street address of the restaurant.
     *
     * @return The restaurant address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the street address of the restaurant.
     *
     * @param address The restaurant address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the city where the restaurant is located.
     *
     * @return The city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city where the restaurant is located.
     *
     * @param city The city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state or province where the restaurant is located.
     *
     * @return The state or province name
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state or province where the restaurant is located.
     *
     * @param state The state or province name
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the postal or ZIP code of the restaurant location.
     *
     * @return The postal or ZIP code
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the postal or ZIP code of the restaurant location.
     *
     * @param zipCode The postal or ZIP code
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Gets the country where the restaurant is located.
     *
     * @return The country name
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country where the restaurant is located.
     *
     * @param country The country name
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the contact phone number of the restaurant.
     *
     * @return The phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the contact phone number of the restaurant.
     *
     * @param phoneNumber The phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the contact email address of the restaurant.
     *
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the contact email address of the restaurant.
     *
     * @param email The email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the website URL of the restaurant.
     *
     * @return The website URL
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the website URL of the restaurant.
     *
     * @param website The website URL
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Gets the latitude coordinate of the restaurant location.
     *
     * @return The latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude coordinate of the restaurant location.
     *
     * @param latitude The latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude coordinate of the restaurant location.
     *
     * @return The longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude coordinate of the restaurant location.
     *
     * @param longitude The longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the type of cuisine served at the restaurant.
     *
     * @return The cuisine type
     */
    public String getCuisineType() {
        return cuisineType;
    }

    /**
     * Sets the type of cuisine served at the restaurant.
     *
     * @param cuisineType The cuisine type
     */
    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    /**
     * Gets the total seating capacity of the restaurant.
     *
     * @return The total capacity
     */
    public int getTotalCapacity() {
        return totalCapacity;
    }

    /**
     * Sets the total seating capacity of the restaurant.
     *
     * @param totalCapacity The total capacity
     */
    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    /**
     * Gets the ID of the restaurant owner.
     *
     * @return The owner ID
     */
    public String getOwnerId() {
        return ownerId;
    }
    
    /**
     * Sets the ID of the restaurant owner.
     *
     * @param ownerId The owner ID
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}