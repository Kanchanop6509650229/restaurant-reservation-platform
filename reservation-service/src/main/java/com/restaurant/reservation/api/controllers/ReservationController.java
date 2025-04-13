package com.restaurant.reservation.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.reservation.dto.ReservationAddMenuItemsRequest;
import com.restaurant.reservation.dto.ReservationCreateRequest;
import com.restaurant.reservation.dto.ReservationDTO;
import com.restaurant.reservation.dto.ReservationUpdateRequest;
import com.restaurant.reservation.security.CurrentUser;
import com.restaurant.reservation.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST Controller for managing restaurant reservations.
 * Provides endpoints for creating, retrieving, updating, and managing reservations.
 * All endpoints are secured and require appropriate authentication and authorization.
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation Management", description = "APIs for managing restaurant reservations")
@SecurityRequirement(name = "bearer-jwt")
public class ReservationController {

    /** Logger for this controller */
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    /** Service for reservation operations */
    private final ReservationService reservationService;

    /**
     * Constructs a new ReservationController with the specified ReservationService.
     *
     * @param reservationService The service responsible for reservation business logic
     */
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Retrieves a paginated list of reservations for the currently authenticated user.
     * Requires user authentication.
     *
     * @param userId The ID of the currently authenticated user
     * @param pageable Pagination parameters (default page size: 10)
     * @return ResponseEntity containing a paginated list of user's reservations
     */
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get user's reservations",
        description = "Retrieves a paginated list of reservations for the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Reservations retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized")
    })
    public ResponseEntity<ResponseDTO<Page<ReservationDTO>>> getReservationsByUser(
            @Parameter(hidden = true) @CurrentUser String userId,
            @Parameter(description = "Pagination parameters") @PageableDefault(size = 10) Pageable pageable) {
        logger.info("Fetching reservations for user: {}", userId);
        Page<ReservationDTO> reservations = reservationService.getReservationsByUserId(userId, pageable);
        logger.debug("Found {} reservations for user {}", reservations.getTotalElements(), userId);
        return ResponseEntity.ok(ResponseDTO.success(reservations));
    }

    /**
     * Retrieves a paginated list of reservations for a specific restaurant.
     * Requires ADMIN or RESTAURANT_OWNER role.
     *
     * @param restaurantId The ID of the restaurant
     * @param pageable Pagination parameters (default page size: 20)
     * @return ResponseEntity containing a paginated list of restaurant's reservations
     */
    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ResponseDTO<Page<ReservationDTO>>> getReservationsByRestaurant(
            @PathVariable String restaurantId,
            @PageableDefault(size = 20) Pageable pageable) {
        logger.info("Fetching reservations for restaurant: {}", restaurantId);
        Page<ReservationDTO> reservations = reservationService.getReservationsByRestaurantId(restaurantId, pageable);
        logger.debug("Found {} reservations for restaurant {}", reservations.getTotalElements(), restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(reservations));
    }

    /**
     * Retrieves a specific reservation by its ID.
     * Requires user authentication.
     *
     * @param id The ID of the reservation to retrieve
     * @return ResponseEntity containing the requested reservation
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<ReservationDTO>> getReservationById(@PathVariable String id) {
        logger.info("Fetching reservation with ID: {}", id);
        try {
            ReservationDTO reservation = reservationService.getReservationById(id);
            return ResponseEntity.ok(ResponseDTO.success(reservation));
        } catch (EntityNotFoundException e) {
            logger.warn("Reservation not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.error("Reservation not found with ID: " + id));
        }
    }

    /**
     * Creates a new reservation.
     * Requires user authentication and valid reservation data.
     * Optionally allows adding menu items to the reservation.
     *
     * @param createRequest The reservation creation request containing reservation details and optional menu items
     * @param userId The ID of the currently authenticated user
     * @return ResponseEntity containing the created reservation with HTTP 201 status
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new reservation", description = "Creates a new reservation with optional menu items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservation created successfully",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReservationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or business validation failed",
                content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content)
    })
    public ResponseEntity<ResponseDTO<ReservationDTO>> createReservation(
            @Valid @RequestBody ReservationCreateRequest createRequest,
            @Parameter(hidden = true) @CurrentUser String userId) {
        logger.info("Creating reservation for user: {}, restaurant: {}, party size: {}",
                userId, createRequest.getRestaurantId(), createRequest.getPartySize());

        try {
            ReservationDTO reservation = reservationService.createReservation(createRequest, userId);
            logger.info("Successfully created reservation: {}", reservation.getId());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ResponseDTO.success(reservation, "Reservation created successfully"));
        } catch (Exception e) {
            logger.error("Failed to create reservation: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error("Failed to create reservation: " + e.getMessage()));
        }
    }

    /**
     * Confirms a pending reservation.
     * Requires user authentication.
     * Only the user who created the reservation can confirm it.
     *
     * @param id The ID of the reservation to confirm
     * @param userId The ID of the currently authenticated user
     * @return ResponseEntity containing the confirmed reservation
     */
    @PostMapping("/{id}/confirm")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Confirm a reservation", description = "Confirms a pending reservation. Only the user who created the reservation can confirm it.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation confirmed successfully",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReservationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or business validation failed",
                content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden - User is not the creator of the reservation",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Reservation not found",
                content = @Content)
    })
    public ResponseEntity<ResponseDTO<ReservationDTO>> confirmReservation(
            @PathVariable String id,
            @CurrentUser String userId) {
        logger.info("Confirming reservation: {}, user: {}", id, userId);

        try {
            ReservationDTO reservation = reservationService.confirmReservation(id, userId);
            logger.info("Successfully confirmed reservation: {}", id);
            return ResponseEntity.ok(ResponseDTO.success(reservation, "Reservation confirmed successfully"));
        } catch (EntityNotFoundException e) {
            logger.warn("Reservation not found for confirmation: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.error("Reservation not found with ID: " + id));
        } catch (Exception e) {
            logger.error("Failed to confirm reservation: {}, error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error("Failed to confirm reservation: " + e.getMessage()));
        }
    }

    /**
     * Cancels an existing reservation.
     * Requires user authentication and a cancellation reason.
     * Only the user who created the reservation or the restaurant owner can cancel it.
     *
     * @param id The ID of the reservation to cancel
     * @param reason The reason for cancellation
     * @param userId The ID of the currently authenticated user
     * @return ResponseEntity containing the cancelled reservation
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel a reservation", description = "Cancels an existing reservation. Only the user who created the reservation or the restaurant owner can cancel it.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReservationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or business validation failed",
                content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden - User is neither the creator nor the restaurant owner",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Reservation not found",
                content = @Content)
    })
    public ResponseEntity<ResponseDTO<ReservationDTO>> cancelReservation(
            @PathVariable String id,
            @RequestParam String reason,
            @CurrentUser String userId) {
        logger.info("Cancelling reservation: {}, user: {}, reason: {}", id, userId, reason);

        try {
            ReservationDTO reservation = reservationService.cancelReservation(id, reason, userId);
            logger.info("Successfully cancelled reservation: {}", id);
            return ResponseEntity.ok(ResponseDTO.success(reservation, "Reservation cancelled successfully"));
        } catch (EntityNotFoundException e) {
            logger.warn("Reservation not found for cancellation: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.error("Reservation not found with ID: " + id));
        } catch (Exception e) {
            logger.error("Failed to cancel reservation: {}, error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error("Failed to cancel reservation: " + e.getMessage()));
        }
    }

    /**
     * Updates an existing reservation.
     * Requires user authentication and valid update data.
     * Only the user who created the reservation can update it.
     *
     * @param id The ID of the reservation to update
     * @param updateRequest The reservation update request containing new details
     * @param userId The ID of the currently authenticated user
     * @return ResponseEntity containing the updated reservation
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update a reservation", description = "Updates an existing reservation. Only the user who created the reservation can update it.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation updated successfully",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReservationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or business validation failed",
                content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden - User is not the creator of the reservation",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Reservation not found",
                content = @Content)
    })
    public ResponseEntity<ResponseDTO<ReservationDTO>> updateReservation(
            @PathVariable String id,
            @Valid @RequestBody ReservationUpdateRequest updateRequest,
            @CurrentUser String userId) {
        logger.info("Updating reservation: {}, user: {}", id, userId);

        try {
            ReservationDTO reservation = reservationService.updateReservation(id, updateRequest, userId);
            logger.info("Successfully updated reservation: {}", id);
            return ResponseEntity.ok(ResponseDTO.success(reservation, "Reservation updated successfully"));
        } catch (EntityNotFoundException e) {
            logger.warn("Reservation not found for update: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.error("Reservation not found with ID: " + id));
        } catch (Exception e) {
            logger.error("Failed to update reservation: {}, error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error("Failed to update reservation: " + e.getMessage()));
        }
    }

    /**
     * Adds menu items to an existing reservation.
     * Requires user authentication and valid menu item data.
     * Only the user who created the reservation can add menu items to it.
     * Menu items can only be added to reservations in PENDING or CONFIRMED status.
     *
     * @param id The ID of the reservation to add menu items to
     * @param addMenuItemsRequest The request containing menu items to add
     * @param userId The ID of the currently authenticated user
     * @return ResponseEntity containing the updated reservation
     */
    @PostMapping("/{id}/menu-items")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add menu items to a reservation", description = "Adds menu items to an existing reservation. Only the user who created the reservation can add menu items.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu items added successfully",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReservationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or business validation failed",
                content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden - User is not the creator of the reservation",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Reservation not found",
                content = @Content)
    })
    public ResponseEntity<ResponseDTO<ReservationDTO>> addMenuItemsToReservation(
            @PathVariable String id,
            @Valid @RequestBody ReservationAddMenuItemsRequest addMenuItemsRequest,
            @CurrentUser String userId) {
        logger.info("Adding menu items to reservation: {}, user: {}", id, userId);

        try {
            ReservationDTO reservation = reservationService.addMenuItemsToReservation(id, addMenuItemsRequest, userId);
            logger.info("Successfully added menu items to reservation: {}", id);
            return ResponseEntity.ok(ResponseDTO.success(reservation, "Menu items added to reservation successfully"));
        } catch (EntityNotFoundException e) {
            logger.warn("Reservation not found for adding menu items: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.error("Reservation not found with ID: " + id));
        } catch (Exception e) {
            logger.error("Failed to add menu items to reservation: {}, error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error("Failed to add menu items to reservation: " + e.getMessage()));
        }
    }
}