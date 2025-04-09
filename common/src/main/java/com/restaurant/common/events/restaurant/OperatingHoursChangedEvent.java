package com.restaurant.common.events.restaurant;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.restaurant.common.events.BaseEvent;

/**
 * Event class that represents a change in a restaurant's operating hours for a specific day.
 * This event is published whenever a restaurant modifies its opening or closing times
 * for a particular day of the week. It captures both the old and new times to track
 * the change and allow for proper handling by dependent services.
 * Extends BaseEvent with type "OPERATING_HOURS_CHANGED" and implements RestaurantEvent interface.
 */
public class OperatingHoursChangedEvent extends BaseEvent implements RestaurantEvent {
    /**
     * The unique identifier of the restaurant whose operating hours changed.
     */
    private final String restaurantId;
    
    /**
     * The day of the week for which the operating hours were changed.
     */
    private final DayOfWeek dayOfWeek;
    
    /**
     * The previous opening time for the specified day before the change.
     */
    private final LocalTime oldOpenTime;
    
    /**
     * The previous closing time for the specified day before the change.
     */
    private final LocalTime oldCloseTime;
    
    /**
     * The new opening time for the specified day after the change.
     */
    private final LocalTime newOpenTime;
    
    /**
     * The new closing time for the specified day after the change.
     */
    private final LocalTime newCloseTime;
    
    /**
     * Constructs a new OperatingHoursChangedEvent with details about the time changes.
     *
     * @param restaurantId  The ID of the restaurant whose hours changed
     * @param dayOfWeek    The day of the week for which hours were modified
     * @param oldOpenTime  The previous opening time
     * @param oldCloseTime The previous closing time
     * @param newOpenTime  The new opening time
     * @param newCloseTime The new closing time
     */
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Gets the day of the week for which operating hours were changed.
     *
     * @return The day of the week as a DayOfWeek enum value
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    
    /**
     * Gets the previous opening time before the change.
     *
     * @return The old opening time
     */
    public LocalTime getOldOpenTime() {
        return oldOpenTime;
    }
    
    /**
     * Gets the previous closing time before the change.
     *
     * @return The old closing time
     */
    public LocalTime getOldCloseTime() {
        return oldCloseTime;
    }
    
    /**
     * Gets the new opening time after the change.
     *
     * @return The new opening time
     */
    public LocalTime getNewOpenTime() {
        return newOpenTime;
    }
    
    /**
     * Gets the new closing time after the change.
     *
     * @return The new closing time
     */
    public LocalTime getNewCloseTime() {
        return newCloseTime;
    }
}