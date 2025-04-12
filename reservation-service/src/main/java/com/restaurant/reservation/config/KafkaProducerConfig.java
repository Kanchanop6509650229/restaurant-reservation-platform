package com.restaurant.reservation.config;

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
 * Configuration class for Kafka Producer settings.
 * Defines the producer factory and template for sending events to Kafka topics.
 * Configures serialization and type mappings for various event types.
 *
 * This configuration supports sending the following event types:
 * - Reservation lifecycle events (created, confirmed, cancelled, modified)
 * - Table assignment and status events
 * - Restaurant validation requests
 * - Reservation time validation requests
 * - Restaurant search requests
 *
 * The configuration includes proper type mappings to ensure correct serialization
 * and deserialization of events across services.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Configuration
public class KafkaProducerConfig {

    /**
     * Kafka bootstrap servers address.
     * Injected from application properties.
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates and configures the Kafka Producer Factory.
     * Sets up:
     * - Bootstrap servers connection
     * - Key serializer (String)
     * - Value serializer (JSON)
     * - Type mappings for event classes
     *
     * @return Configured ProducerFactory for BaseEvent messages
     */
    @Bean
    public ProducerFactory<String, BaseEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        // Set Kafka broker connection
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Configure key serializer for String keys
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Configure value serializer for JSON messages
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Define type mappings for event classes to enable proper deserialization
        configProps.put(JsonSerializer.TYPE_MAPPINGS, String.join(",",
                "ReservationCreatedEvent:com.restaurant.common.events.reservation.ReservationCreatedEvent",
                "ReservationConfirmedEvent:com.restaurant.common.events.reservation.ReservationConfirmedEvent",
                "ReservationCancelledEvent:com.restaurant.common.events.reservation.ReservationCancelledEvent",
                "ReservationModifiedEvent:com.restaurant.common.events.reservation.ReservationModifiedEvent",
                "TableAssignedEvent:com.restaurant.common.events.reservation.TableAssignedEvent",
                "TableStatusChangedEvent:com.restaurant.common.events.restaurant.TableStatusChangedEvent",
                "TableStatusEvent:com.restaurant.common.events.reservation.TableStatusEvent",
                "FindAvailableTableRequestEvent:com.restaurant.common.events.reservation.FindAvailableTableRequestEvent",
                "RestaurantValidationRequestEvent:com.restaurant.common.events.restaurant.RestaurantValidationRequestEvent",
                "RestaurantValidationResponseEvent:com.restaurant.common.events.restaurant.RestaurantValidationResponseEvent",
                "ReservationTimeValidationRequestEvent:com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent",
                "RestaurantSearchRequestEvent:com.restaurant.common.events.restaurant.RestaurantSearchRequestEvent",
                "RestaurantSearchResponseEvent:com.restaurant.common.events.restaurant.RestaurantSearchResponseEvent"
        ));
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a KafkaTemplate for sending messages.
     * Uses the configured producer factory to create a template
     * that can be autowired into services for sending events.
     *
     * @return Configured KafkaTemplate for BaseEvent messages
     */
    @Bean
    public KafkaTemplate<String, BaseEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}