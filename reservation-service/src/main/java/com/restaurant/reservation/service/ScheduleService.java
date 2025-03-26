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

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

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