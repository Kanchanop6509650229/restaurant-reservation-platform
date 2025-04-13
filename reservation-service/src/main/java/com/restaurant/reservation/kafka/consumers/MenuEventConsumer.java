package com.restaurant.reservation.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.kitchen.MenuCategoryUpdatedEvent;
import com.restaurant.common.events.kitchen.MenuItemEvent;
import com.restaurant.common.events.kitchen.MenuItemUpdatedEvent;
import com.restaurant.reservation.service.MenuService;

/**
 * Kafka consumer for menu-related events in the reservation service.
 * This class handles various types of menu events, including:
 * - Menu item updates
 * - Menu category updates
 *
 * The consumer maintains a cache of menu items and categories received from the kitchen service.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class MenuEventConsumer {

    /** Logger instance for tracking menu events */
    private static final Logger logger = LoggerFactory.getLogger(MenuEventConsumer.class);

    /** Service for managing menu data */
    private final MenuService menuService;

    /**
     * Constructs a new MenuEventConsumer with the specified service.
     *
     * @param menuService Service for managing menu data
     */
    @Autowired
    public MenuEventConsumer(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Consumes menu item events from the Kafka topic.
     * This method processes menu item updates and routes them
     * to appropriate handler methods based on the event type.
     *
     * @param event The menu item event to process
     */
    @KafkaListener(
            topics = KafkaTopics.MENU_ITEM_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "menuItemKafkaListenerContainerFactory"
    )
    public void consumeMenuItemEvents(MenuItemEvent event) {
        if (event == null) {
            logger.warn("Received null menu item event");
            return;
        }

        logger.info("Received menu item event: {}, restaurantId={}",
                event.getClass().getSimpleName(), event.getRestaurantId());

        if (event instanceof MenuItemUpdatedEvent) {
            handleMenuItemUpdatedEvent((MenuItemUpdatedEvent) event);
        }
        // Add more event handlers as needed
    }

    /**
     * Consumes menu category events from the Kafka topic.
     * This method processes menu category updates and routes them
     * to appropriate handler methods based on the event type.
     *
     * @param event The menu category event to process
     */
    @KafkaListener(
            topics = KafkaTopics.MENU_CATEGORY_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "menuItemKafkaListenerContainerFactory"
    )
    public void consumeMenuCategoryEvents(MenuItemEvent event) {
        if (event == null) {
            logger.warn("Received null menu category event");
            return;
        }

        logger.info("Received menu category event: {}, restaurantId={}",
                event.getClass().getSimpleName(), event.getRestaurantId());

        if (event instanceof MenuCategoryUpdatedEvent) {
            handleMenuCategoryUpdatedEvent((MenuCategoryUpdatedEvent) event);
        }
        // Add more event handlers as needed
    }

    /**
     * Handles menu item updated events.
     * This method updates the local cache of menu items with the
     * information received from the kitchen service.
     *
     * @param event The menu item updated event to process
     */
    private void handleMenuItemUpdatedEvent(MenuItemUpdatedEvent event) {
        logger.info("Processing menu item updated event for item: {}", event.getMenuItemId());

        try {
            menuService.createOrUpdateMenuItem(
                    event.getMenuItemId(),
                    event.getRestaurantId(),
                    event.getName(),
                    event.getDescription(),
                    event.getPrice(),
                    event.getCategoryId(),
                    event.getCategoryName(),
                    event.isAvailable(),
                    event.isActive(),
                    event.getImageUrl()
            );
            logger.info("Successfully processed menu item updated event for item: {}", event.getMenuItemId());
        } catch (Exception e) {
            logger.error("Error processing menu item updated event for item: {}", event.getMenuItemId(), e);
        }
    }

    /**
     * Handles menu category updated events.
     * This method updates the local cache of menu categories with the
     * information received from the kitchen service.
     *
     * @param event The menu category updated event to process
     */
    private void handleMenuCategoryUpdatedEvent(MenuCategoryUpdatedEvent event) {
        logger.info("Processing menu category updated event for category: {}", event.getCategoryId());

        try {
            menuService.createOrUpdateCategory(
                    event.getCategoryId(),
                    event.getRestaurantId(),
                    event.getName(),
                    event.getDescription(),
                    event.getDisplayOrder(),
                    event.isActive()
            );
            logger.info("Successfully processed menu category updated event for category: {}", event.getCategoryId());
        } catch (Exception e) {
            logger.error("Error processing menu category updated event for category: {}", event.getCategoryId(), e);
        }
    }
}
