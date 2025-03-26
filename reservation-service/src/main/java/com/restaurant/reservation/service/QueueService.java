package com.restaurant.reservation.service;

import com.restaurant.common.constants.StatusCodes;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.reservation.domain.models.Queue;
import com.restaurant.reservation.domain.repositories.QueueRepository;
import com.restaurant.reservation.dto.QueueCreateRequest;
import com.restaurant.reservation.dto.QueueDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueueService {

    private final QueueRepository queueRepository;

    @Value("${queue.notification-expiry-minutes:15}")
    private int notificationExpiryMinutes;

    public QueueService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    public List<QueueDTO> getActiveQueuesByRestaurantId(String restaurantId) {
        return queueRepository.findActiveQueuesByRestaurantId(restaurantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public QueueDTO getQueueById(String id) {
        Queue queue = queueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Queue", id));
        return convertToDTO(queue);
    }

    @Transactional
    public QueueDTO addToQueue(QueueCreateRequest createRequest, String userId) {
        validateQueueRequest(createRequest);
        
        // Check if user already has an active queue entry for this restaurant
        List<Queue> existingQueues = queueRepository.findByUserIdAndStatusIn(
                userId, List.of("WAITING", "NOTIFIED"));
        
        if (!existingQueues.isEmpty()) {
            throw new ValidationException("userId", 
                    "You are already in the queue for this restaurant");
        }
        
        // Create new queue entry
        Queue queue = new Queue();
        queue.setRestaurantId(createRequest.getRestaurantId());
        queue.setUserId(userId);
        queue.setCustomerName(createRequest.getCustomerName());
        queue.setCustomerPhone(createRequest.getCustomerPhone());
        queue.setCustomerEmail(createRequest.getCustomerEmail());
        queue.setPartySize(createRequest.getPartySize());
        queue.setStatus("WAITING");
        queue.setJoinedAt(LocalDateTime.now());
        
        // Set position (last in queue)
        List<Queue> activeQueues = queueRepository.findActiveQueuesByRestaurantId(
                createRequest.getRestaurantId());
        queue.setPosition(activeQueues.size() + 1);
        
        // Set estimated wait time (10 minutes per position as a simple heuristic)
        queue.setEstimatedWaitMinutes(queue.getPosition() * 10);
        
        return convertToDTO(queueRepository.save(queue));
    }

    @Transactional
    public QueueDTO notifyCustomer(String id) {
        Queue queue = queueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Queue", id));
        
        // Check if queue entry is in WAITING status
        if (!queue.getStatus().equals("WAITING")) {
            throw new ValidationException("status", 
                    "Cannot notify customer in " + queue.getStatus() + " status");
        }
        
        // Update queue entry
        queue.setStatus("NOTIFIED");
        queue.setNotifiedAt(LocalDateTime.now());
        
        // Set expiry time
        queue.setExpiredAt(queue.getNotifiedAt().plusMinutes(notificationExpiryMinutes));
        
        return convertToDTO(queueRepository.save(queue));
    }

    @Transactional
    public QueueDTO markAsSeated(String id) {
        Queue queue = queueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Queue", id));
        
        // Check if queue entry can be marked as seated
        if (!queue.getStatus().equals("WAITING") && !queue.getStatus().equals("NOTIFIED")) {
            throw new ValidationException("status", 
                    "Cannot seat customer in " + queue.getStatus() + " status");
        }
        
        // Update queue entry
        queue.setStatus("SEATED");
        
        Queue updatedQueue = queueRepository.save(queue);
        
        // Recalculate positions for remaining queued customers
        recalculatePositions(updatedQueue.getRestaurantId());
        
        return convertToDTO(updatedQueue);
    }

    @Transactional
    public QueueDTO cancelQueue(String id, String reason) {
        Queue queue = queueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Queue", id));
        
        // Check if queue entry can be cancelled
        if (!queue.getStatus().equals("WAITING") && !queue.getStatus().equals("NOTIFIED")) {
            throw new ValidationException("status", 
                    "Cannot cancel queue entry in " + queue.getStatus() + " status");
        }
        
        // Update queue entry
        queue.setStatus("CANCELLED");
        queue.setNotes(reason);
        
        Queue updatedQueue = queueRepository.save(queue);
        
        // Recalculate positions for remaining queued customers
        recalculatePositions(updatedQueue.getRestaurantId());
        
        return convertToDTO(updatedQueue);
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void processExpiredNotifications() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(notificationExpiryMinutes);
        List<Queue> expiredNotifications = queueRepository.findExpiredNotifications(expiryTime);
        
        for (Queue queue : expiredNotifications) {
            queue.setStatus("EXPIRED");
            queueRepository.save(queue);
            
            // Recalculate positions
            recalculatePositions(queue.getRestaurantId());
        }
    }

    private void recalculatePositions(String restaurantId) {
        List<Queue> activeQueues = queueRepository.findActiveQueuesByRestaurantId(restaurantId);
        
        int position = 1;
        for (Queue queue : activeQueues) {
            queue.setPosition(position);
            queue.setEstimatedWaitMinutes(position * 10); // Simple heuristic
            queueRepository.save(queue);
            position++;
        }
    }

    private void validateQueueRequest(QueueCreateRequest request) {
        if (request.getRestaurantId() == null || request.getRestaurantId().isEmpty()) {
            throw new ValidationException("restaurantId", "Restaurant ID is required");
        }
        
        if (request.getCustomerName() == null || request.getCustomerName().isEmpty()) {
            throw new ValidationException("customerName", "Customer name is required");
        }
        
        if (request.getPartySize() <= 0) {
            throw new ValidationException("partySize", "Party size must be greater than 0");
        }
    }

    private QueueDTO convertToDTO(Queue queue) {
        QueueDTO dto = new QueueDTO();
        dto.setId(queue.getId());
        dto.setRestaurantId(queue.getRestaurantId());
        dto.setUserId(queue.getUserId());
        dto.setCustomerName(queue.getCustomerName());
        dto.setCustomerPhone(queue.getCustomerPhone());
        dto.setCustomerEmail(queue.getCustomerEmail());
        dto.setPartySize(queue.getPartySize());
        dto.setJoinedAt(queue.getJoinedAt());
        dto.setNotifiedAt(queue.getNotifiedAt());
        dto.setExpiredAt(queue.getExpiredAt());
        dto.setStatus(queue.getStatus());
        dto.setPosition(queue.getPosition());
        dto.setEstimatedWaitMinutes(queue.getEstimatedWaitMinutes());
        dto.setNotes(queue.getNotes());
        
        // Calculate number of parties ahead
        if (queue.getPosition() > 1) {
            dto.setPartiesAhead(queue.getPosition() - 1);
        } else {
            dto.setPartiesAhead(0);
        }
        
        return dto;
    }
}