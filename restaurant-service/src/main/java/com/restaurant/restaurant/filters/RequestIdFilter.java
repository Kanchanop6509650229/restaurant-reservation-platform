package com.restaurant.restaurant.filters;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter that generates a unique request ID for each incoming API request.
 * This filter provides:
 * - Request ID generation and management
 * - Request tracking across services
 * - Logging correlation support
 * - Distributed tracing capabilities
 * 
 * The filter performs the following functions:
 * 1. Checks for an existing request ID in the X-Request-ID header
 * 2. Generates a new UUID if no request ID is present
 * 3. Adds the request ID to:
 *    - Response headers (X-Request-ID)
 *    - Request attributes (for access in controllers/services)
 *    - Logging context (MDC) for correlation in logs
 * 4. Cleans up the MDC context after request processing
 * 
 * This filter is ordered first (Order(1)) to ensure request IDs are available
 * for all subsequent filters and request processing.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
@Order(1)
public class RequestIdFilter extends OncePerRequestFilter {

    /** Header name for the request ID */
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    
    /** Attribute name for storing request ID in request attributes */
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
    
    /** Key for storing request ID in the logging context */
    private static final String MDC_KEY = "requestId";

    /**
     * Processes each request to ensure it has a unique request ID.
     * This method:
     * - Extracts or generates a request ID
     * - Stores it in request attributes
     * - Adds it to response headers
     * - Sets it in the logging context
     * - Cleans up after request processing
     *
     * @param request The incoming HTTP request
     * @param response The outgoing HTTP response
     * @param filterChain The filter chain for request processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        
        // If no request ID in header, generate one
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        
        // Store in request for access in the controllers/services
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        
        // Add to response headers
        response.setHeader(REQUEST_ID_HEADER, requestId);
        
        // Add to logging context (MDC)
        MDC.put(MDC_KEY, requestId);
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clean up
            MDC.remove(MDC_KEY);
        }
    }
    
    /**
     * Retrieves the current request ID from the request attributes.
     * This method is used by other components to access the request ID
     * for logging and correlation purposes.
     *
     * @param request The HTTP request containing the request ID
     * @return The request ID if present, null otherwise
     */
    public static String getCurrentRequestId(HttpServletRequest request) {
        return (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
    }
}