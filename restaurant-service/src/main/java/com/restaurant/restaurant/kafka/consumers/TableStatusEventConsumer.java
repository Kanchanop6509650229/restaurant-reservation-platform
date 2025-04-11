package com.restaurant.restaurant.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.restaurant.service.TableService;

/**
 * Kafka consumer for processing table status change events.
 * This consumer handles:
 * - Table availability updates
 * - Reservation status synchronization
 * - Status change validation and processing
 * 
 * Events are consumed from the table status topic and processed
 * to maintain consistent table states across the system.
 * Uses JSON parsing for flexible event handling and backwards compatibility.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class TableStatusEventConsumer {

    /** Logger for table status event processing */
    private static final Logger logger = LoggerFactory.getLogger(TableStatusEventConsumer.class);
    
    /** Service for managing table operations */
    private final TableService tableService;
    
    /** Mapper for JSON processing with date/time support */
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new TableStatusEventConsumer with required dependencies.
     * Initializes JSON object mapper with time module support.
     *
     * @param tableService Service for managing table operations
     */
    public TableStatusEventConsumer(TableService tableService) {
        this.tableService = tableService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // For handling date/time fields if present
    }

    /**
     * Consumes and processes table status change events from Kafka.
     * This method:
     * - Parses the JSON event message
     * - Extracts relevant table status information
     * - Updates table status through the table service
     * - Handles error cases and logging
     *
     * @param messageJson The JSON string containing the table status event
     */
    @KafkaListener(topics = KafkaTopics.TABLE_STATUS, groupId = "${spring.kafka.consumer.group-id}", 
                  containerFactory = "tableStatusKafkaListenerContainerFactory")
    public void consumeTableStatusChangedEvent(String messageJson) {
        try {
            logger.info("Received table status event JSON: {}", messageJson);

            // Extract basic fields from JSON without full deserialization
            JsonNode rootNode = objectMapper.readTree(messageJson);

            String eventType = rootNode.has("@type") ? rootNode.get("@type").asText() : "unknown";
            String tableId = rootNode.has("tableId") ? rootNode.get("tableId").asText() : null;
            String newStatus = rootNode.has("newStatus") ? rootNode.get("newStatus").asText() : null;
            String reservationId = rootNode.has("reservationId") ? rootNode.get("reservationId").asText() : null;

            if (tableId != null && newStatus != null) {
                logger.info("Processing table status change: Table {} status changed to {}, reservationId: {}",
                        tableId, newStatus, reservationId);

                tableService.updateTableStatusWithoutEvent(tableId, newStatus, reservationId);
            } else {
                logger.warn("Incomplete table status event data: {}", messageJson);
            }

        } catch (Exception e) {
            logger.error("Error processing table status event: {}", e.getMessage(), e);
        }
    }
}