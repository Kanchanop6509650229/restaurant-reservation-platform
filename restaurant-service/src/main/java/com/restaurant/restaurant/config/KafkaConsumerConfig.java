package com.restaurant.restaurant.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.restaurant.common.events.reservation.FindAvailableTableRequestEvent;
import com.restaurant.common.events.restaurant.RestaurantValidationRequestEvent;
import com.restaurant.common.events.user.UserEvent;

/**
 * Configuration class for Kafka consumers in the restaurant service.
 * This class configures multiple Kafka consumer factories and their corresponding
 * listener container factories for different types of events:
 * - User events
 * - Table status updates
 * - Table availability requests
 * - Restaurant validation requests
 * - Reservation time validation requests
 * 
 * Each consumer configuration includes:
 * - Bootstrap servers
 * - Group ID
 * - Auto offset reset policy
 * - Deserializers for keys and values
 * - Error handling
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Configuration
public class KafkaConsumerConfig {

    /** Kafka bootstrap servers configuration */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /** Kafka consumer group ID */
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Creates a consumer factory for UserEvent messages.
     * This factory is configured to:
     * - Use the specified bootstrap servers
     * - Use the configured group ID
     * - Start from the earliest offset
     * - Use String deserializer for keys
     * - Use JSON deserializer for values
     *
     * @return ConsumerFactory configured for UserEvent messages
     */
    @Bean
    public ConsumerFactory<String, UserEvent> userEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<UserEvent> deserializer = new JsonDeserializer<>(UserEvent.class);
        deserializer.addTrustedPackages("com.restaurant.common.events");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a Kafka listener container factory for UserEvent messages.
     * This factory uses the userEventConsumerFactory for message consumption.
     *
     * @return ConcurrentKafkaListenerContainerFactory for UserEvent messages
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserEvent> userKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userEventConsumerFactory());
        return factory;
    }

    /**
     * Creates a consumer factory for table status messages.
     * This factory is configured to:
     * - Use the specified bootstrap servers
     * - Use a group ID specific to table status
     * - Start from the earliest offset
     * - Use String deserializer for both keys and values
     *
     * @return ConsumerFactory configured for table status messages
     */
    @Bean
    public ConsumerFactory<String, String> tableStatusConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-table-status");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Creates a Kafka listener container factory for table status messages.
     * This factory uses the tableStatusConsumerFactory for message consumption.
     *
     * @return ConcurrentKafkaListenerContainerFactory for table status messages
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> tableStatusKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tableStatusConsumerFactory());
        return factory;
    }

    /**
     * Creates a consumer factory for table availability request messages.
     * This factory is configured to:
     * - Use the specified bootstrap servers
     * - Use a group ID specific to table availability
     * - Start from the earliest offset
     * - Use error handling deserializers
     * - Use JSON deserializer for values
     *
     * @return ConsumerFactory configured for table availability request messages
     */
    @Bean
    public ConsumerFactory<String, FindAvailableTableRequestEvent> tableAvailabilityConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-table-availability");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        JsonDeserializer<FindAvailableTableRequestEvent> deserializer = new JsonDeserializer<>(
                FindAvailableTableRequestEvent.class);
        deserializer.addTrustedPackages("com.restaurant.common.events");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(props,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(deserializer));
    }

    /**
     * Creates a Kafka listener container factory for table availability request messages.
     * This factory uses the tableAvailabilityConsumerFactory for message consumption.
     *
     * @return ConcurrentKafkaListenerContainerFactory for table availability request messages
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FindAvailableTableRequestEvent> tableAvailabilityKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FindAvailableTableRequestEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tableAvailabilityConsumerFactory());
        return factory;
    }

    /**
     * Creates a consumer factory for restaurant validation request messages.
     * This factory is configured to:
     * - Use the specified bootstrap servers
     * - Use a group ID specific to restaurant validation
     * - Start from the earliest offset
     * - Use error handling deserializers
     * - Use JSON deserializer for values
     *
     * @return ConsumerFactory configured for restaurant validation request messages
     */
    @Bean
    public ConsumerFactory<String, RestaurantValidationRequestEvent> restaurantValidationConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-restaurant-validation");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        JsonDeserializer<RestaurantValidationRequestEvent> deserializer = new JsonDeserializer<>(
                RestaurantValidationRequestEvent.class);
        deserializer.addTrustedPackages("com.restaurant.common.events");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(props,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(deserializer));
    }

    /**
     * Creates a Kafka listener container factory for restaurant validation request messages.
     * This factory uses the restaurantValidationConsumerFactory for message consumption.
     *
     * @return ConcurrentKafkaListenerContainerFactory for restaurant validation request messages
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RestaurantValidationRequestEvent> restaurantValidationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RestaurantValidationRequestEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(restaurantValidationConsumerFactory());
        return factory;
    }
    
    /**
     * Creates a consumer factory for reservation time validation request messages.
     * This factory is configured to:
     * - Use the specified bootstrap servers
     * - Use a group ID specific to time validation
     * - Start from the earliest offset
     * - Use error handling deserializers
     * - Use JSON deserializer for values
     *
     * @return ConsumerFactory configured for reservation time validation request messages
     */
    @Bean
    public ConsumerFactory<String, com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent> reservationTimeValidationConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-time-validation");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        JsonDeserializer<com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent> deserializer = 
                new JsonDeserializer<>(com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent.class);
        deserializer.addTrustedPackages("com.restaurant.common.events");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(props,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(deserializer));
    }

    /**
     * Creates a Kafka listener container factory for reservation time validation request messages.
     * This factory uses the reservationTimeValidationConsumerFactory for message consumption.
     *
     * @return ConcurrentKafkaListenerContainerFactory for reservation time validation request messages
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent> restaurantKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(reservationTimeValidationConsumerFactory());
        return factory;
    }
}