package com.restaurant.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
import com.restaurant.reservation.domain.repositories.ReservationQuotaRepository;
import com.restaurant.reservation.domain.repositories.ReservationRepository;
import com.restaurant.reservation.dto.ReservationCreateRequest;
import com.restaurant.reservation.dto.ReservationDTO;
import com.restaurant.reservation.dto.ReservationUpdateRequest;
import com.restaurant.reservation.exception.RestaurantCapacityException;
import com.restaurant.reservation.kafka.producers.ReservationEventProducer;

import jakarta.transaction.Transactional;

/**
 * Service class responsible for managing restaurant reservations.
 * Handles all reservation-related operations including creation, modification,
 * confirmation, and cancellation of reservations.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Service
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    /** Repository for managing reservation data */
    private final ReservationRepository reservationRepository;
    
    /** Repository for managing reservation quota data */
    private final ReservationQuotaRepository quotaRepository;
    
    /** Service for managing table availability */
    private final TableAvailabilityService tableAvailabilityService;
    
    /** Producer for publishing reservation-related events */
    private final ReservationEventProducer eventProducer;
    
    /** Service for validating restaurant-related operations */
    private final RestaurantValidationService restaurantValidationService;

    /** Time in minutes before a reservation expires if not confirmed */
    @Value("${reservation.confirmation-expiration-minutes:15}")
    private int confirmationExpirationMinutes;

    /** Default duration of a reservation session in minutes */
    @Value("${reservation.default-session-length-minutes:120}")
    private int defaultSessionLengthMinutes;

    /** Minimum time in minutes required for advance booking */
    @Value("${reservation.min-advance-booking-minutes:60}")
    private int minAdvanceBookingMinutes;

    /** Maximum allowed party size for a reservation */
    @Value("${reservation.max-party-size:20}")
    private int maxPartySize;

    /** Maximum number of days in advance a reservation can be made */
    @Value("${reservation.max-future-days:90}")
    private int maxFutureDays;

    /**
     * Constructs a new ReservationService with required dependencies.
     *
     * @param reservationRepository Repository for reservation data
     * @param quotaRepository Repository for reservation quota data
     * @param tableAvailabilityService Service for managing table availability
     * @param eventProducer Producer for reservation events
     * @param restaurantValidationService Service for restaurant validation
     */
    public ReservationService(ReservationRepository reservationRepository,
            ReservationQuotaRepository quotaRepository,
            TableAvailabilityService tableAvailabilityService,
            ReservationEventProducer eventProducer,
            RestaurantValidationService restaurantValidationService) {
        this.reservationRepository = reservationRepository;
        this.quotaRepository = quotaRepository;
        this.tableAvailabilityService = tableAvailabilityService;
        this.eventProducer = eventProducer;
        this.restaurantValidationService = restaurantValidationService;
    }

    /**
     * Retrieves all reservations for a specific user with pagination.
     *
     * @param userId The ID of the user whose reservations to retrieve
     * @param pageable Pagination information
     * @return Page of ReservationDTO objects
     */
    public Page<ReservationDTO> getReservationsByUserId(String userId, Pageable pageable) {
        return reservationRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Retrieves all reservations for a specific restaurant with pagination.
     *
     * @param restaurantId The ID of the restaurant whose reservations to retrieve
     * @param pageable Pagination information
     * @return Page of ReservationDTO objects
     */
    public Page<ReservationDTO> getReservationsByRestaurantId(String restaurantId, Pageable pageable) {
        return reservationRepository.findByRestaurantId(restaurantId, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Retrieves a specific reservation by its ID.
     *
     * @param id The ID of the reservation to retrieve
     * @return ReservationDTO object
     * @throws EntityNotFoundException if the reservation is not found
     */
    public ReservationDTO getReservationById(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation", id));
        return convertToDTO(reservation);
    }

    /**
     * Creates a new reservation with the provided details.
     * Validates the request, checks availability, and assigns a table if possible.
     *
     * @param createRequest The reservation creation request containing all necessary details
     * @param userId The ID of the user creating the reservation
     * @return Created ReservationDTO object
     * @throws ValidationException if the request is invalid
     * @throws RestaurantCapacityException if no suitable table is available
     */
    @Transactional
    public ReservationDTO createReservation(ReservationCreateRequest createRequest, String userId) {
        validateReservationRequest(createRequest);

        restaurantValidationService.validateRestaurantExists(createRequest.getRestaurantId());

        restaurantValidationService.validateOperatingHours(createRequest.getRestaurantId(),
                createRequest.getReservationTime());

        // Check restaurant availability for the given time
        if (!isTimeSlotAvailable(createRequest.getRestaurantId(),
                createRequest.getReservationTime(),
                createRequest.getPartySize())) {
            throw new ValidationException("reservationTime", "The selected time is not available");
        }

        // Set duration to default if not specified
        int duration = createRequest.getDurationMinutes() > 0 ? createRequest.getDurationMinutes()
                : defaultSessionLengthMinutes;

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
        reservation = reservationRepository.save(reservation);

        // Try to find and assign a table
        tableAvailabilityService.findAndAssignTable(reservation);

        // Reload the reservation to get the latest state
        reservation = reservationRepository.findById(reservation.getId()).orElse(reservation);

        // If no table was assigned, throw an exception
        if (reservation.getTableId() == null) {
            throw RestaurantCapacityException.noSuitableTables(createRequest.getPartySize());
        }

        // Create history record
        ReservationHistory history = new ReservationHistory(
                reservation, "CREATED", "Reservation created", userId);
        reservation.addHistoryRecord(history);

        // Save final state
        reservation = reservationRepository.save(reservation);

        // Update reservation quota
        updateReservationQuota(reservation, true);

        // Publish event
        eventProducer.publishReservationCreatedEvent(new ReservationCreatedEvent(
                reservation.getId(),
                reservation.getRestaurantId(),
                reservation.getUserId(),
                reservation.getReservationTime().toString(),
                reservation.getPartySize(),
                reservation.getTableId()));

        return convertToDTO(reservation);
    }

    /**
     * Confirms a pending reservation.
     * Validates the reservation status and confirmation deadline before proceeding.
     *
     * @param id The ID of the reservation to confirm
     * @param userId The ID of the user confirming the reservation
     * @return Updated ReservationDTO object
     * @throws EntityNotFoundException if the reservation is not found
     * @throws ValidationException if the reservation cannot be confirmed
     */
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

        // หากยังไม่มีการกำหนดโต๊ะให้กับการจอง ให้ค้นหาและกำหนดโต๊ะใหม่
        if (updatedReservation.getTableId() == null) {
            tableAvailabilityService.findAndAssignTable(updatedReservation);

            // โหลดการจองอีกครั้งเพื่อให้แน่ใจว่ามีข้อมูลล่าสุด
            updatedReservation = reservationRepository.findById(id).orElse(updatedReservation);
        }

        // Publish event
        eventProducer.publishReservationConfirmedEvent(new ReservationConfirmedEvent(
                updatedReservation.getId(),
                updatedReservation.getRestaurantId(),
                updatedReservation.getUserId(),
                updatedReservation.getTableId()));

        return convertToDTO(updatedReservation);
    }

    /**
     * Cancels an existing reservation.
     * Updates the reservation status and releases any assigned table.
     *
     * @param id The ID of the reservation to cancel
     * @param reason The reason for cancellation
     * @param userId The ID of the user cancelling the reservation
     * @return Updated ReservationDTO object
     * @throws EntityNotFoundException if the reservation is not found
     * @throws ValidationException if the reservation cannot be cancelled
     */
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
                reason));

        return convertToDTO(updatedReservation);
    }

    /**
     * Updates an existing reservation with new details.
     * Validates the changes and updates the reservation accordingly.
     *
     * @param id The ID of the reservation to update
     * @param updateRequest The update request containing new details
     * @param userId The ID of the user updating the reservation
     * @return Updated ReservationDTO object
     * @throws EntityNotFoundException if the reservation is not found
     * @throws ValidationException if the update request is invalid
     */
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
                partySizeChanged ? updatedReservation.getPartySize() : 0));

        return convertToDTO(updatedReservation);
    }

    /**
     * Processes expired reservations that have not been confirmed.
     * Automatically cancels reservations that have passed their confirmation deadline.
     */
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
                    "Confirmation deadline expired"));
        }

        // Mark past reservations as completed or no-show
        LocalDateTime pastTime = now.minusHours(1); // Assume 1 hour past the reservation time
        List<Reservation> uncompletedPast = reservationRepository.findUncompletedPastReservations(pastTime);

        for (Reservation reservation : uncompletedPast) {
            if (reservation.getStatus().equals(StatusCodes.RESERVATION_CONFIRMED)) {
                // Mark as completed (could alternatively mark as no-show based on business
                // rules)
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

    /**
     * Checks if a time slot is available for a given restaurant and party size.
     *
     * @param restaurantId The ID of the restaurant
     * @param reservationTime The requested reservation time
     * @param partySize The size of the party
     * @return true if the time slot is available, false otherwise
     */
    private boolean isTimeSlotAvailable(String restaurantId, LocalDateTime reservationTime, int partySize) {
        LocalDate date = reservationTime.toLocalDate();
        LocalTime time = reservationTime.toLocalTime();

        // Get reservation quota for this time slot
        ReservationQuota quota = quotaRepository
                .findByRestaurantIdAndDateAndTimeSlot(restaurantId, date, time)
                .orElse(null);

        // If no quota exists, assume available (will be created when reservation is
        // made)
        if (quota == null) {
            return true;
        }

        // Check availability and throw specific exceptions for better error messages
        if (!quota.hasAvailability()) {
            String formattedDate = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
            String formattedTime = time.format(DateTimeFormatter.ofPattern("h:mm a"));
            throw RestaurantCapacityException.noAvailability(formattedDate, formattedTime);
        }

        if (!quota.canAccommodateParty(partySize)) {
            throw RestaurantCapacityException.noSuitableTables(partySize);
        }

        return true;
    }

    /**
     * Updates the reservation quota for a specific reservation.
     *
     * @param reservation The reservation to update quota for
     * @param isAdd true to add to quota, false to subtract
     */
    private void updateReservationQuota(Reservation reservation, boolean isAdd) {
        updateReservationQuotaForTime(
                reservation.getRestaurantId(),
                reservation.getReservationTime().toLocalDate(),
                reservation.getReservationTime().toLocalTime(),
                reservation.getPartySize(),
                isAdd);
    }

    /**
     * Updates the reservation quota for a specific time slot.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date of the reservation
     * @param time The time of the reservation
     * @param partySize The size of the party
     * @param isAdd true to add to quota, false to subtract
     */
    private void updateReservationQuotaForTime(String restaurantId, LocalDate date,
            LocalTime time, int partySize, boolean isAdd) {
        ReservationQuota quota = quotaRepository
                .findByRestaurantIdAndDateAndTimeSlot(restaurantId, date, time)
                .orElse(new ReservationQuota(restaurantId, date, time, 10, 100));

        if (isAdd) {
            quota.setCurrentReservations(quota.getCurrentReservations() + 1);
            quota.setCurrentCapacity(quota.getCurrentCapacity() + partySize);
        } else {
            quota.setCurrentReservations(Math.max(0, quota.getCurrentReservations() - 1));
            quota.setCurrentCapacity(Math.max(0, quota.getCurrentCapacity() - partySize));
        }

        quotaRepository.save(quota);
    }

    /**
     * Validates a reservation creation request.
     * Checks all required fields and business rules.
     *
     * @param request The reservation creation request to validate
     * @throws ValidationException if the request is invalid
     */
    private void validateReservationRequest(ReservationCreateRequest request) {
        if (request.getRestaurantId() == null || request.getRestaurantId().isEmpty()) {
            throw new ValidationException("restaurantId", "Restaurant ID is required to create a reservation");
        }

        if (request.getReservationTime() == null) {
            throw new ValidationException("reservationTime", "Reservation date and time are required");
        }

        validateReservationTime(request.getReservationTime());
        validatePartySize(request.getPartySize());

        if (request.getCustomerName() == null || request.getCustomerName().isEmpty()) {
            throw new ValidationException("customerName", "Customer name is required for the reservation");
        }

        // Check phone or email is provided
        if ((request.getCustomerPhone() == null || request.getCustomerPhone().isEmpty()) &&
                (request.getCustomerEmail() == null || request.getCustomerEmail().isEmpty())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("customerPhone", "Either phone number or email is required");
            errors.put("customerEmail", "Either phone number or email is required");
            throw new ValidationException("Contact information required", errors);
        }

        // Validate email format if provided
        if (request.getCustomerEmail() != null && !request.getCustomerEmail().isEmpty()) {
            String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
            Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
            if (!pattern.matcher(request.getCustomerEmail()).matches()) {
                throw new ValidationException("customerEmail", "Please provide a valid email address");
            }
        }

        // Validate phone format if provided
        if (request.getCustomerPhone() != null && !request.getCustomerPhone().isEmpty()) {
            String phoneRegex = "^\\+?[0-9]{10,15}$";
            if (!request.getCustomerPhone().matches(phoneRegex)) {
                throw new ValidationException("customerPhone", "Please provide a valid phone number");
            }
        }
    }

    /**
     * Validates a reservation time against business rules.
     *
     * @param reservationTime The time to validate
     * @throws ValidationException if the time is invalid
     */
    private void validateReservationTime(LocalDateTime reservationTime) {
        // Check if reservation time is in the future
        LocalDateTime minTime = LocalDateTime.now().plusMinutes(minAdvanceBookingMinutes);
        if (reservationTime.isBefore(minTime)) {
            throw new ValidationException("reservationTime",
                    String.format("Reservations must be made at least %d minutes in advance. " +
                            "The earliest available time is %s.",
                            minAdvanceBookingMinutes,
                            minTime.format(DateTimeFormatter.ofPattern("h:mm a 'on' MMMM d, yyyy"))));
        }

        // Check if reservation is within acceptable future window (e.g., not more than
        // 3 months ahead)
        LocalDateTime maxFutureTime = LocalDateTime.now().plusDays(maxFutureDays);
        if (reservationTime.isAfter(maxFutureTime)) {
            throw new ValidationException("reservationTime",
                    String.format("Reservations cannot be made more than %d days in advance. " +
                            "The latest date available for reservations is %s.",
                            maxFutureDays,
                            maxFutureTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))));
        }

        // Check if reservation is during operating hours (simplified example)
        // LocalTime time = reservationTime.toLocalTime();
        // if (time.isBefore(LocalTime.of(10, 0)) || time.isAfter(LocalTime.of(22, 0))) {
        //     throw new ValidationException("reservationTime",
        //             "Reservations are only accepted between 10:00 AM and 10:00 PM");
        // }
    }

    /**
     * Validates a party size against business rules.
     *
     * @param partySize The party size to validate
     * @throws ValidationException if the party size is invalid
     */
    private void validatePartySize(int partySize) {
        if (partySize <= 0) {
            throw new ValidationException("partySize", "Party size must be at least 1 person");
        }

        if (partySize > maxPartySize) {
            throw RestaurantCapacityException.partyTooLarge(partySize, maxPartySize);
        }

        // If it's a large party (e.g., >8), provide additional information
        if (partySize > 8) {
            logger.info("Large party reservation requested: {} people", partySize);
        }
    }

    /**
     * Converts a Reservation entity to a ReservationDTO.
     *
     * @param reservation The reservation entity to convert
     * @return Converted ReservationDTO object
     */
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