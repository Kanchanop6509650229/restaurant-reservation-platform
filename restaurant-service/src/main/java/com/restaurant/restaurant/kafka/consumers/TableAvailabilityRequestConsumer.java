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

@Component
public class TableAvailabilityRequestConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TableAvailabilityRequestConsumer.class);
    
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final RestaurantEventProducer eventProducer;
    
    public TableAvailabilityRequestConsumer(
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository tableRepository,
            RestaurantEventProducer eventProducer) {
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.eventProducer = eventProducer;
    }
    
    @KafkaListener(
            topics = KafkaTopics.FIND_AVAILABLE_TABLE_REQUEST,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "tableAvailabilityKafkaListenerContainerFactory"
    )
    public void consumeFindAvailableTableRequest(FindAvailableTableRequestEvent event) {
        logger.info("Received find available table request: correlationId={}, reservationId={}, restaurantId={}", 
                event.getCorrelationId(), event.getReservationId(), event.getRestaurantId());
        
        try {
            // ตรวจสอบว่าร้านอาหารมีอยู่จริง
            Restaurant restaurant = restaurantRepository.findById(event.getRestaurantId()).orElse(null);
            if (restaurant == null || !restaurant.isActive()) {
                sendErrorResponse(event, "Restaurant not found or inactive");
                return;
            }
            
            // ค้นหาโต๊ะที่เหมาะสม
            String tableId = findSuitableTable(event.getRestaurantId(), event.getPartySize());
            
            if (tableId != null) {
                // ส่ง response กลับด้วย tableId ที่พบ
                sendSuccessResponse(event, tableId);
            } else {
                // ไม่พบโต๊ะที่เหมาะสม
                sendErrorResponse(event, "No suitable tables available for the requested party size");
            }
        } catch (Exception e) {
            logger.error("Error processing find available table request: {}", e.getMessage(), e);
            sendErrorResponse(event, "Internal server error: " + e.getMessage());
        }
    }
    
    private String findSuitableTable(String restaurantId, int partySize) {
        // หาโต๊ะว่างที่เหมาะสมกับจำนวนคน
        List<RestaurantTable> availableTables = tableRepository.findByRestaurantIdAndStatus(
                restaurantId, StatusCodes.TABLE_AVAILABLE);
        
        // เรียงลำดับตามความจุเพื่อหาโต๊ะที่เล็กที่สุดที่รองรับได้
        return availableTables.stream()
                .filter(table -> table.getCapacity() >= partySize) // กรองเฉพาะโต๊ะที่รองรับคนได้พอ
                .sorted((t1, t2) -> Integer.compare(t1.getCapacity(), t2.getCapacity())) // เรียงจากเล็กไปใหญ่
                .map(RestaurantTable::getId) // เอาเฉพาะ ID
                .findFirst() // เอาโต๊ะแรกที่พบ
                .orElse(null);
    }
    
    private void sendSuccessResponse(FindAvailableTableRequestEvent request, String tableId) {
        FindAvailableTableResponseEvent response = new FindAvailableTableResponseEvent(
                request.getReservationId(),
                request.getRestaurantId(),
                tableId,
                true,  // success = true
                null,  // ไม่มีข้อความ error
                request.getCorrelationId()
        );
        
        eventProducer.publishFindAvailableTableResponse(response);
        logger.info("Sent success response: correlationId={}, tableId={}", 
                request.getCorrelationId(), tableId);
    }
    
    private void sendErrorResponse(FindAvailableTableRequestEvent request, String errorMessage) {
        FindAvailableTableResponseEvent response = new FindAvailableTableResponseEvent(
                request.getReservationId(),
                request.getRestaurantId(),
                null,  // ไม่มี tableId
                false,  // success = false
                errorMessage,
                request.getCorrelationId()
        );
        
        eventProducer.publishFindAvailableTableResponse(response);
        logger.warn("Sent error response: correlationId={}, error={}", 
                request.getCorrelationId(), errorMessage);
    }
}