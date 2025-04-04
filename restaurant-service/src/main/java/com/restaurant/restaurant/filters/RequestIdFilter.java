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
 * This ID is added to response headers and the logging context to aid in troubleshooting.
 */
@Component
@Order(1)
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
    private static final String MDC_KEY = "requestId";

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
     * Get the current request ID from the request attributes.
     * 
     * @param request The HTTP request
     * @return The request ID or null if not set
     */
    public static String getCurrentRequestId(HttpServletRequest request) {
        return (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
    }
}