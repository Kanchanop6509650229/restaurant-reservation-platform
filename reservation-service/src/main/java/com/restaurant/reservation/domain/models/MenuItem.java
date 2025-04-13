package com.restaurant.reservation.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a menu item in the reservation service.
 * This class stores menu item information received from the kitchen service.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "menu_items", 
       indexes = {
           @Index(name = "idx_menu_item_restaurant_id", columnList = "restaurantId"),
           @Index(name = "idx_menu_item_category_id", columnList = "category_id"),
           @Index(name = "idx_menu_item_active", columnList = "active"),
           @Index(name = "idx_menu_item_available", columnList = "available")
       })
public class MenuItem {

    /** Unique identifier for the menu item */
    @Id
    private String id;

    /** ID of the restaurant this menu item belongs to */
    @NotBlank(message = "Restaurant ID is required")
    @Column(nullable = false)
    private String restaurantId;

    /** Name of the menu item */
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Column(nullable = false)
    private String name;

    /** Description of the menu item */
    @Size(max = 500, message = "Description must be at most 500 characters")
    @Column(length = 500)
    private String description;

    /** Price of the menu item */
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    @Column(nullable = false)
    private double price;

    /** Category of the menu item */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private MenuCategory category;

    /** Flag indicating if the menu item is available */
    @Column(nullable = false)
    private boolean available = true;

    /** Flag indicating if the menu item is active */
    @Column(nullable = false)
    private boolean active = true;

    /** Image URL of the menu item */
    private String imageUrl;

    /** Timestamp when the menu item was created */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the menu item was last updated */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** Reservations that include this menu item */
    @OneToMany(mappedBy = "menuItem")
    private Set<ReservationMenuItem> reservationMenuItems = new HashSet<>();

    /**
     * Default constructor for JPA.
     */
    public MenuItem() {
    }

    /**
     * Creates a new menu item with the specified details.
     * 
     * @param id The ID of the menu item
     * @param restaurantId The ID of the restaurant
     * @param name The name of the menu item
     * @param description The description of the menu item
     * @param price The price of the menu item
     * @param category The category of the menu item
     * @param available Flag indicating if the menu item is available
     * @param active Flag indicating if the menu item is active
     * @param imageUrl Image URL of the menu item
     */
    public MenuItem(String id, String restaurantId, String name, String description, double price, 
                   MenuCategory category, boolean available, boolean active, String imageUrl) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
        this.active = active;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
     * Gets the category of the menu item.
     * 
     * @return The menu item category
     */
    public MenuCategory getCategory() {
        return category;
    }

    /**
     * Sets the category of the menu item.
     * 
     * @param category The menu item category to set
     */
    public void setCategory(MenuCategory category) {
        this.category = category;
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

    /**
     * Gets the reservations that include this menu item.
     * 
     * @return The set of reservation menu items
     */
    public Set<ReservationMenuItem> getReservationMenuItems() {
        return reservationMenuItems;
    }

    /**
     * Sets the reservations that include this menu item.
     * 
     * @param reservationMenuItems The set of reservation menu items to set
     */
    public void setReservationMenuItems(Set<ReservationMenuItem> reservationMenuItems) {
        this.reservationMenuItems = reservationMenuItems;
    }

    /**
     * Updates the menu item with the specified details.
     * 
     * @param name The name of the menu item
     * @param description The description of the menu item
     * @param price The price of the menu item
     * @param category The category of the menu item
     * @param available Flag indicating if the menu item is available
     * @param active Flag indicating if the menu item is active
     * @param imageUrl Image URL of the menu item
     */
    public void update(String name, String description, double price, MenuCategory category, 
                      boolean available, boolean active, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
        this.active = active;
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }
}
