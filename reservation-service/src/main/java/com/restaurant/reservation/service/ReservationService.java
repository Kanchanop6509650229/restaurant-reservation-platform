package com.restaurant.reservation.service;

import com.restaurant.common.constants.StatusCodes;
import com.restaurant.common.events.reservation.ReservationCancelledEvent;
import com.restaurant.common.events.reservation.ReservationConfirmedEvent;
import com.restaurant.common.events.reservation.ReservationCreatedEvent;
import com.restaurant.common.events.reservation.ReservationModifiedEvent;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.reservation.domain.models.Reservation;
import com.restaurant.reservation.domain.models.ReservationHistory;
import com.restaurant.reservation.domain.models.ReservationQuota;
import com.restaurant.reservation.domain.repositories.ReservationRepository;
import com.restaurant.reservation.domain.repositories.ReservationQuotaRepository;
import com.restaurant.reservation.dto.ReservationCreateRequest;
import com.restaurant.reservation.dto.ReservationDTO;
import com.restaurant.reservation.dto.ReservationUpdateRequest;
import com.restaurant.reservation.kafka.producers.ReservationEventProducer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationQuotaRepository quotaRepository;
    private final TableAvailabilityService tableAvailabilityService;
    private final ReservationEventProducer eventProducer;

    @Value("${reservation.confirmation-expiration-minutes:15}")
    private int confirmationExpirationMinutes;

    @Value("${reservation.default-session-length-minutes:120}")
    private int defaultSessionLengthMinutes;

    @Value("${reservation.min-advance-booking-minutes:60}")
    private int minAdvanceBookingMinutes;

    @Value("${reservation.max-party-size:20}")
    private int maxPartySize;

    public ReservationService(ReservationRepository reservationRepository,
                             ReservationQuotaRepository quotaRepository,
                             TableAvailabilityService tableAvailabilityService,
                             ReservationEventProducer eventProducer) {
        this.reservationRepository = reservationRepository;
        this.quotaRepository = quotaRepository;
        this.tableAvailabilityService = tableAvailabilityService;
        this.eventProducer = eventProducer;
    }

    public Page<ReservationDTO> getReservationsByUserId(String userId, Pageable pageable) {
        return reservationRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }

    public Page<ReservationDTO> getReservationsByRestaurantId(String restaurantId, Pageable pageable) {
        return reservationRepository.findByRestaurantId(restaurantId, pageable)
                .map(this::convertToDTO);
    }

    public ReservationDTO getReservationById(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation", id));
        return convertToDTO(reservation);
    }

    @Transactional
    public ReservationDTO createReservation(ReservationCreateRequest createRequest, String userId) {
        validateReservationRequest(createRequest);

        // Check restaurant availability for the given time
        if (!isTimeSlotAvailable(createRequest.getRestaurantId(), 
                                createRequest.getReservationTime(),
                                createRequest.getPartySize())) {
            throw new ValidationException("reservationTime", "The selected time is not available");
        }
        
        // Set duration to default if not specified
        int duration = createRequest.getDurationMinutes() > 0 ? 
                      createRequest.getDurationMinutes() : defaultSessionLengthMinutes;
        
        // Create Reservation
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setRestaurantId(createRequest.getRestaurantId());
        reservation.setReservationTime(createRequest.getReservationTime());
        reservation.setPartySize(createRequest.getPartySize());
        reservation.setDurationMinutes(duration);
        reservation.setCustomerName(createRequest.getCustomerName());
        reservation.setCustomerPhone(createRequest.getCustomerPhone());
        reservation.setCustomerEmail(createRequest.getCustomerEmail());
        reservation.setSpecialRequests(createRequest.getSpecialRequests());
        reservation.setRemindersEnabled(createRequest.isRemindersEnabled());
        reservation.setStatus(StatusCodes.RESERVATION_PENDING);
        
        // Set confirmation deadline
        reservation.setConfirmationDeadline(
                LocalDateTime.now().plusMinutes(confirmationExpirationMinutes));
        
        // Save reservation
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Create history record
        ReservationHistory history = new ReservationHistory(
                savedReservation, "CREATED", "Reservation created", userId);
        savedReservation.addHistoryRecord(history);
        
        reservationRepository.save(savedReservation);
        
        // Update reservation quota
        updateReservationQuota(savedReservation, true);
        
        // Find available table for this reservation
        tableAvailabilityService.findAndAssignTable(savedReservation);
        
        // Publish event
        eventProducer.publishReservationCreatedEvent(new ReservationCreatedEvent(
                savedReservation.getId(),
                savedReservation.getRestaurantId(),
                savedReservation.getUserId(),
                savedReservation.getReservationTime().toString(),
                savedReservation.getPartySize(),
                savedReservation.getTableId()
        ));
        
        return convertToDTO(savedReservation);
    }

    @Transactional
    public ReservationDTO confirmReservation(String id, String userId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation", id));
        
        // Check if reservation is still in PENDING status
        if (!reservation.getStatus().equals(StatusCodes.RESERVATION_PENDING)) {
            throw new ValidationException("status", 
                    "Cannot confirm reservation in " + reservation.getStatus() + " status");
        }
        
        // Check if confirmation deadline has passed
        if (LocalDateTime.now().isAfter(reservation.getConfirmationDeadline())) {
            throw new ValidationException("confirmationDeadline", 
                    "Confirmation deadline has passed");
        }
        
        // Update reservation status
        reservation.setStatus(StatusCodes.RESERVATION_CONFIRMED);
        reservation.setConfirmedAt(LocalDateTime.now());
        
        // Create history record
        ReservationHistory history = new ReservationHistory(
                reservation, "CONFIRMED", "Reservation confirmed", userId);
        reservation.addHistoryRecord(history);
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        
        // Publish event
        eventProducer.publishReservationConfirmedEvent(new ReservationConfirmedEvent(
                updatedReservation.getId(),
                updatedReservation.getRestaurantId(),
                updatedReservation.getUserId(),
                updatedReservation.getTableId()
        ));
        
        return convertToDTO(updatedReservation);
    }

    @Transactional
    public ReservationDTO cancelReservation(String id, String reason, String userId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation", id));
        
        // Check if reservation can be cancelled
        if (reservation.getStatus().equals(StatusCodes.RESERVATION_CANCELLED) ||
            reservation.getStatus().equals(StatusCodes.RESERVATION_COMPLETED) ||
            reservation.getStatus().equals(StatusCodes.RESERVATION_NO_SHOW)) {
            throw new ValidationException("status", 
                    "Cannot cancel reservation in " + reservation.getStatus() + " status");
        }
        
        // Update reservation
        String oldStatus = reservation.getStatus();
        reservation.setStatus(StatusCodes.RESERVATION_CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancellationReason(reason);
        
        // Create history record
        ReservationHistory history = new ReservationHistory(
                reservation, "CANCELLED", "Reservation cancelled: " + reason, userId);
        reservation.addHistoryRecord(history);
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        
        // Update reservation quota
        updateReservationQuota(updatedReservation, false);
        
        // Release assigned table if any
        if (updatedReservation.getTableId() != null) {
            tableAvailabilityService.releaseTable(updatedReservation);
        }
        
        // Publish event
        eventProducer.publishReservationCancelledEvent(new ReservationCancelledEvent(
                updatedReservation.getId(),
                updatedReservation.getRestaurantId(),
                updatedReservation.getUserId(),
                oldStatus,
                reason
        ));
        
        return convertToDTO(updatedReservation);
    }

    @Transactional
    public ReservationDTO updateReservation(String id, ReservationUpdateRequest updateRequest, String userId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation", id));
        
        // Check if reservation can be updated
        if (!reservation.getStatus().equals(StatusCodes.RESERVATION_PENDING) &&
            !reservation.getStatus().equals(StatusCodes.RESERVATION_CONFIRMED)) {
            throw new ValidationException("status", 
                    "Cannot update reservation in " + reservation.getStatus() + " status");
        }
        
        boolean timeChanged = false;
        boolean partySizeChanged = false;
        LocalDateTime oldReservationTime = reservation.getReservationTime();
        int oldPartySize = reservation.getPartySize();
        
        // Check if time is being updated
        if (updateRequest.getReservationTime() != null && 
            !updateRequest.getReservationTime().equals(reservation.getReservationTime())) {
            
            validateReservationTime(updateRequest.getReservationTime());
            
            // Check availability for new time
            if (!isTimeSlotAvailable(reservation.getRestaurantId(), 
                                    updateRequest.getReservationTime(),
                                    reservation.getPartySize())) {
                throw new ValidationException("reservationTime", "The selected time is not available");
            }
            
            // Update reservation time
            reservation.setReservationTime(updateRequest.getReservationTime());
            timeChanged = true;
        }
        
        // Check if party size is being updated
        if (updateRequest.getPartySize() > 0 && 
            updateRequest.getPartySize() != reservation.getPartySize()) {
            
            validatePartySize(updateRequest.getPartySize());
            
            // Check availability for new party size
            if (!isTimeSlotAvailable(reservation.getRestaurantId(), 
                                    reservation.getReservationTime(),
                                    updateRequest.getPartySize())) {
                throw new ValidationException("partySize", 
                        "Cannot accommodate the new party size at the selected time");
            }
            
            // Update party size
            reservation.setPartySize(updateRequest.getPartySize());
            partySizeChanged = true;
        }
        
        // Update other fields if provided
        if (updateRequest.getDurationMinutes() > 0) {
            reservation.setDurationMinutes(updateRequest.getDurationMinutes());
        }
        
        if (updateRequest.getCustomerName() != null) {
            reservation.setCustomerName(updateRequest.getCustomerName());
        }
        
        if (updateRequest.getCustomerPhone() != null) {
            reservation.setCustomerPhone(updateRequest.getCustomerPhone());
        }
        
        if (updateRequest.getCustomerEmail() != null) {
            reservation.setCustomerEmail(updateRequest.getCustomerEmail());
        }
        
        if (updateRequest.getSpecialRequests() != null) {
            reservation.setSpecialRequests(updateRequest.getSpecialRequests());
        }
        
        // Create history record
        StringBuilder details = new StringBuilder("Reservation updated: ");
        if (timeChanged) {
            details.append("Time changed from ")
                   .append(oldReservationTime)
                   .append(" to ")
                   .append(reservation.getReservationTime())
                   .append("; ");
        }
        if (partySizeChanged) {
            details.append("Party size changed from ")
                   .append(oldPartySize)
                   .append(" to ")
                   .append(reservation.getPartySize())
                   .append("; ");
        }
        
        ReservationHistory history = new ReservationHistory(
                reservation, "MODIFIED", details.toString(), userId);
        reservation.addHistoryRecord(history);
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        
        // Update quota if time or party size changed
        if (timeChanged || partySizeChanged) {
            // Remove from old quota
            if (oldReservationTime != null) {
                updateReservationQuotaForTime(
                        reservation.getRestaurantId(), 
                        oldReservationTime.toLocalDate(), 
                        oldReservationTime.toLocalTime(), 
                        oldPartySize, 
                        false);
            }
            
            // Add to new quota
            updateReservationQuota(updatedReservation, true);
            
            // Reassign table if needed
            if (updatedReservation.getTableId() != null) {
                tableAvailabilityService.releaseTable(updatedReservation);
            }
            tableAvailabilityService.findAndAssignTable(updatedReservation);
        }
        
        // Publish event
        eventProducer.publishReservationModifiedEvent(new ReservationModifiedEvent(
                updatedReservation.getId(),
                updatedReservation.getRestaurantId(),
                updatedReservation.getUserId(),
                timeChanged ? oldReservationTime.toString() : null,
                timeChanged ? updatedReservation.getReservationTime().toString() : null,
                partySizeChanged ? oldPartySize : 0,
                partySizeChanged ? updatedReservation.getPartySize() : 0
        ));
        
        return convertToDTO(updatedReservation);
    }

    @Transactional
    public void processExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find expired pending reservations
        List<Reservation> expiredPending = reservationRepository.findExpiredPendingReservations(now);
        
        for (Reservation reservation : expiredPending) {
            // Cancel the reservation
            reservation.setStatus(StatusCodes.RESERVATION_CANCELLED);
            reservation.setCancelledAt(now);
            reservation.setCancellationReason("Confirmation deadline expired");
            
            // Create history record
            ReservationHistory history = new ReservationHistory(
                    reservation, "CANCELLED", "Confirmation deadline expired", "SYSTEM");
            reservation.addHistoryRecord(history);
            
            // Save reservation
            reservationRepository.save(reservation);
            
            // Update quota
            updateReservationQuota(reservation, false);
            
            // Release assigned table if any
            if (reservation.getTableId() != null) {
                tableAvailabilityService.releaseTable(reservation);
            }
            
            // Publish event
            eventProducer.publishReservationCancelledEvent(new ReservationCancelledEvent(
                    reservation.getId(),
                    reservation.getRestaurantId(),
                    reservation.getUserId(),
                    StatusCodes.RESERVATION_PENDING,
                    "Confirmation deadline expired"
            ));
        }
        
        // Mark past reservations as completed or no-show
        LocalDateTime pastTime = now.minusHours(1); // Assume 1 hour past the reservation time
        List<Reservation> uncompletedPast = reservationRepository.findUncompletedPastReservations(pastTime);
        
        for (Reservation reservation : uncompletedPast) {
            if (reservation.getStatus().equals(StatusCodes.RESERVATION_CONFIRMED)) {
                // Mark as completed (could alternatively mark as no-show based on business rules)
                reservation.setStatus(StatusCodes.RESERVATION_COMPLETED);
                reservation.setCompletedAt(now);
                
                // Create history record
                ReservationHistory history = new ReservationHistory(
                        reservation, "COMPLETED", "Reservation marked as completed", "SYSTEM");
                reservation.addHistoryRecord(history);
                
                // Save reservation
                reservationRepository.save(reservation);
                
                // Release table
                if (reservation.getTableId() != null) {
                    tableAvailabilityService.releaseTable(reservation);
                }
            }
        }
    }

    private boolean isTimeSlotAvailable(String restaurantId, LocalDateTime reservationTime, int partySize) {
        LocalDate date = reservationTime.toLocalDate();
        LocalTime time = reservationTime.toLocalTime();
        
        // Get reservation quota for this time slot
        ReservationQuota quota = quotaRepository
                .findByRestaurantIdAndDateAndTimeSlot(restaurantId, date, time)
                .orElse(null);
        
        // If no quota exists, assume available (will be created when reservation is made)
        if (quota == null) {
            return true;
        }
        
        return quota.hasAvailability() && quota.canAccommodateParty(partySize);
    }

    private void updateReservationQuota(Reservation reservation, boolean isAdd) {
        updateReservationQuotaForTime(
                reservation.getRestaurantId(),
                reservation.getReservationTime().toLocalDate(),
                reservation.getReservationTime().toLocalTime(),
                reservation.getPartySize(),
                isAdd);
    }

    private void updateReservationQuotaForTime(String restaurantId, LocalDate date, 
                                              LocalTime time, int partySize, boolean isAdd) {
        ReservationQuota quota = quotaRepository
                .findByRestaurantIdAndDateAndTimeSlot(restaurantId, date, time)
                .orElse(new ReservationQuota(restaurantId, date, time, 10, 100)); // Default values
        
        if (isAdd) {
            quota.setCurrentReservations(quota.getCurrentReservations() + 1);
            quota.setCurrentCapacity(quota.getCurrentCapacity() + partySize);
        } else {
            quota.setCurrentReservations(Math.max(0, quota.getCurrentReservations() - 1));
            quota.setCurrentCapacity(Math.max(0, quota.getCurrentCapacity() - partySize));
        }
        
        quotaRepository.save(quota);
    }

    private void validateReservationRequest(ReservationCreateRequest request) {
        if (request.getRestaurantId() == null || request.getRestaurantId().isEmpty()) {
            throw new ValidationException("restaurantId", "Restaurant ID is required");
        }
        
        if (request.getReservationTime() == null) {
            throw new ValidationException("reservationTime", "Reservation time is required");
        }
        
        validateReservationTime(request.getReservationTime());
        validatePartySize(request.getPartySize());
        
        if (request.getCustomerName() == null || request.getCustomerName().isEmpty()) {
            throw new ValidationException("customerName", "Customer name is required");
        }
    }

    private void validateReservationTime(LocalDateTime reservationTime) {
        // Check if reservation time is in the future
        LocalDateTime minTime = LocalDateTime.now().plusMinutes(minAdvanceBookingMinutes);
        if (reservationTime.isBefore(minTime)) {
            throw new ValidationException("reservationTime", 
                    "Reservation must be at least " + minAdvanceBookingMinutes + " minutes in advance");
        }
    }

    private void validatePartySize(int partySize) {
        if (partySize <= 0) {
            throw new ValidationException("partySize", "Party size must be greater than 0");
        }
        
        if (partySize > maxPartySize) {
            throw new ValidationException("partySize", 
                    "Party size cannot exceed " + maxPartySize + " people");
        }
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUserId());
        dto.setRestaurantId(reservation.getRestaurantId());
        dto.setTableId(reservation.getTableId());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setPartySize(reservation.getPartySize());
        dto.setDurationMinutes(reservation.getDurationMinutes());
        dto.setStatus(reservation.getStatus());
        dto.setCustomerName(reservation.getCustomerName());
        dto.setCustomerPhone(reservation.getCustomerPhone());
        dto.setCustomerEmail(reservation.getCustomerEmail());
        dto.setSpecialRequests(reservation.getSpecialRequests());
        dto.setRemindersEnabled(reservation.isRemindersEnabled());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());
        dto.setConfirmationDeadline(reservation.getConfirmationDeadline());
        
        // Include history if needed
        if (reservation.getHistory() != null && !reservation.getHistory().isEmpty()) {
            dto.setHistoryRecords(reservation.getHistory().stream()
                    .map(h -> new ReservationDTO.HistoryRecord(
                            h.getAction(), h.getTimestamp(), h.getDetails()))
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}