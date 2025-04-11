package com.restaurant.restaurant.dto;

/**
 * Data Transfer Object for restaurant search criteria.
 * This class provides:
 * - Keyword-based search
 * - Cuisine type filtering
 * - Location-based search
 * - Distance-based filtering
 * 
 * Used to transfer search criteria from the presentation layer
 * to the service layer for restaurant search operations.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class RestaurantSearchCriteria {

    /** Search keyword for restaurant name or description */
    private String keyword;

    /** Type of cuisine to filter restaurants */
    private String cuisineType;

    /** City to search for restaurants */
    private String city;

    /** Latitude coordinate for location-based search */
    private double latitude;

    /** Longitude coordinate for location-based search */
    private double longitude;

    /** Maximum distance in kilometers for location-based search */
    private double distance; // in kilometers

    /**
     * Gets the search keyword.
     *
     * @return The search keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Sets the search keyword.
     *
     * @param keyword The search keyword
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Gets the cuisine type filter.
     *
     * @return The cuisine type
     */
    public String getCuisineType() {
        return cuisineType;
    }

    /**
     * Sets the cuisine type filter.
     *
     * @param cuisineType The cuisine type
     */
    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    /**
     * Gets the city filter.
     *
     * @return The city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city filter.
     *
     * @param city The city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the latitude coordinate.
     *
     * @return The latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude coordinate.
     *
     * @param latitude The latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude coordinate.
     *
     * @return The longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude coordinate.
     *
     * @param longitude The longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the maximum search distance in kilometers.
     *
     * @return The maximum distance in kilometers
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the maximum search distance in kilometers.
     *
     * @param distance The maximum distance in kilometers
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
}