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

@Component
public class TableStatusEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TableStatusEventConsumer.class);
    private final TableService tableService;
    private final ObjectMapper objectMapper;

    public TableStatusEventConsumer(TableService tableService) {
        this.tableService = tableService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // For handling date/time fields if present
    }

    @KafkaListener(topics = KafkaTopics.TABLE_STATUS, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "tableStatusKafkaListenerContainerFactory")
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