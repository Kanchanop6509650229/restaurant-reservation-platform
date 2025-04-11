package com.restaurant.restaurant.kafka.consumers;

import java.util.List;

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
     * Finds a suitable table for the given party size.
     * This method:
     * - Retrieves all available tables for the restaurant
     * - Filters tables by required capacity
     * - Sorts tables by capacity to find optimal match
     * - Returns the ID of the most suitable table
     *
     * @param restaurantId The ID of the restaurant
     * @param partySize The size of the party needing a table
     * @return The ID of the suitable table, or null if none found
     */
    private String findSuitableTable(String restaurantId, int partySize) {
        List<RestaurantTable> availableTables = tableRepository.findByRestaurantIdAndStatus(
                restaurantId, StatusCodes.TABLE_AVAILABLE);
        
        return availableTables.stream()
                .filter(table -> table.getCapacity() >= partySize)
                .sorted((t1, t2) -> Integer.compare(t1.getCapacity(), t2.getCapacity()))
                .map(RestaurantTable::getId)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Sends a success response event for a table availability request.
     * This method creates and publishes a response event indicating
     * that a suitable table was found.
     *
     * @param request The original request event
     * @param tableId The ID of the found table
     */
    private void sendSuccessResponse(FindAvailableTableRequestEvent request, String tableId) {
        FindAvailableTableResponseEvent response = new FindAvailableTableResponseEvent(
                request.getReservationId(),
                request.getRestaurantId(),
                tableId,
                true,
                null,
                request.getCorrelationId()
        );
        
        eventProducer.publishFindAvailableTableResponse(response);
        logger.info("Sent success response: correlationId={}, tableId={}", 
                request.getCorrelationId(), tableId);
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
        FindAvailableTableResponseEvent response = new FindAvailableTableResponseEvent(
                request.getReservationId(),
                request.getRestaurantId(),
                null,
                false,
                errorMessage,
                request.getCorrelationId()
        );
        
        eventProducer.publishFindAvailableTableResponse(response);
        logger.warn("Sent error response: correlationId={}, error={}", 
                request.getCorrelationId(), errorMessage);
    }
}