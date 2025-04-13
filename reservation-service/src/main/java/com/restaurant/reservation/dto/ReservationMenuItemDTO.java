package com.restaurant.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for reservation menu items.
 * This class is used to transfer reservation menu item data between the service layer and API controllers.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class ReservationMenuItemDTO {
    
    /** Unique identifier for the reservation menu item */
    private String id;
    
    /** ID of the reservation this menu item is associated with */
    private String reservationId;
    
    /** ID of the menu item */
    private String menuItemId;
    
    /** Name of the menu item */
    private String menuItemName;
    
    /** Quantity of the menu item */
    private int quantity;
    
    /** Special instructions for the menu item */
    private String specialInstructions;
    
    /** Price of the menu item at the time of selection */
    private double price;
    
    /** Timestamp when the menu item was added to the reservation */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    /** Timestamp when the menu item selection was last updated */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Default constructor.
     */
    public ReservationMenuItemDTO() {
    }

    /**
     * Gets the ID of the reservation menu item.
     * 
     * @return The reservation menu item ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the reservation menu item.
     * 
     * @param id The reservation menu item ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the ID of the reservation this menu item is associated with.
     * 
     * @return The reservation ID
     */
    public String getReservationId() {
        return reservationId;
    }

    /**
     * Sets the ID of the reservation this menu item is associated with.
     * 
     * @param reservationId The reservation ID to set
     */
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    /**
     * Gets the ID of the menu item.
     * 
     * @return The menu item ID
     */
    public String getMenuItemId() {
        return menuItemId;
    }

    /**
     * Sets the ID of the menu item.
     * 
     * @param menuItemId The menu item ID to set
     */
    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    /**
     * Gets the name of the menu item.
     * 
     * @return The menu item name
     */
    public String getMenuItemName() {
        return menuItemName;
    }

    /**
     * Sets the name of the menu item.
     * 
     * @param menuItemName The menu item name to set
     */
    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    /**
     * Gets the quantity of the menu item.
     * 
     * @return The quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the menu item.
     * 
     * @param quantity The quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the special instructions for the menu item.
     * 
     * @return The special instructions
     */
    public String getSpecialInstructions() {
        return specialInstructions;
    }

    /**
     * Sets the special instructions for the menu item.
     * 
     * @param specialInstructions The special instructions to set
     */
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    /**
     * Gets the price of the menu item at the time of selection.
     * 
     * @return The price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the menu item at the time of selection.
     * 
     * @param price The price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the creation timestamp of the reservation menu item.
     * 
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the reservation menu item.
     * 
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp of the reservation menu item.
     * 
     * @return The last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp of the reservation menu item.
     * 
     * @param updatedAt The last update timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
