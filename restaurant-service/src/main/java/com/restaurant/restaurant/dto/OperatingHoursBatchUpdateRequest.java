package com.restaurant.restaurant.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class OperatingHoursBatchUpdateRequest {
    private List<OperatingHourEntry> operatingHours;
    
    public static class OperatingHourEntry {
        private DayOfWeek dayOfWeek;
        private LocalTime openTime;
        private LocalTime closeTime;
        
        // Getters and setters
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
    }
    
    // Getters and setters
    public List<OperatingHourEntry> getOperatingHours() {
        return operatingHours;
    }
    
    public void setOperatingHours(List<OperatingHourEntry> operatingHours) {
        this.operatingHours = operatingHours;
    }
}