package com.restaurant.reservation.domain.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservation_quotas", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"restaurant_id", "date", "time_slot"}))
public class ReservationQuota {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "restaurant_id", nullable = false)
    private String restaurantId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "time_slot", nullable = false)
    private LocalTime timeSlot;

    @Column(nullable = false)
    private int maxReservations;

    @Column(nullable = false)
    private int currentReservations;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(nullable = false)
    private int currentCapacity;

    private int thresholdPercentage;

    // Constructors
    public ReservationQuota() {
    }

    public ReservationQuota(String restaurantId, LocalDate date, LocalTime timeSlot, 
                            int maxReservations, int maxCapacity) {
        this.restaurantId = restaurantId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.maxReservations = maxReservations;
        this.maxCapacity = maxCapacity;
        this.currentReservations = 0;
        this.currentCapacity = 0;
        this.thresholdPercentage = 100; // Default to 100%
    }

    // Business methods
    public boolean hasAvailability() {
        return currentReservations < maxReservations && 
               (thresholdPercentage == 100 || 
                (currentCapacity * 100 / maxCapacity) < thresholdPercentage);
    }

    public boolean canAccommodateParty(int partySize) {
        return (currentCapacity + partySize) <= maxCapacity;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(LocalTime timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getMaxReservations() {
        return maxReservations;
    }

    public void setMaxReservations(int maxReservations) {
        this.maxReservations = maxReservations;
    }

    public int getCurrentReservations() {
        return currentReservations;
    }

    public void setCurrentReservations(int currentReservations) {
        this.currentReservations = currentReservations;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public int getThresholdPercentage() {
        return thresholdPercentage;
    }

    public void setThresholdPercentage(int thresholdPercentage) {
        this.thresholdPercentage = thresholdPercentage;
    }
}