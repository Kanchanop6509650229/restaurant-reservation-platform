package com.restaurant.restaurant.kafka.consumers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.constants.StatusCodes;
import com.restaurant.common.events.reservation.FindAvailableTableRequestEvent;
import com.restaurant.common.events.reservation.FindAvailableTableResponseEvent;
import com.restaurant.restaurant.domain.models.Restaurant;
import com.restaurant.restaurant.domain.models.RestaurantTable;
import com.restaurant.restaurant.domain.repositories.RestaurantRepository;
import com.restaurant.restaurant.domain.repositories.RestaurantTableRepository;
import com.restaurant.restaurant.kafka.producers.RestaurantEventProducer;

/**
 * Kafka consumer for processing table availability requests.
 * This consumer handles:
 * - Table availability search requests
 * - Party size matching
 * - Restaurant validation
 * - Response event publishing
 *
 * Events are consumed from the find available table request topic
 * and processed to find suitable tables for reservations.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class TableAvailabilityRequestConsumer {

    /** Logger for table availability request processing */
    private static final Logger logger = LoggerFactory.getLogger(TableAvailabilityRequestConsumer.class);

    /** Repository for restaurant data access */
    private final RestaurantRepository restaurantRepository;

    /** Repository for table data access */
    private final RestaurantTableRepository tableRepository;

    /** Producer for publishing response events */
    private final RestaurantEventProducer eventProducer;

    /**
     * Constructs a new TableAvailabilityRequestConsumer with required dependencies.
     *
     * @param restaurantRepository Repository for restaurant data access
     * @param tableRepository Repository for table data access
     * @param eventProducer Producer for publishing response events
     */
    public TableAvailabilityRequestConsumer(
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository tableRepository,
            RestaurantEventProducer eventProducer) {
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.eventProducer = eventProducer;
    }

    /**
     * Consumes and processes table availability request events from Kafka.
     * This method:
     * - Validates restaurant existence and status
     * - Searches for suitable tables based on party size
     * - Sends success or error response events
     * - Handles error cases and logging
     *
     * @param event The find available table request event
     */
    @KafkaListener(
            topics = KafkaTopics.FIND_AVAILABLE_TABLE_REQUEST,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "tableAvailabilityKafkaListenerContainerFactory"
    )
    public void consumeFindAvailableTableRequest(FindAvailableTableRequestEvent event) {
        logger.info("Received find available table request: correlationId={}, reservationId={}, restaurantId={}",
                event.getCorrelationId(), event.getReservationId(), event.getRestaurantId());

        try {
            // Validate restaurant exists and is active
            Restaurant restaurant = restaurantRepository.findById(event.getRestaurantId()).orElse(null);
            if (restaurant == null || !restaurant.isActive()) {
                sendErrorResponse(event, "Restaurant not found or inactive");
                return;
            }

            // Find suitable table
            String tableId = findSuitableTable(event.getRestaurantId(), event.getPartySize());

            if (tableId != null) {
                sendSuccessResponse(event, tableId);
            } else {
                sendErrorResponse(event, "No suitable tables available for the requested party size");
            }
        } catch (Exception e) {
            logger.error("Error processing find available table request: {}", e.getMessage(), e);
            sendErrorResponse(event, "Internal server error: " + e.getMessage());
        }
    }

    /**
     * Finds a suitable table or combination of tables for the given party size.
     * This method:
     * - Retrieves all available tables for the restaurant
     * - Tries to find a single table that can accommodate the party
     * - If no single table is found, tries to find a combination of tables
     * - Prioritizes tables with the "combinable: true" property
     * - Returns either a single table ID or a comma-separated list of table IDs
     *
     * @param restaurantId The ID of the restaurant
     * @param partySize The size of the party needing a table
     * @return The ID of the suitable table or comma-separated list of table IDs, or null if none found
     */
    private String findSuitableTable(String restaurantId, int partySize) {
        List<RestaurantTable> availableTables = tableRepository.findByRestaurantIdAndStatus(
                restaurantId, StatusCodes.TABLE_AVAILABLE);

        if (availableTables.isEmpty()) {
            logger.warn("No available tables found for restaurant {}", restaurantId);
            return null;
        }

        // First, try to find a single table that can accommodate the party
        String singleTableId = availableTables.stream()
                .filter(table -> table.getCapacity() >= partySize)
                .sorted((t1, t2) -> Integer.compare(t1.getCapacity(), t2.getCapacity()))
                .map(RestaurantTable::getId)
                .findFirst()
                .orElse(null);

        if (singleTableId != null) {
            logger.info("Found single suitable table for party size {}", partySize);
            return singleTableId;
        }

        // If no single table is found, try to find a combination of tables
        logger.info("No single table found for party size {}, trying to find combination of tables", partySize);

        // First, try with combinable tables only
        List<String> combinedTableIds = findCombinableTables(availableTables, partySize, true);

        // If no combination of combinable tables is found, try with all tables
        if (combinedTableIds.isEmpty()) {
            logger.info("No combination of combinable tables found, trying with all tables");
            combinedTableIds = findCombinableTables(availableTables, partySize, false);
        }

        if (!combinedTableIds.isEmpty()) {
            String result = String.join(",", combinedTableIds);
            logger.info("Found combination of tables for party size {}: {}", partySize, result);
            return result;
        }

        logger.warn("No suitable table or combination found for party size {}", partySize);
        return null;
    }

    /**
     * Finds a combination of tables that can accommodate the given party size.
     * This method uses a greedy algorithm to find a combination of tables:
     * 1. Sorts tables by capacity (descending)
     * 2. Takes tables one by one until the total capacity is sufficient
     * 3. Prioritizes tables with the "combinable: true" property if requested
     *
     * @param availableTables List of available tables
     * @param partySize The size of the party needing tables
     * @param combinableOnly Whether to consider only tables with combinable=true
     * @return List of table IDs that can be combined, or empty list if no combination is found
     */
    private List<String> findCombinableTables(List<RestaurantTable> availableTables, int partySize, boolean combinableOnly) {
        // Filter tables based on combinable flag if needed
        List<RestaurantTable> eligibleTables = combinableOnly
                ? availableTables.stream().filter(RestaurantTable::isCombinable).collect(Collectors.toList())
                : new ArrayList<>(availableTables);

        if (eligibleTables.isEmpty()) {
            return Collections.emptyList();
        }

        // Sort tables by capacity (descending) to minimize the number of tables needed
        eligibleTables.sort((t1, t2) -> Integer.compare(t2.getCapacity(), t1.getCapacity()));

        List<RestaurantTable> selectedTables = new ArrayList<>();
        int totalCapacity = 0;

        // Select tables until we have enough capacity
        for (RestaurantTable table : eligibleTables) {
            selectedTables.add(table);
            totalCapacity += table.getCapacity();

            if (totalCapacity >= partySize) {
                break;
            }
        }

        // Check if the selected tables have enough capacity
        if (totalCapacity < partySize) {
            return Collections.emptyList();
        }

        // Return the IDs of the selected tables
        return selectedTables.stream()
                .map(RestaurantTable::getId)
                .collect(Collectors.toList());
    }

    /**
     * Sends a success response event for a table availability request.
     * This method creates and publishes a response event indicating
     * that a suitable table was found.
     *
     * @param request The original request event
     * @param tableId The ID of the found table or comma-separated list of table IDs
     */
    private void sendSuccessResponse(FindAvailableTableRequestEvent request, String tableId) {
        FindAvailableTableResponseEvent response;

        // Check if this is a combined table result (contains commas)
        if (tableId != null && tableId.contains(",")) {
            // Parse the comma-separated list into a List<String>
            List<String> tableIds = new ArrayList<>();
            for (String id : tableId.split(",")) {
                tableIds.add(id.trim());
            }

            // Create a response with combined tables
            response = new FindAvailableTableResponseEvent();
            response.setReservationId(request.getReservationId());
            response.setRestaurantId(request.getRestaurantId());
            response.setTableIds(tableIds);
            response.setSuccess(true);
            response.setErrorMessage(null);
            response.setCorrelationId(request.getCorrelationId());
            response.setCombinedTables(true);

            logger.info("Sent success response with combined tables: correlationId={}, tableIds={}",
                    request.getCorrelationId(), tableId);
        } else {
            // Single table ID
            response = new FindAvailableTableResponseEvent(
                    request.getReservationId(),
                    request.getRestaurantId(),
                    tableId,
                    true,
                    null,
                    request.getCorrelationId()
            );

            logger.info("Sent success response with single table: correlationId={}, tableId={}",
                    request.getCorrelationId(), tableId);
        }

        eventProducer.publishFindAvailableTableResponse(response);
    }

    /**
     * Sends an error response event for a table availability request.
     * This method creates and publishes a response event indicating
     * that the request could not be fulfilled.
     *
     * @param request The original request event
     * @param errorMessage The error message explaining the failure
     */
    private void sendErrorResponse(FindAvailableTableRequestEvent request, String errorMessage) {
        // For error responses, we use the single table ID constructor with null tableId
        FindAvailableTableResponseEvent response = new FindAvailableTableResponseEvent(
                request.getReservationId(),
                request.getRestaurantId(),
                (String) null,  // Explicitly cast to String to resolve ambiguity
                false,
                errorMessage,
                request.getCorrelationId()
        );

        eventProducer.publishFindAvailableTableResponse(response);
        logger.warn("Sent error response: correlationId={}, error={}",
                request.getCorrelationId(), errorMessage);
    }
}