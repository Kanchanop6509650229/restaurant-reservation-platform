package com.restaurant.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for menu categories.
 * This class is used to transfer menu category data between the service layer and API controllers.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class MenuCategoryDTO {
    
    /** Unique identifier for the menu category */
    private String id;
    
    /** ID of the restaurant this menu category belongs to */
    private String restaurantId;
    
    /** Name of the menu category */
    private String name;
    
    /** Description of the menu category */
    private String description;
    
    /** Display order of the menu category */
    private int displayOrder;
    
    /** Flag indicating if the menu category is active */
    private boolean active;
    
    /** Timestamp when the menu category was created */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    /** Timestamp when the menu category was last updated */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /** Menu items in this category */
    private List<MenuItemDTO> menuItems = new ArrayList<>();

    /**
     * Default constructor.
     */
    public MenuCategoryDTO() {
    }

    /**
     * Gets the ID of the menu category.
     * 
     * @return The menu category ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the menu category.
     * 
     * @param id The menu category ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the restaurant ID associated with this menu category.
     * 
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the restaurant ID associated with this menu category.
     * 
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
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
     * Sets the name of the menu category.
     * 
     * @param name The menu category name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets the description of the menu category.
     * 
     * @param description The menu category description to set
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Sets the display order of the menu category.
     * 
     * @param displayOrder The display order to set
     */
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * Checks if the menu category is active.
     * 
     * @return true if active, false if deleted
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of the menu category.
     * 
     * @param active The active status to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the creation timestamp of the menu category.
     * 
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the menu category.
     * 
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp of the menu category.
     * 
     * @return The last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp of the menu category.
     * 
     * @param updatedAt The last update timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the menu items in this category.
     * 
     * @return The list of menu items
     */
    public List<MenuItemDTO> getMenuItems() {
        return menuItems;
    }

    /**
     * Sets the menu items in this category.
     * 
     * @param menuItems The list of menu items to set
     */
    public void setMenuItems(List<MenuItemDTO> menuItems) {
        this.menuItems = menuItems;
    }

    /**
     * Adds a menu item to this category.
     * 
     * @param menuItem The menu item to add
     */
    public void addMenuItem(MenuItemDTO menuItem) {
        this.menuItems.add(menuItem);
    }
}
