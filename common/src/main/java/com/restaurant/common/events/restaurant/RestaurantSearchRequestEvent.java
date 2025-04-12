package com.restaurant.common.events.restaurant;

import java.time.LocalDate;
import java.time.LocalTime;

import com.restaurant.common.events.BaseEvent;

/**
 * Event representing a request to search for available restaurants.
 * This event is published when a user wants to find restaurants that match
 * specific criteria including date, time, party size, and other filters.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class RestaurantSearchRequestEvent extends BaseEvent implements RestaurantEvent {
    
    /** Date for the reservation */
    private LocalDate date;
    
    /** Time for the reservation */
    private LocalTime time;
    
    /** Number of people in the party */
    private int partySize;
    
    /** Type of cuisine (optional filter) */
    private String cuisineType;
    
    /** City location (optional filter) */
    private String city;
    
    /** Latitude coordinate for location-based search */
    private Double latitude;
    
    /** Longitude coordinate for location-based search */
    private Double longitude;
    
    /** Maximum distance in kilometers for location-based search */
    private Double distance;
    
    /** Correlation ID for tracking the request-response cycle */
    private String correlationId;
    
    /** Restaurant ID if searching for a specific restaurant */
    private String restaurantId;

    /**
     * Default constructor.
     * Initializes a new restaurant search request event.
     */
    public RestaurantSearchRequestEvent() {
        super("RESTAURANT_SEARCH_REQUEST");
    }

    /**
     * Creates a new restaurant search request event with all required details.
     *
     * @param date Date for the reservation
     * @param time Time for the reservation
     * @param partySize Number of people in the party
     * @param correlationId Correlation ID for tracking the request-response cycle
     */
    public RestaurantSearchRequestEvent(LocalDate date, LocalTime time, int partySize, String correlationId) {
        super("RESTAURANT_SEARCH_REQUEST");
        this.date = date;
        this.time = time;
        this.partySize = partySize;
        this.correlationId = correlationId;
        this.restaurantId = null; // Not searching for a specific restaurant
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
    public int getPartySize() {
        return partySize;
    }

    /**
     * Sets the number of people in the party.
     *
     * @param partySize The party size to set
     */
    public void setPartySize(int partySize) {
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

    /**
     * Gets the correlation ID for tracking the request-response cycle.
     *
     * @return The correlation ID
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the correlation ID for tracking the request-response cycle.
     *
     * @param correlationId The correlation ID to set
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Gets the restaurant ID if searching for a specific restaurant.
     *
     * @return The restaurant ID, or null if not searching for a specific restaurant
     */
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the restaurant ID if searching for a specific restaurant.
     *
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
