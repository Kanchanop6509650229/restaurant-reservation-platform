package com.restaurant.reservation.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.restaurant.RestaurantSearchResponseEvent;
import com.restaurant.reservation.service.RestaurantSearchService;

/**
 * Kafka consumer for restaurant search responses in the reservation service.
 * This class handles responses to restaurant search requests, processing
 * the results and making them available to the service layer.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class RestaurantSearchResponseConsumer {

    /** Logger for this consumer */
    private static final Logger logger = LoggerFactory.getLogger(RestaurantSearchResponseConsumer.class);

    /** Service for managing restaurant search operations */
    private final RestaurantSearchService restaurantSearchService;

    /**
     * Constructs a new RestaurantSearchResponseConsumer with required dependencies.
     *
     * @param restaurantSearchService Service for managing restaurant search operations
     */
    public RestaurantSearchResponseConsumer(RestaurantSearchService restaurantSearchService) {
        this.restaurantSearchService = restaurantSearchService;
    }

    /**
     * Consumes restaurant search response events from the Kafka topic.
     * This method processes responses to restaurant search requests and
     * completes the corresponding CompletableFuture in the service layer.
     *
     * @param event The restaurant search response event to process
     */
    @KafkaListener(
            topics = KafkaTopics.RESTAURANT_SEARCH_RESPONSE,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "restaurantSearchKafkaListenerContainerFactory"
    )
    public void consumeRestaurantSearchResponse(RestaurantSearchResponseEvent event) {
        if (event == null) {
            logger.warn("Received null restaurant search response event");
            return;
        }

        logger.info("Received restaurant search response: correlationId={}, success={}, resultCount={}",
                event.getCorrelationId(),
                event.isSuccess(),
                event.getRestaurants() != null ? event.getRestaurants().size() : 0);

        try {
            restaurantSearchService.processSearchResponse(event);
        } catch (Exception e) {
            logger.error("Error processing restaurant search response: {}", e.getMessage(), e);
        }
    }
}
