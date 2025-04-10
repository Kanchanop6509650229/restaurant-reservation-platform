package com.restaurant.reservation.filters;

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
 * This ID is added to response headers and the logging context to aid in troubleshooting.
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

    /** HTTP header name for request ID */
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    
    /** Request attribute name for storing request ID */
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
    
    /** MDC (Mapped Diagnostic Context) key for request ID */
    private static final String MDC_KEY = "requestId";

    /**
     * Processes each incoming request to ensure a unique request ID is available.
     * The request ID is used for request tracking and log correlation.
     *
     * @param request The HTTP request
     * @param response The HTTP response
     * @param filterChain The filter chain to continue processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Get existing request ID from header or generate new one
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        
        // Store request ID in request attributes for access in controllers/services
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        
        // Add request ID to response headers
        response.setHeader(REQUEST_ID_HEADER, requestId);
        
        // Add request ID to logging context for correlation
        MDC.put(MDC_KEY, requestId);
        
        try {
            // Continue processing the request
            filterChain.doFilter(request, response);
        } finally {
            // Clean up MDC context after request processing
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