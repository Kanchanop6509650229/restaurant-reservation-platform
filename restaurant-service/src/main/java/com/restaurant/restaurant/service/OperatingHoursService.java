package com.restaurant.restaurant.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.restaurant.common.events.restaurant.OperatingHoursChangedEvent;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.restaurant.domain.models.OperatingHours;
import com.restaurant.restaurant.domain.models.Restaurant;
import com.restaurant.restaurant.domain.repositories.OperatingHoursRepository;
import com.restaurant.restaurant.domain.repositories.RestaurantRepository;
import com.restaurant.restaurant.dto.OperatingHoursBatchUpdateRequest;
import com.restaurant.restaurant.dto.OperatingHoursBatchUpdateRequest.OperatingHourEntry;
import com.restaurant.restaurant.dto.OperatingHoursDTO;
import com.restaurant.restaurant.dto.OperatingHoursUpdateRequest;
import com.restaurant.restaurant.kafka.producers.RestaurantEventProducer;

import jakarta.transaction.Transactional;

@Service
public class OperatingHoursService {

    private final OperatingHoursRepository operatingHoursRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantEventProducer restaurantEventProducer;

    @Value("${restaurant.default.open-time:10:00}")
    private String defaultOpenTime;

    @Value("${restaurant.default.close-time:22:00}")
    private String defaultCloseTime;

    public OperatingHoursService(OperatingHoursRepository operatingHoursRepository,
                                RestaurantRepository restaurantRepository,
                                RestaurantEventProducer restaurantEventProducer) {
        this.operatingHoursRepository = operatingHoursRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantEventProducer = restaurantEventProducer;
    }

    public List<OperatingHoursDTO> getOperatingHoursByRestaurantId(String restaurantId) {
        return operatingHoursRepository.findByRestaurantId(restaurantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createDefaultOperatingHours(Restaurant restaurant) {
        LocalTime openTime = LocalTime.parse(defaultOpenTime);
        LocalTime closeTime = LocalTime.parse(defaultCloseTime);

        List<OperatingHours> hoursToSave = new ArrayList<>();
        
        for (DayOfWeek day : DayOfWeek.values()) {
            // Check if hours already exist for this day
            Optional<OperatingHours> existingHours = operatingHoursRepository
                    .findByRestaurantIdAndDayOfWeek(restaurant.getId(), day);
            
            if (existingHours.isPresent()) {
                continue;
            }
            
            OperatingHours hours = new OperatingHours();
            hours.setRestaurant(restaurant);
            hours.setDayOfWeek(day);
            hours.setOpenTime(openTime);
            hours.setCloseTime(closeTime);
            hours.setClosed(day == DayOfWeek.SUNDAY); // Example: Closed on Sundays by default
            
            hoursToSave.add(hours);
        }
        
        if (!hoursToSave.isEmpty()) {
            operatingHoursRepository.saveAll(hoursToSave);
        }
    }

    @Transactional
    public OperatingHoursDTO updateOperatingHours(String restaurantId, DayOfWeek day, 
                                               OperatingHoursUpdateRequest updateRequest) {
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));
        
        OperatingHours hours = operatingHoursRepository.findByRestaurantIdAndDayOfWeek(restaurantId, day)
                .orElseGet(() -> {
                    OperatingHours newHours = new OperatingHours();
                    newHours.setRestaurant(restaurant);
                    newHours.setDayOfWeek(day);
                    return newHours;
                });
        
        // Validation
        if (updateRequest.getOpenTime() != null && updateRequest.getCloseTime() != null) {
            if (updateRequest.getOpenTime().isAfter(updateRequest.getCloseTime())) {
                throw new ValidationException("openTime", 
                        "Open time must be before close time");
            }
        }
        
        LocalTime oldOpenTime = hours.getOpenTime();
        LocalTime oldCloseTime = hours.getCloseTime();
        boolean oldClosed = hours.isClosed();
        
        if (updateRequest.getOpenTime() != null) {
            hours.setOpenTime(updateRequest.getOpenTime());
        }
        
        if (updateRequest.getCloseTime() != null) {
            hours.setCloseTime(updateRequest.getCloseTime());
        }
        
        hours.setClosed(updateRequest.isClosed());
        
        if (updateRequest.getBreakStartTime() != null) {
            hours.setBreakStartTime(updateRequest.getBreakStartTime());
        }
        
        if (updateRequest.getBreakEndTime() != null) {
            hours.setBreakEndTime(updateRequest.getBreakEndTime());
        }
        
        if (updateRequest.getSpecialHoursDescription() != null) {
            hours.setSpecialHoursDescription(updateRequest.getSpecialHoursDescription());
        }
        
        OperatingHours updatedHours = operatingHoursRepository.save(hours);
        
        // Publish operating hours changed event
        if (!oldClosed && updatedHours.isClosed()) {
            // If changing from open to closed
            restaurantEventProducer.publishOperatingHoursChangedEvent(
                    new OperatingHoursChangedEvent(
                            restaurantId,
                            day,
                            oldOpenTime,
                            oldCloseTime,
                            null, // When closed, no open/close times
                            null
                    ));
        } else if (oldClosed && !updatedHours.isClosed()) {
            // If changing from closed to open
            restaurantEventProducer.publishOperatingHoursChangedEvent(
                    new OperatingHoursChangedEvent(
                            restaurantId,
                            day,
                            null, // When was closed, no previous times
                            null,
                            updatedHours.getOpenTime(),
                            updatedHours.getCloseTime()
                    ));
        } else if (!oldClosed && !updatedHours.isClosed() && 
                  (oldOpenTime != updatedHours.getOpenTime() || 
                   oldCloseTime != updatedHours.getCloseTime())) {
            // If changing times when open
            restaurantEventProducer.publishOperatingHoursChangedEvent(
                    new OperatingHoursChangedEvent(
                            restaurantId,
                            day,
                            oldOpenTime,
                            oldCloseTime,
                            updatedHours.getOpenTime(),
                            updatedHours.getCloseTime()
                    ));
        }
        
        return convertToDTO(updatedHours);
    }

