package com.restaurant.common.events.restaurant;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.restaurant.common.events.BaseEvent;

public class OperatingHoursChangedEvent extends BaseEvent implements RestaurantEvent {
    private final String restaurantId;
    private final DayOfWeek dayOfWeek;
    private final LocalTime oldOpenTime;
    private final LocalTime oldCloseTime;
    private final LocalTime newOpenTime;
    private final LocalTime newCloseTime;
    
    public OperatingHoursChangedEvent(
            String restaurantId, 
            DayOfWeek dayOfWeek, 
            LocalTime oldOpenTime, 
            LocalTime oldCloseTime, 
            LocalTime newOpenTime, 
            LocalTime newCloseTime) {
        super("OPERATING_HOURS_CHANGED");
        this.restaurantId = restaurantId;
        this.dayOfWeek = dayOfWeek;
        this.oldOpenTime = oldOpenTime;
        this.oldCloseTime = oldCloseTime;
        this.newOpenTime = newOpenTime;
        this.newCloseTime = newCloseTime;
    }
    
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    
    public LocalTime getOldOpenTime() {
        return oldOpenTime;
    }
    
    public LocalTime getOldCloseTime() {
        return oldCloseTime;
    }
    
    public LocalTime getNewOpenTime() {
        return newOpenTime;
    }
    
    public LocalTime getNewCloseTime() {
        return newCloseTime;
    }
}