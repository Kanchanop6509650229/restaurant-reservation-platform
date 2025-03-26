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

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<Page<ReservationDTO>>> getReservationsByUser(
            @CurrentUser String userId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReservationDTO> reservations = reservationService.getReservationsByUserId(userId, pageable);
        return ResponseEntity.ok(ResponseDTO.success(reservations));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ResponseDTO<Page<ReservationDTO>>> getReservationsByRestaurant(
            @PathVariable String restaurantId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ReservationDTO> reservations = reservationService.getReservationsByRestaurantId(restaurantId, pageable);
        return ResponseEntity.ok(ResponseDTO.success(reservations));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<ReservationDTO>> getReservationById(@PathVariable String id) {
        ReservationDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(ResponseDTO.success(reservation));
    }

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

    @PostMapping("/{id}/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<ReservationDTO>> confirmReservation(
            @PathVariable String id,
            @CurrentUser String userId) {
        ReservationDTO reservation = reservationService.confirmReservation(id, userId);
        return ResponseEntity.ok(ResponseDTO.success(reservation, "Reservation confirmed successfully"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<ReservationDTO>> cancelReservation(
            @PathVariable String id,
            @RequestParam String reason,
            @CurrentUser String userId) {
        ReservationDTO reservation = reservationService.cancelReservation(id, reason, userId);
        return ResponseEntity.ok(ResponseDTO.success(reservation, "Reservation cancelled successfully"));
    }

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