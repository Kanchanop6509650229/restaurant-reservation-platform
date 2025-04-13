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
import com.restaurant.reservation.domain.models.MenuItem;
import com.restaurant.reservation.domain.models.Reservation;
import com.restaurant.reservation.domain.models.ReservationHistory;
import com.restaurant.reservation.domain.models.ReservationMenuItem;
import com.restaurant.reservation.domain.models.ReservationQuota;
import com.restaurant.reservation.domain.repositories.MenuItemRepository;
import com.restaurant.reservation.domain.repositories.ReservationMenuItemRepository;
import com.restaurant.reservation.domain.repositories.ReservationQuotaRepository;
import com.restaurant.reservation.domain.repositories.ReservationRepository;
import com.restaurant.reservation.dto.MenuItemSelectionDTO;
import com.restaurant.reservation.dto.ReservationAddMenuItemsRequest;
import com.restaurant.reservation.dto.ReservationCreateRequest;
import com.restaurant.reservation.dto.ReservationDTO;
import com.restaurant.reservation.dto.ReservationMenuItemDTO;
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

    /** Repository for managing menu items */
    private final MenuItemRepository menuItemRepository;

    /** Repository for managing reservation menu items */
    private final ReservationMenuItemRepository reservationMenuItemRepository;

    /** Service for managing table availability */
    private final TableAvailabilityService tableAvailabilityService;

    /** Producer for publishing reservation-related events */
    private final ReservationEventProducer eventProducer;

    /** Service for validating restaurant-related operations */
    private final RestaurantValidationService restaurantValidationService;

    /** Service for validating restaurant ownership */
    private final RestaurantOwnershipService restaurantOwnershipService;

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
     * @param menuItemRepository Repository for menu items
     * @param reservationMenuItemRepository Repository for reservation menu items
     * @param tableAvailabilityService Service for managing table availability
     * @param eventProducer Producer for reservation events
     * @param restaurantValidationService Service for restaurant validation
     * @param restaurantOwnershipService Service for validating restaurant ownership
     */
    public ReservationService(ReservationRepository reservationRepository,
            ReservationQuotaRepository quotaRepository,
            MenuItemRepository menuItemRepository,
            ReservationMenuItemRepository reservationMenuItemRepository,
            TableAvailabilityService tableAvailabilityService,
            ReservationEventProducer eventProducer,
            RestaurantValidationService restaurantValidationService,
            RestaurantOwnershipService restaurantOwnershipService) {
        this.reservationRepository = reservationRepository;
        this.quotaRepository = quotaRepository;
        this.menuItemRepository = menuItemRepository;
        this.reservationMenuItemRepository = reservationMenuItemRepository;
        this.tableAvailabilityService = tableAvailabilityService;
        this.eventProducer = eventProducer;
        this.restaurantValidationService = restaurantValidationService;
        this.restaurantOwnershipService = restaurantOwnershipService;
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

        // Process menu items if any
        if (createRequest.getMenuItems() != null && !createRequest.getMenuItems().isEmpty()) {
            processMenuItems(reservation, createRequest.getMenuItems());
        }

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

        // Check if the user is the one who created the reservation
        if (!reservation.getUserId().equals(userId)) {
            throw new ValidationException("userId",
                    "Only the user who created the reservation can confirm it");
        }

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
     * Cancels a reservation with the specified reason.
     * Updates the reservation status, creates a history record, and publishes a cancellation event.
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

        // Check if the user is the one who created the reservation or the restaurant owner
        boolean isCreator = reservation.getUserId().equals(userId);
        boolean isRestaurantOwner = restaurantOwnershipService.isUserRestaurantOwner(reservation.getRestaurantId(), userId);

        if (!isCreator && !isRestaurantOwner) {
            throw new ValidationException("userId",
                    "Only the user who created the reservation or the restaurant owner can cancel it");
        }

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
     * Validates the update request and checks availability for the new time slot.
     *
     * @param id The ID of the reservation to update
     * @param updateRequest The update request containing new details
     * @param userId The ID of the user updating the reservation
     * @return Updated ReservationDTO object
     * @throws EntityNotFoundException if the reservation is not found
     * @throws ValidationException if the update request is invalid
     * @throws RestaurantCapacityException if no suitable table is available for the new time
     */
    @Transactional
    public ReservationDTO updateReservation(String id, ReservationUpdateRequest updateRequest, String userId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation", id));

        // Check if the user is the one who created the reservation
        if (!reservation.getUserId().equals(userId)) {
            throw new ValidationException("userId",
                    "Only the user who created the reservation can update it");
        }

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
                (timeChanged && oldReservationTime != null) ? oldReservationTime.toString() : null,
                timeChanged ? updatedReservation.getReservationTime().toString() : null,
                partySizeChanged ? oldPartySize : 0,
                partySizeChanged ? updatedReservation.getPartySize() : 0));

        return convertToDTO(updatedReservation);
    }

    /**
     * Processes reservations that should be expired.
     * This method is typically called by a scheduled task to handle:
     * - Pending reservations that have passed their confirmation deadline
     * - Confirmed reservations that have passed their start time
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
     * Checks if a time slot is available for a reservation.
     * Validates against:
     * - Reservation quotas
     * - Capacity constraints
     *
     * Note: Restaurant operating hours validation is handled separately by RestaurantValidationService
     *
     * @param restaurantId The ID of the restaurant
     * @param reservationTime The desired reservation time
     * @param partySize The size of the party
     * @return true if the time slot is available, false otherwise
     * @throws RestaurantCapacityException if the time slot is not available or cannot accommodate the party
     */
    private boolean isTimeSlotAvailable(String restaurantId, LocalDateTime reservationTime, int partySize) {
        if (restaurantId == null || reservationTime == null || partySize <= 0) {
            throw new IllegalArgumentException("Invalid parameters for checking time slot availability");
        }

        LocalDate date = reservationTime.toLocalDate();
        LocalTime time = reservationTime.toLocalTime();

        // Get reservation quota for this time slot
        ReservationQuota quota = quotaRepository
                .findByRestaurantIdAndDateAndTimeSlot(restaurantId, date, time)
                .orElse(null);

        // If no quota exists, assume available (will be created when reservation is made)
        if (quota == null) {
            logger.debug("No quota found for restaurant {} on {} at {}, assuming available",
                    restaurantId, date, time);
            return true;
        }

        // Check availability and throw specific exceptions for better error messages
        if (!quota.hasAvailability()) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            String formattedDate = date.format(dateFormatter);
            String formattedTime = time.format(timeFormatter);

            logger.debug("No availability for restaurant {} on {} at {}, current capacity: {}/{}",
                    restaurantId, date, time, quota.getCurrentCapacity(), quota.getMaxCapacity());

            throw RestaurantCapacityException.noAvailability(formattedDate, formattedTime);
        }

        if (!quota.canAccommodateParty(partySize)) {
            logger.debug("Cannot accommodate party of {} for restaurant {} on {} at {}, current capacity: {}/{}",
                    partySize, restaurantId, date, time, quota.getCurrentCapacity(), quota.getMaxCapacity());

            throw RestaurantCapacityException.noSuitableTables(partySize);
        }

        return true;
    }

    /**
     * Updates the reservation quota for a specific reservation.
     * This affects the restaurant's capacity tracking for the reservation time.
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
     * Creates or updates the quota record for the given restaurant, date, and time.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date of the reservation
     * @param time The time of the reservation
     * @param partySize The size of the party
     * @param isAdd true to add to quota, false to subtract
     */
    private void updateReservationQuotaForTime(String restaurantId, LocalDate date,
            LocalTime time, int partySize, boolean isAdd) {
        if (restaurantId == null || date == null || time == null || partySize <= 0) {
            logger.warn("Invalid parameters for updating reservation quota");
            return;
        }

        try {
            ReservationQuota quota = quotaRepository
                    .findByRestaurantIdAndDateAndTimeSlot(restaurantId, date, time)
                    .orElse(null);

            if (quota == null) {
                // Create new quota if it doesn't exist
                quota = new ReservationQuota(restaurantId, date, time, 10, 100);

                if (isAdd) {
                    quota.setCurrentReservations(1);
                    quota.setCurrentCapacity(partySize);
                }

                quotaRepository.save(quota);
                logger.debug("Created new quota: restaurant={}, date={}, time={}, party={}, capacity={}/{}",
                        restaurantId, date, time, partySize, quota.getCurrentCapacity(), quota.getMaxCapacity());
            } else {
                // Update existing quota
                if (isAdd) {
                    // Try to increment the quota
                    int result = quotaRepository.incrementCurrentQuota(
                            restaurantId, date, time, 1, partySize);

                    if (result > 0) {
                        logger.debug("Added reservation to quota: restaurant={}, date={}, time={}, party={}",
                                restaurantId, date, time, partySize);
                    } else {
                        logger.warn("Failed to increment quota: restaurant={}, date={}, time={}, party={}",
                                restaurantId, date, time, partySize);
                    }
                } else {
                    // Try to decrement the quota
                    int result = quotaRepository.decrementCurrentQuota(
                            restaurantId, date, time, 1, partySize);

                    if (result > 0) {
                        logger.debug("Removed reservation from quota: restaurant={}, date={}, time={}, party={}",
                                restaurantId, date, time, partySize);
                    } else {
                        logger.warn("Failed to decrement quota: restaurant={}, date={}, time={}, party={}",
                                restaurantId, date, time, partySize);

                        // Fallback to direct update if the decrement fails
                        quota.setCurrentReservations(Math.max(0, quota.getCurrentReservations() - 1));
                        quota.setCurrentCapacity(Math.max(0, quota.getCurrentCapacity() - partySize));
                        quotaRepository.save(quota);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error updating reservation quota: {}", e.getMessage(), e);
            // Don't throw the exception as this is a non-critical operation
            // The reservation can still be created/updated even if quota update fails
        }
    }

    /**
     * Validates a reservation creation request.
     * Checks:
     * - Party size limits
     * - Reservation time constraints
     * - Required fields
     * - Contact information
     *
     * @param request The reservation creation request to validate
     * @throws ValidationException if any validation fails
     */
    private void validateReservationRequest(ReservationCreateRequest request) {
        Map<String, String> errors = new HashMap<>();

        // Validate restaurant ID
        if (request.getRestaurantId() == null || request.getRestaurantId().isEmpty()) {
            errors.put("restaurantId", "Restaurant ID is required to create a reservation");
        }

        // Validate reservation time
        if (request.getReservationTime() == null) {
            errors.put("reservationTime", "Reservation date and time are required");
        } else {
            try {
                validateReservationTime(request.getReservationTime());
            } catch (ValidationException e) {
                errors.put("reservationTime", e.getMessage());
            }
        }

        // Validate party size
        try {
            validatePartySize(request.getPartySize());
        } catch (ValidationException e) {
            errors.put("partySize", e.getMessage());
        }

        // Validate customer name
        if (request.getCustomerName() == null || request.getCustomerName().isEmpty()) {
            errors.put("customerName", "Customer name is required for the reservation");
        } else if (request.getCustomerName().length() < 2) {
            errors.put("customerName", "Customer name must be at least 2 characters");
        }

        // Validate contact information (phone or email is required)
        boolean hasPhone = request.getCustomerPhone() != null && !request.getCustomerPhone().isEmpty();
        boolean hasEmail = request.getCustomerEmail() != null && !request.getCustomerEmail().isEmpty();

        if (!hasPhone && !hasEmail) {
            errors.put("customerPhone", "Either phone number or email is required");
            errors.put("customerEmail", "Either phone number or email is required");
        } else {
            // Validate email format if provided
            if (hasEmail) {
                String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
                Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
                if (!pattern.matcher(request.getCustomerEmail()).matches()) {
                    errors.put("customerEmail", "Please provide a valid email address");
                }
            }

            // Validate phone format if provided
            if (hasPhone) {
                String phoneRegex = "^\\+?[0-9]{10,15}$";
                if (!request.getCustomerPhone().matches(phoneRegex)) {
                    errors.put("customerPhone", "Please provide a valid phone number");
                }
            }
        }

        // If any validation errors were found, throw an exception
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed for reservation request", errors);
        }
    }

    /**
     * Validates a reservation time against business rules.
     * Checks:
     * - Minimum advance booking time
     * - Maximum future booking days
     * - Time is not in the past
     *
     * @param reservationTime The time to validate
     * @throws ValidationException if the time is invalid
     */
    private void validateReservationTime(LocalDateTime reservationTime) {
        if (reservationTime == null) {
            throw new ValidationException("reservationTime", "Reservation time cannot be null");
        }

        // Check if reservation time is in the future with minimum advance notice
        LocalDateTime minTime = LocalDateTime.now().plusMinutes(minAdvanceBookingMinutes);
        if (reservationTime.isBefore(minTime)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a 'on' MMMM d, yyyy");
            throw new ValidationException("reservationTime",
                    String.format("Reservations must be made at least %d minutes in advance. " +
                            "The earliest available time is %s.",
                            minAdvanceBookingMinutes,
                            minTime.format(formatter)));
        }

        // Check if reservation is within acceptable future window
        LocalDateTime maxFutureTime = LocalDateTime.now().plusDays(maxFutureDays);
        if (reservationTime.isAfter(maxFutureTime)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            throw new ValidationException("reservationTime",
                    String.format("Reservations cannot be made more than %d days in advance. " +
                            "The latest date available for reservations is %s.",
                            maxFutureDays,
                            maxFutureTime.format(formatter)));
        }

        // Note: Operating hours validation is handled by RestaurantValidationService
    }

    /**
     * Validates the party size against business rules.
     * Checks if the size is within the allowed range (1 to maxPartySize).
     *
     * @param partySize The party size to validate
     * @throws ValidationException if the party size is invalid
     * @throws RestaurantCapacityException if the party is too large for the restaurant
     */
    private void validatePartySize(int partySize) {
        if (partySize <= 0) {
            throw new ValidationException("partySize", "Party size must be at least 1 person");
        }

        if (partySize > maxPartySize) {
            throw RestaurantCapacityException.partyTooLarge(partySize, maxPartySize);
        }

        // Log large party reservations for monitoring
        if (partySize > 8) {
            logger.info("Large party reservation requested: {} people", partySize);
        }
    }

    /**
     * Adds menu items to an existing reservation.
     * Validates that the reservation is in a valid state for adding menu items.
     *
     * @param id The ID of the reservation to add menu items to
     * @param addMenuItemsRequest The request containing menu items to add
     * @param userId The ID of the user adding the menu items
     * @return Updated ReservationDTO object
     * @throws EntityNotFoundException if the reservation is not found
     * @throws ValidationException if the reservation cannot have menu items added
     */
    @Transactional
    public ReservationDTO addMenuItemsToReservation(String id, ReservationAddMenuItemsRequest addMenuItemsRequest, String userId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation", id));

        // Check if the user is the one who created the reservation
        if (!reservation.getUserId().equals(userId)) {
            throw new ValidationException("userId",
                    "Only the user who created the reservation can add menu items");
        }

        // Check if reservation is in a valid state for adding menu items
        if (!reservation.getStatus().equals(StatusCodes.RESERVATION_PENDING) &&
                !reservation.getStatus().equals(StatusCodes.RESERVATION_CONFIRMED)) {
            throw new ValidationException("status",
                    "Cannot add menu items to reservation in " + reservation.getStatus() + " status");
        }

        // Process menu items
        if (addMenuItemsRequest.getMenuItems() != null && !addMenuItemsRequest.getMenuItems().isEmpty()) {
            processMenuItems(reservation, addMenuItemsRequest.getMenuItems());
        } else {
            throw new ValidationException("menuItems", "At least one menu item must be provided");
        }

        // Create history record
        ReservationHistory history = new ReservationHistory(
                reservation, "MENU_ITEMS_ADDED", "Menu items added to reservation", userId);
        reservation.addHistoryRecord(history);

        // Save reservation
        Reservation updatedReservation = reservationRepository.save(reservation);

        return convertToDTO(updatedReservation);
    }

    /**
     * Processes menu items for a reservation.
     * Creates ReservationMenuItem entities for each selected menu item.
     *
     * @param reservation The reservation to add menu items to
     * @param menuItems The list of menu items to add
     * @throws EntityNotFoundException if a menu item is not found
     */
    @Transactional
    private void processMenuItems(Reservation reservation, List<MenuItemSelectionDTO> menuItems) {
        logger.info("Processing {} menu items for reservation: {}", menuItems.size(), reservation.getId());

        for (MenuItemSelectionDTO itemSelection : menuItems) {
            // Find the menu item
            MenuItem menuItem = menuItemRepository.findById(itemSelection.getMenuItemId())
                    .orElseThrow(() -> new EntityNotFoundException("MenuItem", itemSelection.getMenuItemId()));

            // Check if the menu item is active and available
            if (!menuItem.isActive() || !menuItem.isAvailable()) {
                logger.warn("Menu item {} is not active or available", menuItem.getId());
                continue;
            }

            // Check if the menu item belongs to the restaurant
            if (!menuItem.getRestaurantId().equals(reservation.getRestaurantId())) {
                logger.warn("Menu item {} does not belong to restaurant {}",
                        menuItem.getId(), reservation.getRestaurantId());
                continue;
            }

            // Create a new reservation menu item
            ReservationMenuItem reservationMenuItem = new ReservationMenuItem(
                    reservation,
                    menuItem,
                    itemSelection.getQuantity(),
                    itemSelection.getSpecialInstructions(),
                    menuItem.getPrice());

            // Save the reservation menu item
            reservationMenuItemRepository.save(reservationMenuItem);
            logger.debug("Added menu item {} to reservation {}", menuItem.getId(), reservation.getId());
        }
    }

    /**
     * Converts a Reservation entity to a ReservationDTO.
     * Maps all relevant fields from the entity to the DTO.
     *
     * @param reservation The reservation entity to convert
     * @return A new ReservationDTO object
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

        // Include menu items if any
        if (reservation.getMenuItems() != null && !reservation.getMenuItems().isEmpty()) {
            List<ReservationMenuItemDTO> menuItemDTOs = reservation.getMenuItems().stream()
                    .map(this::convertMenuItemToDTO)
                    .collect(Collectors.toList());
            dto.setMenuItems(menuItemDTOs);
        }

        return dto;
    }

    /**
     * Converts a ReservationMenuItem entity to a ReservationMenuItemDTO.
     *
     * @param menuItem The reservation menu item entity to convert
     * @return A new ReservationMenuItemDTO object
     */
    private ReservationMenuItemDTO convertMenuItemToDTO(ReservationMenuItem menuItem) {
        ReservationMenuItemDTO dto = new ReservationMenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setReservationId(menuItem.getReservation().getId());
        dto.setMenuItemId(menuItem.getMenuItem().getId());
        dto.setMenuItemName(menuItem.getMenuItem().getName());
        dto.setQuantity(menuItem.getQuantity());
        dto.setSpecialInstructions(menuItem.getSpecialInstructions());
        dto.setPrice(menuItem.getPrice());
        dto.setCreatedAt(menuItem.getCreatedAt());
        dto.setUpdatedAt(menuItem.getUpdatedAt());
        return dto;
    }
}