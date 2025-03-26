package com.restaurant.reservation.domain.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedules", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"restaurant_id", "date"}))
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "restaurant_id", nullable = false)
    private String restaurantId;

    @Column(nullable = false)
    private LocalDate date;

    private boolean customOpenTime;
    private LocalTime openTime;

    private boolean customCloseTime;
    private LocalTime closeTime;

    @Column(nullable = false)
    private boolean closed;

    private String specialHoursDescription;
    private int totalCapacity;
    private int availableCapacity;
    private int bookedCapacity;
    private int bookedTables;

    // Constructors
    public Schedule() {
    }

    public Schedule(String restaurantId, LocalDate date) {
        this.restaurantId = restaurantId;
        this.date = date;
        this.closed = false;
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

    public boolean isCustomOpenTime() {
        return customOpenTime;
    }

    public void setCustomOpenTime(boolean customOpenTime) {
        this.customOpenTime = customOpenTime;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public boolean isCustomCloseTime() {
        return customCloseTime;
    }

    public void setCustomCloseTime(boolean customCloseTime) {
        this.customCloseTime = customCloseTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getSpecialHoursDescription() {
        return specialHoursDescription;
    }

    public void setSpecialHoursDescription(String specialHoursDescription) {
        this.specialHoursDescription = specialHoursDescription;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public int getBookedCapacity() {
        return bookedCapacity;
    }

    public void setBookedCapacity(int bookedCapacity) {
        this.bookedCapacity = bookedCapacity;
    }

    public int getBookedTables() {
        return bookedTables;
    }

    public void setBookedTables(int bookedTables) {
        this.bookedTables = bookedTables;
    }
}