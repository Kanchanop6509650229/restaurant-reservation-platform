package com.restaurant.reservation.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.reservation.FindAvailableTableResponseEvent;
import com.restaurant.reservation.service.TableResponseManager;

@Component
public class TableAvailabilityResponseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TableAvailabilityResponseConsumer.class);
    
    private final TableResponseManager tableResponseManager;
    
    public TableAvailabilityResponseConsumer(TableResponseManager tableResponseManager) {
        this.tableResponseManager = tableResponseManager;
    }
    
    @KafkaListener(
            topics = KafkaTopics.FIND_AVAILABLE_TABLE_RESPONSE,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "tableAvailabilityKafkaListenerContainerFactory"
    )
    public void consumeTableAvailabilityResponse(FindAvailableTableResponseEvent event) {
        logger.info("Received find available table response: correlationId={}, tableId={}, success={}", 
                event.getCorrelationId(), event.getTableId(), event.isSuccess());
        
        try {
            // ส่งข้อมูลไปยัง TableResponseManager เพื่อให้ CompletableFuture ที่รออยู่ได้รับข้อมูล
            tableResponseManager.completeResponse(event);
        } catch (Exception e) {
            logger.error("Error processing table availability response: {}", e.getMessage(), e);
        }
    }
}