package com.restaurant.restaurant.domain.models;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "operating_hours", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"restaurant_id", "day_of_week"}))
public class OperatingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime openTime;

    @Column(nullable = false)
    private LocalTime closeTime;

    @Column(nullable = false)
    private boolean closed = false;

    // Additional fields
    private LocalTime breakStartTime; // For restaurants that close between lunch and dinner
    private LocalTime breakEndTime;
    private String specialHoursDescription; // For holidays or special events

    // Constructors
    public OperatingHours() {
    }

    public OperatingHours(Restaurant restaurant, DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime) {
        this.restaurant = restaurant;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    // Method to check if the restaurant is open at a specific time on this day
    public boolean isOpenAt(LocalTime time) {
        if (closed) {
            return false;
        }

        // If there's a break period
        if (breakStartTime != null && breakEndTime != null) {
            return (time.isAfter(openTime) || time.equals(openTime)) && 
                   (time.isBefore(breakStartTime)) || 
                   (time.isAfter(breakEndTime) || time.equals(breakEndTime)) && 
                   (time.isBefore(closeTime));
        }

        // Regular hours
        return (time.isAfter(openTime) || time.equals(openTime)) && time.isBefore(closeTime);
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
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

    public LocalTime getBreakStartTime() {
        return breakStartTime;
    }

    public void setBreakStartTime(LocalTime breakStartTime) {
        this.breakStartTime = breakStartTime;
    }

    public LocalTime getBreakEndTime() {
        return breakEndTime;
    }

    public void setBreakEndTime(LocalTime breakEndTime) {
        this.breakEndTime = breakEndTime;
    }

    public String getSpecialHoursDescription() {
        return specialHoursDescription;
    }

    public void setSpecialHoursDescription(String specialHoursDescription) {
        this.specialHoursDescription = specialHoursDescription;
    }
}