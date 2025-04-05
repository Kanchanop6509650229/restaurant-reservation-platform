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

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, BaseEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.TYPE_MAPPINGS,
                "ReservationCreatedEvent:com.restaurant.common.events.reservation.ReservationCreatedEvent," +
                        "ReservationConfirmedEvent:com.restaurant.common.events.reservation.ReservationConfirmedEvent,"
                        +
                        "ReservationCancelledEvent:com.restaurant.common.events.reservation.ReservationCancelledEvent,"
                        +
                        "ReservationModifiedEvent:com.restaurant.common.events.reservation.ReservationModifiedEvent," +
                        "TableAssignedEvent:com.restaurant.common.events.reservation.TableAssignedEvent," +
                        "TableStatusChangedEvent:com.restaurant.common.events.restaurant.TableStatusChangedEvent," +
                        "TableStatusEvent:com.restaurant.common.events.reservation.TableStatusEvent," +
                        "FindAvailableTableRequestEvent:com.restaurant.common.events.reservation.FindAvailableTableRequestEvent,"
                        +
                        "RestaurantValidationRequestEvent:com.restaurant.common.events.restaurant.RestaurantValidationRequestEvent,"
                        +
                        "RestaurantValidationResponseEvent:com.restaurant.common.events.restaurant.RestaurantValidationResponseEvent,"
                        +
                        "ReservationTimeValidationRequestEvent:com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, BaseEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}