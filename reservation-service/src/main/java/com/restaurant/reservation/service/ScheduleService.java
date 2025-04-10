package com.restaurant.reservation.service;

import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.reservation.domain.models.Schedule;
import com.restaurant.reservation.domain.repositories.ScheduleRepository;
import com.restaurant.reservation.dto.ScheduleDTO;
import com.restaurant.reservation.dto.ScheduleUpdateRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing restaurant schedules and operating hours.
 * This service handles:
 * - Retrieving and managing restaurant schedules
 * - Setting default operating hours based on day of week
 * - Updating schedule information including special hours and capacity
 * - Converting schedule entities to DTOs with formatted time information
 * 
 * The service ensures that restaurants have consistent operating hours while allowing
 * for custom schedules and special hours when needed.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Service
public class ScheduleService {

    /** Repository for managing schedule data */
    private final ScheduleRepository scheduleRepository;

    /**
     * Constructs a new ScheduleService with required dependencies.
     *
     * @param scheduleRepository Repository for schedule data
     */
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Retrieves the schedule for a restaurant over a specified date range.
     * This method:
     * 1. Fetches existing schedules from the database
     * 2. Creates default schedules for any missing dates
     * 3. Sets default operating hours based on day of week
     * 4. Returns a sorted list of schedule DTOs
     *
     * @param restaurantId The ID of the restaurant
     * @param startDate The start date of the schedule range
     * @param endDate The end date of the schedule range
     * @return List of ScheduleDTO objects for the specified date range
     */
    public List<ScheduleDTO> getScheduleForRestaurant(String restaurantId, LocalDate startDate, LocalDate endDate) {
        List<Schedule> schedules = scheduleRepository.findByRestaurantIdAndDateBetween(
                restaurantId, startDate, endDate);
        
        // Create entries for any missing dates
        LocalDate currentDate = startDate;
        List<LocalDate> existingDates = schedules.stream()
                .map(Schedule::getDate)
                .collect(Collectors.toList());
        
        List<Schedule> allSchedules = new ArrayList<>(schedules);
        
        while (!currentDate.isAfter(endDate)) {
            if (!existingDates.contains(currentDate)) {
                Schedule newSchedule = new Schedule(restaurantId, currentDate);
                
                // Set default operating hours based on day of week
                setDefaultHours(newSchedule, currentDate.getDayOfWeek());
                
                allSchedules.add(newSchedule);
            }
            currentDate = currentDate.plusDays(1);
        }
        
        return allSchedules.stream()
                .sorted((s1, s2) -> s1.getDate().compareTo(s2.getDate()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates the schedule for a specific restaurant and date.
     * This method:
     * 1. Retrieves or creates a new schedule for the specified date
     * 2. Updates schedule properties based on the update request
     * 3. Handles special cases like custom operating hours
     * 4. Saves and returns the updated schedule as a DTO
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date to update
     * @param updateRequest The update request containing new schedule information
     * @return Updated ScheduleDTO object
     */
    @Transactional
    public ScheduleDTO updateSchedule(String restaurantId, LocalDate date, 
                                    ScheduleUpdateRequest updateRequest) {
        Schedule schedule = scheduleRepository.findByRestaurantIdAndDate(restaurantId, date)
                .orElse(new Schedule(restaurantId, date));
        
        if (updateRequest.isClosed() != schedule.isClosed()) {
            schedule.setClosed(updateRequest.isClosed());
        }
        
        if (updateRequest.getOpenTime() != null) {
            schedule.setOpenTime(updateRequest.getOpenTime());
            schedule.setCustomOpenTime(true);
        }
        
        if (updateRequest.getCloseTime() != null) {
            schedule.setCloseTime(updateRequest.getCloseTime());
            schedule.setCustomCloseTime(true);
        }
        
        if (updateRequest.getSpecialHoursDescription() != null) {
            schedule.setSpecialHoursDescription(updateRequest.getSpecialHoursDescription());
        }
        
        if (updateRequest.getTotalCapacity() > 0) {
            schedule.setTotalCapacity(updateRequest.getTotalCapacity());
        }
        
        return convertToDTO(scheduleRepository.save(schedule));
    }

    /**
     * Sets default operating hours for a schedule based on the day of week.
     * Default hours are:
     * - Weekdays: 10:00 AM - 10:00 PM
     * - Friday/Saturday: 10:00 AM - 11:00 PM
     * - Monday: Closed
     *
     * @param schedule The schedule to set default hours for
     * @param dayOfWeek The day of week to determine default hours
     */
    private void setDefaultHours(Schedule schedule, DayOfWeek dayOfWeek) {
        // Default hours: 10 AM - 10 PM
        LocalTime defaultOpenTime = LocalTime.of(10, 0);
        LocalTime defaultCloseTime = LocalTime.of(22, 0);
        
        // Adjust for weekends
        if (dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY) {
            defaultCloseTime = LocalTime.of(23, 0);
        }
        
        // Default closed on Mondays
        if (dayOfWeek == DayOfWeek.MONDAY) {
            schedule.setClosed(true);
        }
        
        schedule.setOpenTime(defaultOpenTime);
        schedule.setCloseTime(defaultCloseTime);
    }

    /**
     * Converts a Schedule entity to a ScheduleDTO.
     * This method:
     * 1. Copies all basic schedule information
     * 2. Calculates operating hours duration
     * 3. Formats time strings for display
     * 4. Includes capacity and booking information
     *
     * @param schedule The schedule entity to convert
     * @return A new ScheduleDTO object with formatted information
     */
    private ScheduleDTO convertToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setRestaurantId(schedule.getRestaurantId());
        dto.setDate(schedule.getDate());
        dto.setDayOfWeek(schedule.getDate().getDayOfWeek().toString());
        dto.setClosed(schedule.isClosed());
        dto.setOpenTime(schedule.getOpenTime());
        dto.setCloseTime(schedule.getCloseTime());
        dto.setCustomOpenTime(schedule.isCustomOpenTime());
        dto.setCustomCloseTime(schedule.isCustomCloseTime());
        dto.setSpecialHoursDescription(schedule.getSpecialHoursDescription());
        dto.setTotalCapacity(schedule.getTotalCapacity());
        dto.setAvailableCapacity(schedule.getAvailableCapacity());
        dto.setBookedCapacity(schedule.getBookedCapacity());
        dto.setBookedTables(schedule.getBookedTables());
        
        // Calculate operating hours
        if (!schedule.isClosed() && schedule.getOpenTime() != null && schedule.getCloseTime() != null) {
            long minutes = ChronoUnit.MINUTES.between(schedule.getOpenTime(), schedule.getCloseTime());
            dto.setOperatingHours(String.format("%d hours %d minutes", minutes / 60, minutes % 60));
            
            // Format times
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            dto.setFormattedOpenTime(schedule.getOpenTime().format(timeFormatter));
            dto.setFormattedCloseTime(schedule.getCloseTime().format(timeFormatter));
        }
        
        return dto;
    }
}