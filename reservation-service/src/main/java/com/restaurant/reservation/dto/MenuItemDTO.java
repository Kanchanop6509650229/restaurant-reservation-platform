package com.restaurant.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for menu items.
 * This class is used to transfer menu item data between the service layer and API controllers.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class MenuItemDTO {
    
    /** Unique identifier for the menu item */
    private String id;
    
    /** ID of the restaurant this menu item belongs to */
    private String restaurantId;
    
    /** Name of the menu item */
    private String name;
    
    /** Description of the menu item */
    private String description;
    
    /** Price of the menu item */
    private double price;
    
    /** ID of the category this menu item belongs to */
    private String categoryId;
    
    /** Name of the category this menu item belongs to */
    private String categoryName;
    
    /** Flag indicating if the menu item is available */
    private boolean available;
    
    /** Flag indicating if the menu item is active */
    private boolean active;
    
    /** Image URL of the menu item */
    private String imageUrl;
    
    /** Timestamp when the menu item was created */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    /** Timestamp when the menu item was last updated */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Default constructor.
     */
    public MenuItemDTO() {
    }

    /**
     * Gets the ID of the menu item.
     * 
     * @return The menu item ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the menu item.
     * 
     * @param id The menu item ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the restaurant ID associated with this menu item.
     * 
     * @return The restaurant ID
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Sets the restaurant ID associated with this menu item.
     * 
     * @param restaurantId The restaurant ID to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
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
     * Sets the name of the menu item.
     * 
     * @param name The menu item name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets the description of the menu item.
     * 
     * @param description The menu item description to set
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Sets the price of the menu item.
     * 
     * @param price The menu item price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the ID of the category this menu item belongs to.
     * 
     * @return The category ID
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the ID of the category this menu item belongs to.
     * 
     * @param categoryId The category ID to set
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Gets the name of the category this menu item belongs to.
     * 
     * @return The category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the name of the category this menu item belongs to.
     * 
     * @param categoryName The category name to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
     * Sets the availability of the menu item.
     * 
     * @param available The availability to set
     */
    public void setAvailable(boolean available) {
        this.available = available;
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
     * Sets the active status of the menu item.
     * 
     * @param active The active status to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the image URL of the menu item.
     * 
     * @return The image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the image URL of the menu item.
     * 
     * @param imageUrl The image URL to set
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the creation timestamp of the menu item.
     * 
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the menu item.
     * 
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp of the menu item.
     * 
     * @return The last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp of the menu item.
     * 
     * @param updatedAt The last update timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
