package com.restaurant.reservation.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.restaurant.RestaurantOwnershipResponseEvent;
import com.restaurant.reservation.service.RestaurantResponseManager;

/**
 * Kafka consumer for restaurant ownership validation responses.
 * This consumer listens for ownership validation responses from the restaurant service
 * and completes the corresponding pending requests.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class RestaurantOwnershipConsumer {
    
    /** Logger for this consumer */
    private static final Logger logger = LoggerFactory.getLogger(RestaurantOwnershipConsumer.class);
    
    /** Manager for handling validation responses */
    private final RestaurantResponseManager responseManager;
    
    /**
     * Constructs a new RestaurantOwnershipConsumer with required dependencies.
     *
     * @param responseManager Manager for handling validation responses
     */
    public RestaurantOwnershipConsumer(RestaurantResponseManager responseManager) {
        this.responseManager = responseManager;
    }
    
    /**
     * Listens for restaurant ownership validation responses.
     * When a response is received, it completes the corresponding pending request.
     *
     * @param response The ownership validation response event
     */
    @KafkaListener(topics = KafkaTopics.RESTAURANT_OWNERSHIP_RESPONSE, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOwnershipResponse(RestaurantOwnershipResponseEvent response) {
        if (response == null || response.getCorrelationId() == null) {
            logger.error("Received null ownership response or response with null correlation ID");
            return;
        }
        
        logger.info("Received restaurant ownership validation response: correlationId={}, restaurantId={}, userId={}, isOwner={}",
                response.getCorrelationId(), response.getRestaurantId(), response.getUserId(), response.isOwner());
        
        responseManager.completeOwnershipResponse(response);
    }
}
