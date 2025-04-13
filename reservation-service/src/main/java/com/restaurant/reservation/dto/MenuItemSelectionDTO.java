package com.restaurant.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for selecting menu items when creating or updating a reservation.
 * This class is used to transfer menu item selection data from the client to the server.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class MenuItemSelectionDTO {
    
    /** ID of the menu item */
    @NotBlank(message = "Menu item ID is required")
    private String menuItemId;
    
    /** Quantity of the menu item */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
    
    /** Special instructions for the menu item */
    @Size(max = 500, message = "Special instructions cannot exceed 500 characters")
    private String specialInstructions;
    
    /**
     * Default constructor.
     */
    public MenuItemSelectionDTO() {
    }
    
    /**
     * Creates a new menu item selection with the specified details.
     * 
     * @param menuItemId The ID of the menu item
     * @param quantity The quantity of the menu item
     * @param specialInstructions Special instructions for the menu item
     */
    public MenuItemSelectionDTO(String menuItemId, int quantity, String specialInstructions) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
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
}
