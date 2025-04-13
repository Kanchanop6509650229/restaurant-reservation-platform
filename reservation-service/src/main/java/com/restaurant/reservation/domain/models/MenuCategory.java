package com.restaurant.reservation.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a menu category in the reservation service.
 * This class stores menu category information received from the kitchen service.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "menu_categories", 
       indexes = {
           @Index(name = "idx_menu_category_restaurant_id", columnList = "restaurantId"),
           @Index(name = "idx_menu_category_active", columnList = "active")
       })
public class MenuCategory {

    /** Unique identifier for the menu category */
    @Id
    private String id;

    /** ID of the restaurant this menu category belongs to */
    @NotBlank(message = "Restaurant ID is required")
    @Column(nullable = false)
    private String restaurantId;

    /** Name of the menu category */
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Column(nullable = false)
    private String name;

    /** Description of the menu category */
    @Size(max = 500, message = "Description must be at most 500 characters")
    @Column(length = 500)
    private String description;

    /** Display order of the menu category */
    @Column(nullable = false)
    private int displayOrder;

    /** Flag indicating if the menu category is active */
    @Column(nullable = false)
    private boolean active = true;

    /** Timestamp when the menu category was created */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the menu category was last updated */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** Menu items in this category */
    @OneToMany(mappedBy = "category")
    private Set<MenuItem> menuItems = new HashSet<>();

    /**
     * Default constructor for JPA.
     */
    public MenuCategory() {
    }

    /**
     * Creates a new menu category with the specified details.
     * 
     * @param id The ID of the menu category
     * @param restaurantId The ID of the restaurant
     * @param name The name of the menu category
     * @param description The description of the menu category
     * @param displayOrder The display order of the menu category
     * @param active Flag indicating if the menu category is active
     */
    public MenuCategory(String id, String restaurantId, String name, String description, int displayOrder, boolean active) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
        this.active = active;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
     * @return The set of menu items
     */
    public Set<MenuItem> getMenuItems() {
        return menuItems;
    }

    /**
     * Sets the menu items in this category.
     * 
     * @param menuItems The set of menu items to set
     */
    public void setMenuItems(Set<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    /**
     * Updates the menu category with the specified details.
     * 
     * @param name The name of the menu category
     * @param description The description of the menu category
     * @param displayOrder The display order of the menu category
     * @param active Flag indicating if the menu category is active
     */
    public void update(String name, String description, int displayOrder, boolean active) {
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }
}
