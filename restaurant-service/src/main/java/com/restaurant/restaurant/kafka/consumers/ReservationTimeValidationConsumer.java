package com.restaurant.restaurant.kafka.consumers;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.ErrorCodes;
import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent;
import com.restaurant.common.events.restaurant.ReservationTimeValidationResponseEvent;
import com.restaurant.restaurant.domain.models.OperatingHours;
import com.restaurant.restaurant.domain.models.Restaurant;
import com.restaurant.restaurant.domain.repositories.OperatingHoursRepository;
import com.restaurant.restaurant.domain.repositories.RestaurantRepository;
import com.restaurant.restaurant.kafka.producers.RestaurantEventProducer;

@Component
public class ReservationTimeValidationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ReservationTimeValidationConsumer.class);
    
    private final RestaurantRepository restaurantRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final RestaurantEventProducer restaurantEventProducer;
    
    public ReservationTimeValidationConsumer(
            RestaurantRepository restaurantRepository,
            OperatingHoursRepository operatingHoursRepository,
            RestaurantEventProducer restaurantEventProducer) {
        this.restaurantRepository = restaurantRepository;
        this.operatingHoursRepository = operatingHoursRepository;
        this.restaurantEventProducer = restaurantEventProducer;
    }
    
    @KafkaListener(
            topics = KafkaTopics.RESERVATION_TIME_VALIDATION_REQUEST,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "restaurantKafkaListenerContainerFactory"
    )
    public void consumeReservationTimeValidationRequest(ReservationTimeValidationRequestEvent event) {
        logger.info("Received reservation time validation request: correlationId={}, restaurantId={}, time={}", 
                event.getCorrelationId(), event.getRestaurantId(), event.getReservationTime());
        
        try {
            // Parse the reservation time
            LocalDateTime reservationTime = LocalDateTime.parse(event.getReservationTime());
            
            // Check if restaurant exists and is active
            Restaurant restaurant = restaurantRepository.findById(event.getRestaurantId()).orElse(null);
            if (restaurant == null) {
                sendInvalidResponse(event, "Restaurant not found");
                return;
            }
            
            if (!restaurant.isActive()) {
                sendInvalidResponse(event, "Restaurant is not active", ErrorCodes.RESTAURANT_NOT_ACTIVE);
                return;
            }
            
            // Get day of week from reservation time
            DayOfWeek dayOfWeek = reservationTime.getDayOfWeek();
            
            // Get operating hours for that day
            OperatingHours hours = operatingHoursRepository
                    .findByRestaurantIdAndDayOfWeek(event.getRestaurantId(), dayOfWeek)
                    .orElse(null);
            
            if (hours == null) {
                sendInvalidResponse(event, "Operating hours not found for the requested day");
                return;
            }
            
            if (hours.isClosed()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");
                String dayName = dayOfWeek.toString();
                sendInvalidResponse(event, 
                        "Restaurant is closed on " + dayName, 
                        ErrorCodes.OUTSIDE_OPERATING_HOURS);
                return;
            }
            
            // Get reservation time as LocalTime
            LocalTime time = reservationTime.toLocalTime();
            
            // Check if time is within operating hours
            boolean isWithinHours = hours.isOpenAt(time);
            
            if (!isWithinHours) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
                String openTimeStr = hours.getOpenTime().format(formatter);
                String closeTimeStr = hours.getCloseTime().format(formatter);
                
                sendInvalidResponse(event, 
                        "Reservation time must be between " + openTimeStr + " and " + closeTimeStr, 
                        ErrorCodes.OUTSIDE_OPERATING_HOURS);
                return;
            }
            
            // Check for break time if applicable
            if (hours.getBreakStartTime() != null && hours.getBreakEndTime() != null) {
                if (time.isAfter(hours.getBreakStartTime()) && time.isBefore(hours.getBreakEndTime())) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
                    String breakStartStr = hours.getBreakStartTime().format(formatter);
                    String breakEndStr = hours.getBreakEndTime().format(formatter);
                    
                    sendInvalidResponse(event, 
                            "Restaurant is on break between " + breakStartStr + " and " + breakEndStr, 
                            ErrorCodes.OUTSIDE_OPERATING_HOURS);
                    return;
                }
            }
            
            // If all checks pass, send valid response
            sendValidResponse(event);
            
        } catch (Exception e) {
            logger.error("Error processing reservation time validation request: {}", e.getMessage(), e);
            sendInvalidResponse(event, "Error processing validation request: " + e.getMessage());
        }
    }
    
    private void sendValidResponse(ReservationTimeValidationRequestEvent request) {
        ReservationTimeValidationResponseEvent response = new ReservationTimeValidationResponseEvent(
                request.getRestaurantId(),
                request.getCorrelationId(),
                true // valid
        );
        
        restaurantEventProducer.publishReservationTimeValidationResponse(response);
        logger.info("Sent valid time validation response: correlationId={}", request.getCorrelationId());
    }
    
    private void sendInvalidResponse(ReservationTimeValidationRequestEvent request, String errorMessage) {
        sendInvalidResponse(request, errorMessage, ErrorCodes.INVALID_RESERVATION_TIME);
    }
    
    private void sendInvalidResponse(ReservationTimeValidationRequestEvent request, String errorMessage, String errorCode) {
        ReservationTimeValidationResponseEvent response = new ReservationTimeValidationResponseEvent(
                request.getRestaurantId(),
                request.getCorrelationId(),
                false // invalid
        );
        response.setErrorMessage(errorMessage);
        
        restaurantEventProducer.publishReservationTimeValidationResponse(response);
        logger.info("Sent invalid time validation response: correlationId={}, error={}", 
                request.getCorrelationId(), errorMessage);
    }
}