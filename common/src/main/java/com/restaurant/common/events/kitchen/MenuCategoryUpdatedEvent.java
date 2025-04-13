package com.restaurant.common.events.kitchen;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents an update to a menu category.
 * This event is published whenever a menu category is created, updated, or deleted.
 * It contains all the necessary information about the menu category for other services to process.
 * 
 * @author Restaurant Team
 * @version 1.0
 */
public class MenuCategoryUpdatedEvent extends BaseEvent implements MenuItemEvent {
    
    /** The unique identifier of the restaurant associated with this menu category */
    private final String restaurantId;
    
    /** The unique identifier of the menu category */
    private final String categoryId;
    
    /** The name of the menu category */
    private final String name;
    
    /** The description of the menu category */
    private final String description;
    
    /** The display order of the menu category */
    private final int displayOrder;
    
    /** Flag indicating if the menu category is active or deleted */
    private final boolean active;

    /**
     * Creates a new MenuCategoryUpdatedEvent with the specified details.
     * 
     * @param restaurantId The ID of the restaurant
     * @param categoryId The ID of the menu category
     * @param name The name of the menu category
     * @param description The description of the menu category
     * @param displayOrder The display order of the menu category
     * @param active Flag indicating if the menu category is active
     */
    public MenuCategoryUpdatedEvent(
            String restaurantId, 
            String categoryId, 
            String name, 
            String description, 
            int displayOrder, 
            boolean active) {
        super("MENU_CATEGORY_UPDATED");
        this.restaurantId = restaurantId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
        this.active = active;
    }

    /**
     * Gets the restaurant ID associated with this menu category.
     * 
     * @return The restaurant ID
     */
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Gets the menu category ID.
     * 
     * @return The menu category ID
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Gets the name of the menu category.
     * 
     * @return The menu category name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the menu category.
     * 
     * @return The menu category description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the display order of the menu category.
     * 
     * @return The display order
     */
    public int getDisplayOrder() {
        return displayOrder;
    }

    /**
     * Checks if the menu category is active.
     * 
     * @return true if active, false if deleted
     */
    public boolean isActive() {
        return active;
    }
}
