package com.restaurant.reservation.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entity class representing a menu item selected for a reservation.
 * This class links reservations with menu items and tracks quantities.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "reservation_menu_items")
public class ReservationMenuItem {

    /** Unique identifier for the reservation menu item */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** The reservation this menu item is associated with */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    /** The menu item selected for the reservation */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    /** Quantity of the menu item */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private int quantity;

    /** Special instructions for the menu item */
    @Column(length = 500)
    private String specialInstructions;

    /** Price of the menu item at the time of selection */
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    @Column(nullable = false)
    private double price;

    /** Timestamp when the menu item was added to the reservation */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the menu item selection was last updated */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor for JPA.
     */
    public ReservationMenuItem() {
    }

    /**
     * Creates a new reservation menu item with the specified details.
     * 
     * @param reservation The reservation
     * @param menuItem The menu item
     * @param quantity The quantity of the menu item
     * @param specialInstructions Special instructions for the menu item
     * @param price The price of the menu item at the time of selection
     */
    public ReservationMenuItem(Reservation reservation, MenuItem menuItem, int quantity, 
                              String specialInstructions, double price) {
        this.reservation = reservation;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
     * Gets the reservation associated with this menu item.
     * 
     * @return The reservation
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * Sets the reservation associated with this menu item.
     * 
     * @param reservation The reservation to set
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    /**
     * Gets the menu item selected for the reservation.
     * 
     * @return The menu item
     */
    public MenuItem getMenuItem() {
        return menuItem;
    }

    /**
     * Sets the menu item selected for the reservation.
     * 
     * @param menuItem The menu item to set
     */
    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
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

    /**
     * Updates the reservation menu item with the specified details.
     * 
     * @param quantity The quantity of the menu item
     * @param specialInstructions Special instructions for the menu item
     */
    public void update(int quantity, String specialInstructions) {
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
        this.updatedAt = LocalDateTime.now();
    }
}
