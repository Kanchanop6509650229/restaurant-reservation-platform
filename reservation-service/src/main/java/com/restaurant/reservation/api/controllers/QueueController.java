package com.restaurant.reservation.api.controllers;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.reservation.dto.QueueCreateRequest;
import com.restaurant.reservation.dto.QueueDTO;
import com.restaurant.reservation.security.CurrentUser;
import com.restaurant.reservation.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queues")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ResponseDTO<List<QueueDTO>>> getActiveQueuesByRestaurant(
            @PathVariable String restaurantId) {
        List<QueueDTO> queues = queueService.getActiveQueuesByRestaurantId(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(queues));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<QueueDTO>> getQueueById(@PathVariable String id) {
        QueueDTO queue = queueService.getQueueById(id);
        return ResponseEntity.ok(ResponseDTO.success(queue));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<QueueDTO>> addToQueue(
            @Valid @RequestBody QueueCreateRequest createRequest,
            @CurrentUser String userId) {
        QueueDTO queue = queueService.addToQueue(createRequest, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.success(queue, "Added to queue successfully"));
    }

    @PostMapping("/{id}/notify")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ResponseDTO<QueueDTO>> notifyCustomer(@PathVariable String id) {
        QueueDTO queue = queueService.notifyCustomer(id);
        return ResponseEntity.ok(ResponseDTO.success(queue, "Customer notified successfully"));
    }

    @PostMapping("/{id}/seat")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ResponseDTO<QueueDTO>> markAsSeated(@PathVariable String id) {
        QueueDTO queue = queueService.markAsSeated(id);
        return ResponseEntity.ok(ResponseDTO.success(queue, "Customer marked as seated"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<QueueDTO>> cancelQueue(
            @PathVariable String id,
            @RequestParam String reason) {
        QueueDTO queue = queueService.cancelQueue(id, reason);
        return ResponseEntity.ok(ResponseDTO.success(queue, "Queue entry cancelled successfully"));
    }
}