    @Transactional
    public List<OperatingHoursDTO> updateAllOperatingHours(String restaurantId, OperatingHoursBatchUpdateRequest updateRequest) {
        // Create a set of days that are included in the update
        Set<DayOfWeek> includedDays = updateRequest.getOperatingHours().stream()
                .map(OperatingHourEntry::getDayOfWeek)
                .collect(Collectors.toSet());
        
        // Update each day in the request
        for (OperatingHourEntry dayEntry : updateRequest.getOperatingHours()) {
            if (dayEntry.getDayOfWeek() == null) {
                throw new ValidationException("dayOfWeek", "Day of week is required");
            }
            
            // Create an OperatingHoursUpdateRequest from the OperatingHourEntry
            OperatingHoursUpdateRequest updateRequestForDay = new OperatingHoursUpdateRequest();
            updateRequestForDay.setOpenTime(dayEntry.getOpenTime());
            updateRequestForDay.setCloseTime(dayEntry.getCloseTime());
            updateRequestForDay.setClosed(false);  // If a day is included, it's not closed
            
            // Use the existing method to update this day's hours
            updateOperatingHours(restaurantId, dayEntry.getDayOfWeek(), updateRequestForDay);
        }
        
        // For days not in the request, mark them as closed
        for (DayOfWeek day : DayOfWeek.values()) {
            if (!includedDays.contains(day)) {
                OperatingHoursUpdateRequest closeRequest = new OperatingHoursUpdateRequest();
                closeRequest.setClosed(true);
                
                updateOperatingHours(restaurantId, day, closeRequest);
            }
        }
        
        // Return all operating hours for the restaurant
        return getOperatingHoursByRestaurantId(restaurantId);
    }

    public OperatingHoursDTO getOperatingHoursByDay(String restaurantId, DayOfWeek day) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));
                
        OperatingHours hours = operatingHoursRepository.findByRestaurantIdAndDayOfWeek(restaurantId, day)
                .orElseThrow(() -> new EntityNotFoundException("Operating Hours", 
                        "Restaurant: " + restaurantId + ", Day: " + day));
                        
        return convertToDTO(hours);
    }

    private OperatingHoursDTO convertToDTO(OperatingHours hours) {
        OperatingHoursDTO dto = new OperatingHoursDTO();
        dto.setId(hours.getId());
        dto.setRestaurantId(hours.getRestaurant().getId());
        dto.setDayOfWeek(hours.getDayOfWeek());
        dto.setOpenTime(hours.getOpenTime());
        dto.setCloseTime(hours.getCloseTime());
        dto.setClosed(hours.isClosed());
        dto.setBreakStartTime(hours.getBreakStartTime());
        dto.setBreakEndTime(hours.getBreakEndTime());
        dto.setSpecialHoursDescription(hours.getSpecialHoursDescription());
        return dto;
    }
}