package com.restaurant.restaurant.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Data Transfer Object for batch updating operating hours of a restaurant.
 * This class provides:
 * - Bulk update capability for multiple days' operating hours
 * - Structured format for operating hours entries
 * - Validation support for time ranges
 * 
 * Used in the restaurant service to handle bulk updates of operating hours
 * for multiple days of the week in a single request.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
public class OperatingHoursBatchUpdateRequest {
    /** List of operating hour entries for different days of the week */
    private List<OperatingHourEntry> operatingHours;
    
    /**
     * Inner class representing a single operating hour entry.
     * This class provides:
     * - Day of week specification
     * - Opening and closing time management
     * - Time range validation support
     */
    public static class OperatingHourEntry {
        /** Day of the week for these operating hours */
        private DayOfWeek dayOfWeek;
        
        /** Time when the restaurant opens */
        private LocalTime openTime;
        
        /** Time when the restaurant closes */
        private LocalTime closeTime;
        
        /**
         * Gets the day of the week for these operating hours.
         *
         * @return The day of the week
         */
        public DayOfWeek getDayOfWeek() {
            return dayOfWeek;
        }
        
        /**
         * Sets the day of the week for these operating hours.
         *
         * @param dayOfWeek The day of the week
         */
        public void setDayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }
        
        /**
         * Gets the opening time of the restaurant.
         *
         * @return The opening time
         */
        public LocalTime getOpenTime() {
            return openTime;
        }
        
        /**
         * Sets the opening time of the restaurant.
         *
         * @param openTime The opening time
         */
        public void setOpenTime(LocalTime openTime) {
            this.openTime = openTime;
        }
        
        /**
         * Gets the closing time of the restaurant.
         *
         * @return The closing time
         */
        public LocalTime getCloseTime() {
            return closeTime;
        }
        
        /**
         * Sets the closing time of the restaurant.
         *
         * @param closeTime The closing time
         */
        public void setCloseTime(LocalTime closeTime) {
            this.closeTime = closeTime;
        }
    }
    
    /**
     * Gets the list of operating hour entries.
     *
     * @return The list of operating hour entries
     */
    public List<OperatingHourEntry> getOperatingHours() {
        return operatingHours;
    }
    
    /**
     * Sets the list of operating hour entries.
     *
     * @param operatingHours The list of operating hour entries
     */
    public void setOperatingHours(List<OperatingHourEntry> operatingHours) {
        this.operatingHours = operatingHours;
    }
}