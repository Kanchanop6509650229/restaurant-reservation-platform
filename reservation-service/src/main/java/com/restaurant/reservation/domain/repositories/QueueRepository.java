package com.restaurant.reservation.domain.repositories;

import com.restaurant.reservation.domain.models.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QueueRepository extends JpaRepository<Queue, String> {

    List<Queue> findByRestaurantIdAndStatus(String restaurantId, String status);
    
    @Query("SELECT q FROM Queue q WHERE q.restaurantId = :restaurantId AND " +
           "q.status IN ('WAITING', 'NOTIFIED') ORDER BY q.position")
    List<Queue> findActiveQueuesByRestaurantId(@Param("restaurantId") String restaurantId);
    
    @Query("SELECT COUNT(q) FROM Queue q WHERE q.restaurantId = :restaurantId AND " +
           "q.status IN ('WAITING', 'NOTIFIED') AND q.position < :position")
    int countPartiesAhead(@Param("restaurantId") String restaurantId, @Param("position") int position);
    
    List<Queue> findByUserIdAndStatusIn(String userId, List<String> statuses);
    
    @Query("SELECT q FROM Queue q WHERE q.status = 'NOTIFIED' AND " +
           "q.notifiedAt < :expiryTime")
    List<Queue> findExpiredNotifications(@Param("expiryTime") LocalDateTime expiryTime);
}