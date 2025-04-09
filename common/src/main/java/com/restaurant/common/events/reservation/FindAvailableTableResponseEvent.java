package com.restaurant.common.events.reservation;

import com.restaurant.common.events.BaseEvent;

/**
 * Event representing the response to a find available table request.
 * This event is published in response to a FindAvailableTableRequestEvent,
 * containing the result of the search for available tables.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class FindAvailableTableResponseEvent extends BaseEvent {
    
    /** Unique identifier of the reservation request */
    private String reservationId;
    
    /** ID of the restaurant that was searched */
    private String restaurantId;
    
    /** ID of the found available table, if any */
    private String tableId;
    
    /** Whether the search was successful */
    private boolean success;
    
    /** Error message in case of failure */
    private String errorMessage;
    
    /** Correlation ID matching the original request */
    private String correlationId;

    /**
     * Default constructor.
     * Initializes a new find available table response event.
     */
    public FindAvailableTableResponseEvent() {
        super("FIND_AVAILABLE_TABLE_RESPONSE");
    }

    /**
     * Creates a new find available table response event with all required details.
     *
     * @param reservationId Unique identifier of the reservation request
     * @param restaurantId ID of the restaurant
     * @param tableId ID of the found available table (null if none found)
     * @param success Whether the search was successful
     * @param errorMessage Error message in case of failure
     * @param correlationId Correlation ID matching the original request
     */
    public FindAvailableTableResponseEvent(String reservationId, String restaurantId, 
                                         String tableId, boolean success, 
                                         String errorMessage, String correlationId) {
        super("FIND_AVAILABLE_TABLE_RESPONSE");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.success = success;
        this.errorMessage = errorMessage;
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
     * Gets the unique identifier of the reservation request.
     *
     * @return The reservation ID
     */
    public String getReservationId() {
        return reservationId;
    }

    /**
     * Sets the ID of the restaurant that was searched.
     *
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    /**
     * Gets the ID of the restaurant that was searched.
     *
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the ID of the found available table.
     *
     * @param tableId The table ID to set
     */
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
    
    /**
     * Gets the ID of the found available table.
     *
     * @return The table ID, or null if no table was found
     */
    public String getTableId() {
        return tableId;
    }

    /**
     * Sets whether the search was successful.
     *
     * @param success true if the search was successful, false otherwise
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    /**
     * Checks if the search was successful.
     *
     * @return true if the search was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the error message in case of failure.
     *
     * @param errorMessage The error message to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    /**
     * Gets the error message in case of failure.
     *
     * @return The error message, or null if the search was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the correlation ID matching the original request.
     *
     * @param correlationId The correlation ID to set
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    
    /**
     * Gets the correlation ID matching the original request.
     *
     * @return The correlation ID
     */
    public String getCorrelationId() {
        return correlationId;
    }
}