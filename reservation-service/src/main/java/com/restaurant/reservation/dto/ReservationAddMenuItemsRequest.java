package com.restaurant.reservation.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

/**
 * Data Transfer Object (DTO) representing a request to add menu items to an existing reservation.
 * Contains the list of menu items to be added to the reservation.
 *
 * This class includes validation constraints to ensure that:
 * - At least one menu item is provided
 * - Each menu item selection is valid
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class ReservationAddMenuItemsRequest {

    /** List of menu items to be added to the reservation */
    @NotEmpty(message = "At least one menu item must be provided")
    @Valid
    private List<MenuItemSelectionDTO> menuItems = new ArrayList<>();

    /**
     * Default constructor.
     */
    public ReservationAddMenuItemsRequest() {
    }

    /**
     * Gets the list of menu items to be added to the reservation.
     *
     * @return The list of menu items
     */
    public List<MenuItemSelectionDTO> getMenuItems() {
        return menuItems;
    }

    /**
     * Sets the list of menu items to be added to the reservation.
     *
     * @param menuItems The list of menu items to set
     */
    public void setMenuItems(List<MenuItemSelectionDTO> menuItems) {
        this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
    }

    /**
     * Adds a menu item to the reservation.
     *
     * @param menuItem The menu item to add
     */
    public void addMenuItem(MenuItemSelectionDTO menuItem) {
        if (menuItem != null) {
            this.menuItems.add(menuItem);
        }
    }
}
