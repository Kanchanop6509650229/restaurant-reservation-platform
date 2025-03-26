package com.restaurant.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleDTO {

    private String id;
    private String restaurantId;
    private LocalDate date;
    private String dayOfWeek;
    private boolean closed;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean customOpenTime;
    private boolean customCloseTime;
    private String specialHoursDescription;
    private int totalCapacity;
    private int availableCapacity;
    private int bookedCapacity;
    private int bookedTables;
    private String operatingHours;
    private String formattedOpenTime;
    private String formattedCloseTime;

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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
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

    public boolean isCustomOpenTime() {
        return customOpenTime;
    }

    public void setCustomOpenTime(boolean customOpenTime) {
        this.customOpenTime = customOpenTime;
    }

    public boolean isCustomCloseTime() {
        return customCloseTime;
    }

    public void setCustomCloseTime(boolean customCloseTime) {
        this.customCloseTime = customCloseTime;
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

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public String getFormattedOpenTime() {
        return formattedOpenTime;
    }

    public void setFormattedOpenTime(String formattedOpenTime) {
        this.formattedOpenTime = formattedOpenTime;
    }

    public String getFormattedCloseTime() {
        return formattedCloseTime;
    }

    public void setFormattedCloseTime(String formattedCloseTime) {
        this.formattedCloseTime = formattedCloseTime;
    }
}