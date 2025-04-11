package com.restaurant.restaurant.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.restaurant.common.events.BaseEvent;

/**
 * Configuration class for Kafka producers in the restaurant service.
 * This class configures the Kafka producer factory and template for sending events
 * to various topics. It supports multiple event types including:
 * - Restaurant updates
 * - Capacity changes
 * - Table status changes
 * - Operating hours changes
 * - Table availability responses
 * - Restaurant validation responses
 * - Reservation time validation responses and requests
 * 
 * The configuration includes:
 * - Bootstrap servers
 * - Serializers for keys and values
 * - Type mappings for event serialization
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Configuration
public class KafkaProducerConfig {

    /** Kafka bootstrap servers configuration */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates a producer factory for Kafka events.
     * This factory is configured to:
     * - Use the specified bootstrap servers
     * - Use String serializer for keys
     * - Use JSON serializer for values
     * - Map event types to their corresponding classes
     * 
     * The type mappings include:
     * - RestaurantUpdatedEvent
     * - CapacityChangedEvent
     * - TableStatusChangedEvent
     * - OperatingHoursChangedEvent
     * - FindAvailableTableResponseEvent
     * - RestaurantValidationResponseEvent
     * - ReservationTimeValidationResponseEvent
     * - ReservationTimeValidationRequestEvent
     *
     * @return ProducerFactory configured for BaseEvent messages
     */
    @Bean
    public ProducerFactory<String, BaseEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.TYPE_MAPPINGS,
                "RestaurantUpdatedEvent:com.restaurant.common.events.restaurant.RestaurantUpdatedEvent," +
                        "CapacityChangedEvent:com.restaurant.common.events.restaurant.CapacityChangedEvent," +
                        "TableStatusChangedEvent:com.restaurant.common.events.restaurant.TableStatusChangedEvent," +
                        "OperatingHoursChangedEvent:com.restaurant.common.events.restaurant.OperatingHoursChangedEvent,"
                        +
                        "FindAvailableTableResponseEvent:com.restaurant.common.events.reservation.FindAvailableTableResponseEvent,"
                        +
                        "RestaurantValidationResponseEvent:com.restaurant.common.events.restaurant.RestaurantValidationResponseEvent,"
                        +
                        "ReservationTimeValidationResponseEvent:com.restaurant.common.events.restaurant.ReservationTimeValidationResponseEvent,"
                        +
                        "ReservationTimeValidationRequestEvent:com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a Kafka template for sending events.
     * This template uses the configured producer factory and provides
     * a convenient way to send events to Kafka topics.
     *
     * @return KafkaTemplate configured for BaseEvent messages
     */
    @Bean
    public KafkaTemplate<String, BaseEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}