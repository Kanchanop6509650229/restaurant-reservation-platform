package com.restaurant.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Min;

/**
 * Data Transfer Object for restaurant search criteria.
 * This class captures the criteria for searching available restaurants
 * based on reservation requirements.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class RestaurantSearchCriteriaDTO {

    /** Restaurant ID for direct lookup */
    private String restaurantId;

    /** Restaurant name for name-based search */
    private String restaurantName;

    /** Date for the reservation */
    private LocalDate date;

    /** Time for the reservation */
    private LocalTime time;

    /** Number of people in the party */
    private Integer partySize;

    /** Type of cuisine (optional filter) */
    private String cuisineType;

    /** City location (optional filter) */
    private String city;

    /** Latitude coordinate for location-based search */
    private Double latitude;

    /** Longitude coordinate for location-based search */
    private Double longitude;

    /** Maximum distance in kilometers for location-based search */
    @Min(value = 0, message = "Distance must be non-negative")
    private Double distance;

    /**
     * Default constructor.
     */
    public RestaurantSearchCriteriaDTO() {
    }

    /**
     * Gets the restaurant ID for direct lookup.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the restaurant ID for direct lookup.
     *
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Gets the restaurant name for name-based search.
     *
     * @return The restaurant name
     */
    public String getRestaurantName() {
        return restaurantName;
    }

    /**
     * Sets the restaurant name for name-based search.
     *
     * @param restaurantName The restaurant name to set
     */
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    /**
     * Gets the date for the reservation.
     *
     * @return The reservation date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date for the reservation.
     *
     * @param date The reservation date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the time for the reservation.
     *
     * @return The reservation time
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Sets the time for the reservation.
     *
     * @param time The reservation time to set
     */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    /**
     * Gets the number of people in the party.
     *
     * @return The party size
     */
    public Integer getPartySize() {
        return partySize;
    }

    /**
     * Sets the number of people in the party.
     *
     * @param partySize The party size to set
     */
    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }

    /**
     * Gets the type of cuisine filter.
     *
     * @return The cuisine type
     */
    public String getCuisineType() {
        return cuisineType;
    }

    /**
     * Sets the type of cuisine filter.
     *
     * @param cuisineType The cuisine type to set
     */
    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    /**
     * Gets the city location filter.
     *
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city location filter.
     *
     * @param city The city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the latitude coordinate.
     *
     * @return The latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude coordinate.
     *
     * @param latitude The latitude to set
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude coordinate.
     *
     * @return The longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude coordinate.
     *
     * @param longitude The longitude to set
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the maximum distance in kilometers.
     *
     * @return The distance
     */
    public Double getDistance() {
        return distance;
    }

    /**
     * Sets the maximum distance in kilometers.
     *
     * @param distance The distance to set
     */
    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
