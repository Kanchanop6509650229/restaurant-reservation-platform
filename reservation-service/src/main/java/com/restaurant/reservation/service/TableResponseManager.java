package com.restaurant.reservation.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.restaurant.common.events.reservation.FindAvailableTableResponseEvent;

/**
 * Service ที่ใช้ในการจัดการกับการรอ Response จาก Kafka
 * โดยใช้ CompletableFuture เพื่อให้สามารถทำงานแบบ Asynchronous ได้
 */
@Component
public class TableResponseManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TableResponseManager.class);
    
    // Map สำหรับเก็บค่า CompletableFuture ที่รอ Response
    // Key: correlationId, Value: CompletableFuture ที่จะได้รับผลลัพธ์เมื่อมี Response
    private final Map<String, CompletableFuture<FindAvailableTableResponseEvent>> pendingResponses = new ConcurrentHashMap<>();
    
    /**
     * สร้าง CompletableFuture สำหรับรอ Response ตาม correlationId
     */
    public CompletableFuture<FindAvailableTableResponseEvent> createPendingResponse(String correlationId) {
        CompletableFuture<FindAvailableTableResponseEvent> future = new CompletableFuture<>();
        pendingResponses.put(correlationId, future);
        return future;
    }
    
    /**
     * เมื่อได้รับ Response จาก Kafka ให้เรียกเมธอดนี้เพื่อส่งผลลัพธ์ไปยัง CompletableFuture ที่รออยู่
     */
    public void completeResponse(FindAvailableTableResponseEvent response) {
        String correlationId = response.getCorrelationId();
        CompletableFuture<FindAvailableTableResponseEvent> future = pendingResponses.remove(correlationId);
        
        if (future != null) {
            future.complete(response);
            logger.info("Completed response for correlationId: {}", correlationId);
        } else {
            logger.warn("Received response for unknown correlationId: {}", correlationId);
        }
    }
    
    /**
     * ยกเลิกการรอ Response หากเกิด timeout หรือกรณีอื่นๆ
     */
    public void cancelPendingResponse(String correlationId, String reason) {
        CompletableFuture<FindAvailableTableResponseEvent> future = pendingResponses.remove(correlationId);
        
        if (future != null) {
            future.completeExceptionally(new RuntimeException("Request cancelled: " + reason));
            logger.warn("Cancelled pending response for correlationId: {} - Reason: {}", correlationId, reason);
        }
    }
    
    /**
     * เคลียร์ response ที่หมดเวลารอแล้ว
     */
    public void cleanupExpiredResponses() {
        pendingResponses.forEach((correlationId, future) -> {
            if (future.isDone() || future.isCompletedExceptionally() || future.isCancelled()) {
                pendingResponses.remove(correlationId);
            }
        });
    }
    
    /**
     * รับ Response โดยมีการตั้ง timeout
     */
    public FindAvailableTableResponseEvent getResponseWithTimeout(String correlationId, long timeout, TimeUnit unit) throws Exception {
        CompletableFuture<FindAvailableTableResponseEvent> future = pendingResponses.get(correlationId);
        if (future == null) {
            throw new IllegalArgumentException("No pending response for correlationId: " + correlationId);
        }
        
        try {
            return future.get(timeout, unit);
        } catch (Exception e) {
            pendingResponses.remove(correlationId);
            throw e;
        }
    }
}