package com.restaurant.reservation.config;

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

import com.restaurant.common.events.kitchen.MenuItemEvent;
import com.restaurant.common.events.reservation.FindAvailableTableResponseEvent;
import com.restaurant.common.events.restaurant.ReservationTimeValidationResponseEvent;
import com.restaurant.common.events.restaurant.RestaurantSearchResponseEvent;
import com.restaurant.common.events.restaurant.RestaurantValidationResponseEvent;
import com.restaurant.common.events.user.UserEvent;

/**
 * Configuration class for Kafka Consumer settings.
 * Defines multiple consumer factories and listener container factories
 * for different types of events in the system.
 * Handles deserialization and error handling for Kafka messages.
 *
 * This configuration includes:
 * - General restaurant event consumers
 * - User event consumers
 * - Table availability event consumers
 * - Restaurant validation event consumers
 * - Reservation time validation event consumers
 *
 * Each consumer type has its own factory and container factory with specific
 * settings for handling different event types.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Configuration
public class KafkaConsumerConfig {

    /**
     * Kafka bootstrap servers address.
     * Injected from application properties.
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Kafka consumer group ID.
     * Injected from application properties.
     */
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Creates a consumer factory for general restaurant events.
     * Configures:
     * - Bootstrap servers
     * - Consumer group ID
     * - Auto offset reset policy
     * - Error handling deserializers
     * - Trusted packages for deserialization
     *
     * @return Configured ConsumerFactory for general restaurant events
     */
    @Bean
    public ConsumerFactory<String, Object> restaurantEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        JsonDeserializer<Object> deserializer = new JsonDeserializer<>(Object.class);
        deserializer.addTrustedPackages("com.restaurant.common.events");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(props,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(deserializer));
    }

    /**
     * Creates a container factory for restaurant event listeners.
     * Uses the restaurant event consumer factory for message consumption.
     *
     * @return Configured ConcurrentKafkaListenerContainerFactory for restaurant events
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> restaurantKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(restaurantEventConsumerFactory());
        return factory;
    }

    /**
     * Creates a consumer factory for user events.
     * Configures specific settings for handling user-related events.
     *
     * @return Configured ConsumerFactory for UserEvent messages
     */
    @Bean
    public ConsumerFactory<String, UserEvent> userEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-user");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<UserEvent> deserializer = new JsonDeserializer<>(UserEvent.class);
        deserializer.addTrustedPackages("com.restaurant.common.events");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a container factory for user event listeners.
     * Uses the user event consumer factory for message consumption.
     *
     * @return Configured ConcurrentKafkaListenerContainerFactory for user events
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserEvent> userKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userEventConsumerFactory());
        return factory;
    }

    /**
     * Creates a consumer factory for table availability events.
     * Configures specific settings for handling table availability responses.
     *
     * @return Configured ConsumerFactory for FindAvailableTableResponseEvent messages
     */
    @Bean
    public ConsumerFactory<String, FindAvailableTableResponseEvent> tableAvailabilityConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-table-availability");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<FindAvailableTableResponseEvent> deserializer = new JsonDeserializer<>(
                FindAvailableTableResponseEvent.class);
        deserializer.addTrustedPackages("com.restaurant.common.events");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a container factory for table availability event listeners.
     * Uses the table availability consumer factory for message consumption.
     *
     * @return Configured ConcurrentKafkaListenerContainerFactory for table availability events
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FindAvailableTableResponseEvent> tableAvailabilityKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FindAvailableTableResponseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tableAvailabilityConsumerFactory());
        return factory;
    }

    /**
     * Creates a consumer factory for restaurant validation events.
     * Configures specific settings for handling restaurant validation responses.
     *
     * @return Configured ConsumerFactory for RestaurantValidationResponseEvent messages
     */
    @Bean
    public ConsumerFactory<String, RestaurantValidationResponseEvent> restaurantValidationConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-restaurant-validation");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<RestaurantValidationResponseEvent> deserializer = new JsonDeserializer<>(
                RestaurantValidationResponseEvent.class);
        deserializer.addTrustedPackages("com.restaurant.common.events");
        deserializer.setUseTypeMapperForKey(true);
        deserializer.setRemoveTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a container factory for restaurant validation event listeners.
     * Uses the restaurant validation consumer factory for message consumption.
     *
     * @return Configured ConcurrentKafkaListenerContainerFactory for restaurant validation events
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RestaurantValidationResponseEvent> restaurantValidationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RestaurantValidationResponseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(restaurantValidationConsumerFactory());
        return factory;
    }

    /**
     * Creates a consumer factory for reservation time validation events.
     * Configures specific settings for handling time validation responses.
     *
     * @return Configured ConsumerFactory for ReservationTimeValidationResponseEvent messages
     */
    @Bean
    public ConsumerFactory<String, ReservationTimeValidationResponseEvent> reservationTimeValidationConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-time-validation");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<ReservationTimeValidationResponseEvent> deserializer = new JsonDeserializer<>(
                ReservationTimeValidationResponseEvent.class);
        deserializer.addTrustedPackages("com.restaurant.common.events");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a container factory for reservation time validation event listeners.
     * Uses the time validation consumer factory for message consumption.
     *
     * @return Configured ConcurrentKafkaListenerContainerFactory for time validation events
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReservationTimeValidationResponseEvent> reservationTimeValidationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReservationTimeValidationResponseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(reservationTimeValidationConsumerFactory());
        return factory;
    }

    /**
     * Creates a consumer factory for restaurant search events.
     * Configures specific settings for handling restaurant search responses.
     *
     * @return Configured ConsumerFactory for RestaurantSearchResponseEvent messages
     */
    @Bean
    public ConsumerFactory<String, RestaurantSearchResponseEvent> restaurantSearchConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-restaurant-search");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<RestaurantSearchResponseEvent> deserializer = new JsonDeserializer<>(
                RestaurantSearchResponseEvent.class);
        deserializer.addTrustedPackages("com.restaurant.common.events");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a container factory for restaurant search event listeners.
     * Uses the restaurant search consumer factory for message consumption.
     *
     * @return Configured ConcurrentKafkaListenerContainerFactory for restaurant search events
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RestaurantSearchResponseEvent> restaurantSearchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RestaurantSearchResponseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(restaurantSearchConsumerFactory());
        return factory;
    }

    /**
     * Creates a consumer factory for menu item events.
     * Configures specific settings for handling menu item events from the kitchen service.
     *
     * @return Configured ConsumerFactory for MenuItemEvent messages
     */
    @Bean
    public ConsumerFactory<String, MenuItemEvent> menuItemConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-menu-item");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<MenuItemEvent> deserializer = new JsonDeserializer<>(MenuItemEvent.class);
        deserializer.addTrustedPackages("com.restaurant.common.events");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a container factory for menu item event listeners.
     * Uses the menu item consumer factory for message consumption.
     *
     * @return Configured ConcurrentKafkaListenerContainerFactory for menu item events
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MenuItemEvent> menuItemKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MenuItemEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(menuItemConsumerFactory());
        return factory;
    }
}