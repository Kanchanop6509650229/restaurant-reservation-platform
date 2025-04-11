package com.restaurant.reservation.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.reservation.FindAvailableTableResponseEvent;
import com.restaurant.reservation.service.TableResponseManager;

/**
 * Kafka consumer for table availability responses in the reservation service.
 * This class handles responses to table availability requests, processing the
 * results and completing the corresponding asynchronous requests through the
 * TableResponseManager.
 *
 * The consumer ensures that table availability checks are properly completed
 * and their results are made available to the requesting components.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class TableAvailabilityResponseConsumer {

    /** Logger instance for tracking table availability responses */
    private static final Logger logger = LoggerFactory.getLogger(TableAvailabilityResponseConsumer.class);

    /** Manager for handling table availability responses */
    private final TableResponseManager tableResponseManager;

    /**
     * Constructs a new TableAvailabilityResponseConsumer with the specified response manager.
     *
     * @param tableResponseManager The manager for handling table availability responses
     */
    public TableAvailabilityResponseConsumer(TableResponseManager tableResponseManager) {
        this.tableResponseManager = tableResponseManager;
    }

    /**
     * Consumes table availability response events from the Kafka topic.
     * This method processes responses to table availability requests,
     * logging the response details and completing the corresponding
     * CompletableFuture in the response manager.
     *
     * @param event The table availability response event containing availability details
     */
    @KafkaListener(
            topics = KafkaTopics.FIND_AVAILABLE_TABLE_RESPONSE,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "tableAvailabilityKafkaListenerContainerFactory"
    )
    public void consumeTableAvailabilityResponse(FindAvailableTableResponseEvent event) {
        if (event == null) {
            logger.warn("Received null table availability response event");
            return;
        }

        logger.info("Received find available table response: correlationId={}, tableId={}, success={}, errorMessage={}",
                event.getCorrelationId(),
                event.getTableId(),
                event.isSuccess(),
                event.getErrorMessage() != null ? event.getErrorMessage() : "none");

        try {
            // Send the response to the TableResponseManager to complete the waiting CompletableFuture
            tableResponseManager.completeResponse(event);
            logger.debug("Processed table availability response for correlationId={}",
                    event.getCorrelationId());
        } catch (Exception e) {
            logger.error("Error processing table availability response: correlationId={}, error={}",
                    event.getCorrelationId(), e.getMessage(), e);
        }
    }
}