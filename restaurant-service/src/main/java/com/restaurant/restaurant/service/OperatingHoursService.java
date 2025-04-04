package com.restaurant.restaurant.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(OperatingHoursService.class);

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
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));

        if (!restaurant.isActive()) {
            throw new ValidationException("ไม่สามารถอัปเดตร้านอาหารที่ถูกลบไปแล้ว");
        }

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

        if (!restaurant.isActive()) {
            throw new ValidationException("restaurant",
                    "Cannot update operating hours for an inactive restaurant. Please activate the restaurant first.");
        }

        OperatingHours hours = operatingHoursRepository.findByRestaurantIdAndDayOfWeek(restaurantId, day)
                .orElseGet(() -> {
                    OperatingHours newHours = new OperatingHours();
                    newHours.setRestaurant(restaurant);
                    newHours.setDayOfWeek(day);
                    return newHours;
                });

        // Validation
        validateOperatingHoursUpdate(updateRequest, day);

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

        if (updateRequest.getBreakStartTime() != null && updateRequest.getBreakEndTime() != null) {
            // Validate break times
            if (updateRequest.getBreakStartTime().isAfter(updateRequest.getBreakEndTime())) {
                throw new ValidationException("breakTime",
                        "Break start time must be before break end time");
            }

            // Validate that break is within opening hours
            if (hours.getOpenTime() != null && hours.getCloseTime() != null) {
                if (updateRequest.getBreakStartTime().isBefore(hours.getOpenTime()) ||
                        updateRequest.getBreakEndTime().isAfter(hours.getCloseTime())) {
                    throw new ValidationException("breakTime",
                            "Break time must be within the restaurant's operating hours");
                }
            }

            hours.setBreakStartTime(updateRequest.getBreakStartTime());
            hours.setBreakEndTime(updateRequest.getBreakEndTime());
        }

        if (updateRequest.getSpecialHoursDescription() != null) {
            hours.setSpecialHoursDescription(updateRequest.getSpecialHoursDescription());
        }

        OperatingHours updatedHours = operatingHoursRepository.save(hours);

        // Publish operating hours changed event
        try {
            if (!oldClosed && updatedHours.isClosed()) {
                // If changing from open to closed
                restaurantEventProducer.publishOperatingHoursChangedEvent(
                        new OperatingHoursChangedEvent(
                                restaurantId,
                                day,
                                oldOpenTime,
                                oldCloseTime,
                                null, // When closed, no open/close times
                                null));
            } else if (oldClosed && !updatedHours.isClosed()) {
                // If changing from closed to open
                restaurantEventProducer.publishOperatingHoursChangedEvent(
                        new OperatingHoursChangedEvent(
                                restaurantId,
                                day,
                                null, // When was closed, no previous times
                                null,
                                updatedHours.getOpenTime(),
                                updatedHours.getCloseTime()));
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
                                updatedHours.getCloseTime()));
            }
        } catch (Exception e) {
            logger.error("Failed to publish operating hours changed event: {}", e.getMessage(), e);
            // Continue with the update even if event publishing fails
        }

        return convertToDTO(updatedHours);
    }

    private void validateOperatingHoursUpdate(OperatingHoursUpdateRequest updateRequest, DayOfWeek day) {
        Map<String, String> errors = new HashMap<>();

        if (updateRequest.getOpenTime() != null && updateRequest.getCloseTime() != null) {
            if (updateRequest.getOpenTime().isAfter(updateRequest.getCloseTime())) {
                errors.put("operatingHours",
                        "Opening time must be before closing time for " + day.toString());
            }

            if (updateRequest.getOpenTime().equals(updateRequest.getCloseTime())) {
                errors.put("operatingHours",
                        "Opening and closing times cannot be the same for " + day.toString());
            }

            if (ChronoUnit.MINUTES.between(updateRequest.getOpenTime(), updateRequest.getCloseTime()) < 30) {
                errors.put("operatingHours",
                        "Operating period must be at least 30 minutes long for " + day.toString());
            }
        }

        // Break time validation
        if (updateRequest.getBreakStartTime() != null && updateRequest.getBreakEndTime() == null) {
            errors.put("breakEndTime", "Break end time must be provided when break start time is set");
        }

        if (updateRequest.getBreakEndTime() != null && updateRequest.getBreakStartTime() == null) {
            errors.put("breakStartTime", "Break start time must be provided when break end time is set");
        }

        if (updateRequest.getBreakStartTime() != null && updateRequest.getBreakEndTime() != null) {
            if (updateRequest.getBreakStartTime().isAfter(updateRequest.getBreakEndTime())) {
                errors.put("breakTime", "Break start time must be before break end time");
            }

            if (ChronoUnit.MINUTES.between(updateRequest.getBreakStartTime(), updateRequest.getBreakEndTime()) < 15) {
                errors.put("breakTime", "Break period must be at least 15 minutes long");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Operating hours validation failed", errors);
        }
    }

    @Transactional
    public List<OperatingHoursDTO> updateAllOperatingHours(String restaurantId,
            OperatingHoursBatchUpdateRequest updateRequest) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));

        if (!restaurant.isActive()) {
            throw new ValidationException("restaurant",
                    "Cannot update operating hours for an inactive restaurant. Please activate the restaurant first.");
        }

        // Validate the batch update request
        validateBatchUpdateRequest(updateRequest);

        // Create a set of days that are included in the update
        Set<DayOfWeek> includedDays = updateRequest.getOperatingHours().stream()
                .map(OperatingHourEntry::getDayOfWeek)
                .collect(Collectors.toSet());

        // Update each day in the request
        for (OperatingHourEntry dayEntry : updateRequest.getOperatingHours()) {
            // Create an OperatingHoursUpdateRequest from the OperatingHourEntry
            OperatingHoursUpdateRequest updateRequestForDay = new OperatingHoursUpdateRequest();
            updateRequestForDay.setOpenTime(dayEntry.getOpenTime());
            updateRequestForDay.setCloseTime(dayEntry.getCloseTime());
            updateRequestForDay.setClosed(false); // If a day is included, it's not closed

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

    private void validateBatchUpdateRequest(OperatingHoursBatchUpdateRequest updateRequest) {
        if (updateRequest.getOperatingHours() == null || updateRequest.getOperatingHours().isEmpty()) {
            throw new ValidationException("operatingHours",
                    "At least one day's operating hours must be provided");
        }

        // Check for duplicate days
        Set<DayOfWeek> days = new HashSet<>();
        for (OperatingHourEntry entry : updateRequest.getOperatingHours()) {
            if (entry.getDayOfWeek() == null) {
                throw new ValidationException("dayOfWeek", "Day of week is required for each entry");
            }

            if (!days.add(entry.getDayOfWeek())) {
                throw new ValidationException("operatingHours",
                        "Duplicate day of week: " + entry.getDayOfWeek() + ". Each day can only appear once");
            }

            if (entry.getOpenTime() == null) {
                throw new ValidationException("openTime",
                        "Opening time is required for " + entry.getDayOfWeek());
            }

            if (entry.getCloseTime() == null) {
                throw new ValidationException("closeTime",
                        "Closing time is required for " + entry.getDayOfWeek());
            }

            if (entry.getOpenTime().isAfter(entry.getCloseTime())) {
                throw new ValidationException("operatingHours",
                        "Opening time must be before closing time for " + entry.getDayOfWeek());
            }
        }
    }

    public OperatingHoursDTO getOperatingHoursByDay(String restaurantId, DayOfWeek day) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));

        if (!restaurant.isActive()) {
            throw new ValidationException("ไม่สามารถดูข้อมูลเวลาทำการเนื่องจากร้านอาหารนี้ไม่ได้ให้บริการแล้ว");
        }

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