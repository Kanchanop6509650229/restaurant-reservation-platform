package com.restaurant.common.events.reservation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.restaurant.common.events.BaseEvent;

/**
 * Event representing the response to a find available table request.
 * This event is published in response to a FindAvailableTableRequestEvent,
 * containing the result of the search for available tables.
 *
 * @author Restaurant Team
 * @version 1.1
 */
public class FindAvailableTableResponseEvent extends BaseEvent {

    /** Unique identifier of the reservation request */
    private String reservationId;

    /** ID of the restaurant that was searched */
    private String restaurantId;

    /** ID of the found available table, if any (for backward compatibility) */
    private String tableId;

    /** List of IDs of found available tables, if any (supports table combinations) */
    private List<String> tableIds = new ArrayList<>();

    /** Whether the search was successful */
    private boolean success;

    /** Error message in case of failure */
    private String errorMessage;

    /** Correlation ID matching the original request */
    private String correlationId;

    /** Flag indicating if this is a combined table result */
    private boolean combinedTables = false;

    /**
     * Default constructor.
     * Initializes a new find available table response event.
     */
    public FindAvailableTableResponseEvent() {
        super("FIND_AVAILABLE_TABLE_RESPONSE");
    }

    /**
     * Creates a new find available table response event with all required details.
     * This constructor supports a single table ID for backward compatibility.
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
        if (tableId != null) {
            this.tableIds = new ArrayList<>(List.of(tableId));
        }
        this.success = success;
        this.errorMessage = errorMessage;
        this.correlationId = correlationId;
        this.combinedTables = false;
    }

    /**
     * Creates a new find available table response event with multiple table IDs.
     * This constructor supports combinations of tables for larger party sizes.
     *
     * @param reservationId Unique identifier of the reservation request
     * @param restaurantId ID of the restaurant
     * @param tableIds List of IDs of found available tables
     * @param success Whether the search was successful
     * @param errorMessage Error message in case of failure
     * @param correlationId Correlation ID matching the original request
     */
    public FindAvailableTableResponseEvent(String reservationId, String restaurantId,
                                         List<String> tableIds, boolean success,
                                         String errorMessage, String correlationId) {
        super("FIND_AVAILABLE_TABLE_RESPONSE");
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.tableIds = tableIds != null ? new ArrayList<>(tableIds) : new ArrayList<>();
        this.tableId = tableIds != null && !tableIds.isEmpty() ? tableIds.get(0) : null;
        this.success = success;
        this.errorMessage = errorMessage;
        this.correlationId = correlationId;
        this.combinedTables = tableIds != null && tableIds.size() > 1;
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
     * This also updates the first entry in the tableIds list for consistency.
     *
     * @param tableId The table ID to set
     */
    public void setTableId(String tableId) {
        this.tableId = tableId;
        if (tableId != null) {
            if (this.tableIds.isEmpty()) {
                this.tableIds.add(tableId);
            } else {
                this.tableIds.set(0, tableId);
            }
        } else {
            this.tableIds.clear();
        }
        this.combinedTables = false;
    }

    /**
     * Gets the ID of the found available table.
     * For combined tables, this returns the first table ID.
     *
     * @return The table ID, or null if no table was found
     */
    public String getTableId() {
        return tableId;
    }

    /**
     * Sets the list of IDs of found available tables.
     * This also updates the tableId field with the first entry for backward compatibility.
     *
     * @param tableIds The list of table IDs to set
     */
    public void setTableIds(List<String> tableIds) {
        this.tableIds = tableIds != null ? new ArrayList<>(tableIds) : new ArrayList<>();
        this.tableId = this.tableIds.isEmpty() ? null : this.tableIds.get(0);
        this.combinedTables = this.tableIds.size() > 1;
    }

    /**
     * Gets the list of IDs of found available tables.
     *
     * @return The list of table IDs, or an empty list if no tables were found
     */
    public List<String> getTableIds() {
        return new ArrayList<>(tableIds);
    }

    /**
     * Gets a comma-separated string of table IDs.
     * This is useful for storing multiple table IDs in a single field.
     *
     * @return A comma-separated string of table IDs, or null if no tables were found
     */
    public String getTableIdsAsString() {
        if (tableIds == null || tableIds.isEmpty()) {
            return null;
        }
        return tableIds.stream().collect(Collectors.joining(","));
    }

    /**
     * Sets the table IDs from a comma-separated string.
     * This also updates the tableId field with the first entry for backward compatibility.
     *
     * @param tableIdsString A comma-separated string of table IDs
     */
    public void setTableIdsFromString(String tableIdsString) {
        if (tableIdsString == null || tableIdsString.isEmpty()) {
            this.tableIds = new ArrayList<>();
            this.tableId = null;
            this.combinedTables = false;
            return;
        }

        this.tableIds = Arrays.stream(tableIdsString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        this.tableId = this.tableIds.isEmpty() ? null : this.tableIds.get(0);
        this.combinedTables = this.tableIds.size() > 1;
    }

    /**
     * Checks if this response contains combined tables.
     *
     * @return true if multiple tables were found and combined, false otherwise
     */
    public boolean isCombinedTables() {
        return combinedTables;
    }

    /**
     * Sets whether this response contains combined tables.
     *
     * @param combinedTables true if multiple tables were found and combined, false otherwise
     */
    public void setCombinedTables(boolean combinedTables) {
        this.combinedTables = combinedTables;
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