package com.restaurant.common.events.reservation;

import java.time.LocalDateTime;

import com.restaurant.common.events.BaseEvent;

/**
 * Event representing a request to find available tables.
 * This event is published when a customer wants to find available tables
 * at a restaurant for a specific time period and party size.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class FindAvailableTableRequestEvent extends BaseEvent {

    /** Unique identifier of the reservation request */
    private String reservationId;
    
    /** ID of the restaurant to search for available tables */
    private String restaurantId;
    
    /** Start time of the desired reservation period */
    private LocalDateTime startTime;
    
    /** End time of the desired reservation period */
    private LocalDateTime endTime;
    
    /** Number of people in the party */
    private int partySize;
    
    /** Correlation ID for tracking the request-response cycle */
    private String correlationId;

    /**
     * Default constructor.
     * Initializes a new find available table request event.
     */
    public FindAvailableTableRequestEvent() {
        super("FIND_AVAILABLE_TABLE_REQUEST");
    }

    /**
     * Creates a new find available table request event with all required details.
     *
     * @param reservationId Unique identifier of the reservation request
     * @param restaurantId ID of the restaurant
     * @param startTime Start time of the desired reservation period
     * @param endTime End time of the desired reservation period
     * @param partySize Number of people in the party
     * @param correlationId Correlation ID for tracking the request-response cycle
     */
    public FindAvailableTableRequestEvent(String reservationId, String restaurantId,
            LocalDateTime startTime, LocalDateTime endTime,
            int partySize, String correlationId) {
        super("FIND_AVAILABLE_TABLE_REQUEST");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.partySize = partySize;
        this.correlationId = correlationId;
    }

    /**
     * Sets the unique identifier of the reservation request.
     *
     * @param reservationId The reservation ID to set
     */
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    /**
     * Sets the ID of the restaurant to search for available tables.
     *
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Sets the start time of the desired reservation period.
     *
     * @param startTime The start time to set
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the end time of the desired reservation period.
     *
     * @param endTime The end time to set
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
     * Sets the correlation ID for tracking the request-response cycle.
     *
     * @param correlationId The correlation ID to set
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Gets the unique identifier of the reservation request.
     *
     * @return The reservation ID
     */
    public String getReservationId() {
        return reservationId;
    }

    /**
     * Gets the ID of the restaurant to search for available tables.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Gets the start time of the desired reservation period.
     *
     * @return The start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time of the desired reservation period.
     *
     * @return The end time
     */
    public LocalDateTime getEndTime() {
        return endTime;
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
     * Gets the correlation ID for tracking the request-response cycle.
     *
     * @return The correlation ID
     */
    public String getCorrelationId() {
        return correlationId;
    }
}