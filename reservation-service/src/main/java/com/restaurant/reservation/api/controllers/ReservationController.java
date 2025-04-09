package com.restaurant.reservation.api.controllers;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.reservation.dto.ReservationCreateRequest;
import com.restaurant.reservation.dto.ReservationDTO;
import com.restaurant.reservation.dto.ReservationUpdateRequest;
import com.restaurant.reservation.security.CurrentUser;
import com.restaurant.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing restaurant reservations.
 * Provides endpoints for creating, retrieving, updating, and managing reservations.
 * All endpoints are secured and require appropriate authentication and authorization.
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

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
    public ResponseEntity<ResponseDTO<Page<ReservationDTO>>> getReservationsByUser(
            @CurrentUser String userId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReservationDTO> reservations = reservationService.getReservationsByUserId(userId, pageable);
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
        Page<ReservationDTO> reservations = reservationService.getReservationsByRestaurantId(restaurantId, pageable);
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
        ReservationDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(ResponseDTO.success(reservation));
    }

    /**
     * Creates a new reservation.
     * Requires user authentication and valid reservation data.
     *
     * @param createRequest The reservation creation request containing reservation details
     * @param userId The ID of the currently authenticated user
     * @return ResponseEntity containing the created reservation with HTTP 201 status
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<ReservationDTO>> createReservation(
            @Valid @RequestBody ReservationCreateRequest createRequest,
            @CurrentUser String userId) {
        ReservationDTO reservation = reservationService.createReservation(createRequest, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.success(reservation, "Reservation created successfully"));
    }

    /**
     * Confirms a pending reservation.
     * Requires user authentication.
     *
     * @param id The ID of the reservation to confirm
     * @param userId The ID of the currently authenticated user
     * @return ResponseEntity containing the confirmed reservation
     */
    @PostMapping("/{id}/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<ReservationDTO>> confirmReservation(
            @PathVariable String id,
            @CurrentUser String userId) {
        ReservationDTO reservation = reservationService.confirmReservation(id, userId);
        return ResponseEntity.ok(ResponseDTO.success(reservation, "Reservation confirmed successfully"));
    }

    /**
     * Cancels an existing reservation.
     * Requires user authentication and a cancellation reason.
     *
     * @param id The ID of the reservation to cancel
     * @param reason The reason for cancellation
     * @param userId The ID of the currently authenticated user
     * @return ResponseEntity containing the cancelled reservation
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<ReservationDTO>> cancelReservation(
            @PathVariable String id,
            @RequestParam String reason,
            @CurrentUser String userId) {
        ReservationDTO reservation = reservationService.cancelReservation(id, reason, userId);
        return ResponseEntity.ok(ResponseDTO.success(reservation, "Reservation cancelled successfully"));
    }

    /**
     * Updates an existing reservation.
     * Requires user authentication and valid update data.
     *
     * @param id The ID of the reservation to update
     * @param updateRequest The reservation update request containing new details
     * @param userId The ID of the currently authenticated user
     * @return ResponseEntity containing the updated reservation
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<ReservationDTO>> updateReservation(
            @PathVariable String id,
            @Valid @RequestBody ReservationUpdateRequest updateRequest,
            @CurrentUser String userId) {
        ReservationDTO reservation = reservationService.updateReservation(id, updateRequest, userId);
        return ResponseEntity.ok(ResponseDTO.success(reservation, "Reservation updated successfully"));
    }
}