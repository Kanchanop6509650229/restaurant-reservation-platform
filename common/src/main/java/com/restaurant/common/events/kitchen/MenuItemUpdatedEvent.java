package com.restaurant.common.events.kitchen;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents an update to a menu item.
 * This event is published whenever a menu item is created, updated, or deleted.
 * It contains all the necessary information about the menu item for other services to process.
 * 
 * @author Restaurant Team
 * @version 1.0
 */
public class MenuItemUpdatedEvent extends BaseEvent implements MenuItemEvent {
    
    /** The unique identifier of the restaurant associated with this menu item */
    private final String restaurantId;
    
    /** The unique identifier of the menu item */
    private final String menuItemId;
    
    /** The name of the menu item */
    private final String name;
    
    /** The description of the menu item */
    private final String description;
    
    /** The price of the menu item */
    private final double price;
    
    /** The category ID of the menu item */
    private final String categoryId;
    
    /** The category name of the menu item */
    private final String categoryName;
    
    /** Flag indicating if the menu item is available */
    private final boolean available;
    
    /** Flag indicating if the menu item is active or deleted */
    private final boolean active;
    
    /** Image URL of the menu item */
    private final String imageUrl;

    /**
     * Creates a new MenuItemUpdatedEvent with the specified details.
     * 
     * @param restaurantId The ID of the restaurant
     * @param menuItemId The ID of the menu item
     * @param name The name of the menu item
     * @param description The description of the menu item
     * @param price The price of the menu item
     * @param categoryId The category ID of the menu item
     * @param categoryName The category name of the menu item
     * @param available Flag indicating if the menu item is available
     * @param active Flag indicating if the menu item is active
     * @param imageUrl Image URL of the menu item
     */
    public MenuItemUpdatedEvent(
            String restaurantId, 
            String menuItemId, 
            String name, 
            String description, 
            double price, 
            String categoryId, 
            String categoryName, 
            boolean available, 
            boolean active, 
            String imageUrl) {
        super("MENU_ITEM_UPDATED");
        this.restaurantId = restaurantId;
        this.menuItemId = menuItemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.available = available;
        this.active = active;
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the restaurant ID associated with this menu item.
     * 
     * @return The restaurant ID
     */
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Gets the menu item ID.
     * 
     * @return The menu item ID
     */
    public String getMenuItemId() {
        return menuItemId;
    }

    /**
     * Gets the name of the menu item.
     * 
     * @return The menu item name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the menu item.
     * 
     * @return The menu item description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the price of the menu item.
     * 
     * @return The menu item price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Gets the category ID of the menu item.
     * 
     * @return The category ID
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Gets the category name of the menu item.
     * 
     * @return The category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Checks if the menu item is available.
     * 
     * @return true if available, false otherwise
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Checks if the menu item is active.
     * 
     * @return true if active, false if deleted
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the image URL of the menu item.
     * 
     * @return The image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }
}
