package com.restaurant.restaurant.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.reservation.TableStatusEvent;
import com.restaurant.common.events.restaurant.TableStatusChangedEvent;
import com.restaurant.restaurant.service.TableService;

@Component
public class TableStatusEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TableStatusEventConsumer.class);
    private final TableService tableService;

    public TableStatusEventConsumer(TableService tableService) {
        this.tableService = tableService;
    }

    @KafkaListener(
            topics = KafkaTopics.TABLE_STATUS,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "tableStatusKafkaListenerContainerFactory"
    )
    public void consumeTableStatusChangedEvent(Object event) {
        logger.info("Received table status event: {}", event.getClass().getSimpleName());
        
        try {
            if (event instanceof TableStatusChangedEvent) {
                handleTableStatusChangedEvent((TableStatusChangedEvent) event);
            } else if (event instanceof TableStatusEvent) {
                handleTableStatusEvent((TableStatusEvent) event);
            }
        } catch (Exception e) {
            logger.error("Error processing table status event: {}", e.getMessage(), e);
        }
    }
    
    private void handleTableStatusChangedEvent(TableStatusChangedEvent event) {
        logger.info("Processing table status change: Table {} status changed from {} to {}", 
                event.getTableId(), event.getOldStatus(), event.getNewStatus());
        
        // Update the table status in the database without publishing a duplicate event
        tableService.updateTableStatusWithoutEvent(event.getTableId(), event.getNewStatus(), event.getReservationId());
    }
    
    private void handleTableStatusEvent(TableStatusEvent event) {
        logger.info("Processing table status event: Table {} status changed from {} to {}", 
                event.getTableId(), event.getOldStatus(), event.getNewStatus());
        
        // Update the table status in the database without publishing a duplicate event
        tableService.updateTableStatusWithoutEvent(event.getTableId(), event.getNewStatus(), event.getReservationId());
    }
}