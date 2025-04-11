package com.restaurant.restaurant.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.restaurant.common.constants.ErrorCodes;
import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.common.exceptions.AuthenticationException;
import com.restaurant.common.exceptions.BaseException;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.restaurant.filters.RequestIdFilter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler for the restaurant service.
 * This class provides:
 * - Centralized exception handling
 * - Consistent error response format
 * - Detailed error logging
 * - Request tracking
 * 
 * Handles various types of exceptions and converts them into
 * appropriate HTTP responses with standardized error formats.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /** Logger instance for error tracking */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles EntityNotFoundException by returning a 404 Not Found response.
     * This occurs when a requested entity cannot be found in the database.
     *
     * @param ex The caught EntityNotFoundException
     * @param request The web request
     * @param httpRequest The HTTP servlet request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseDTO<Void>> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request, HttpServletRequest httpRequest) {
        String message = String.format("The requested %s with ID '%s' could not be found. Please verify the ID and try again.", 
                ex.getEntityType(), ex.getEntityId());
        
        logger.error("Entity not found: {}", message);
        
        ResponseDTO<Void> responseDTO = ResponseDTO.error(message, ex.getErrorCode());
        responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(httpRequest));
        responseDTO.setTimestamp(java.time.LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(responseDTO);
    }

    /**
     * Handles ValidationException by returning a 400 Bad Request response.
     * This occurs when input data fails validation rules.
     *
     * @param ex The caught ValidationException
     * @param request The HTTP servlet request
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseDTO<Map<String, String>>> handleValidationException(ValidationException ex, HttpServletRequest request) {
        logger.error("Validation failed: {} with errors: {}", 
                ex.getMessage(), ex.getValidationErrors());
        
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
        responseDTO.setSuccess(false);
        responseDTO.setMessage("The provided data failed validation. Please correct the errors and try again.");
        responseDTO.setErrorCode(ErrorCodes.VALIDATION_ERROR);
        responseDTO.setData(ex.getValidationErrors());
        responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
        responseDTO.setTimestamp(java.time.LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseDTO);
    }

    /**
     * Handles AuthenticationException by returning a 401 Unauthorized response.
     * This occurs when authentication fails or credentials are invalid.
     *
     * @param ex The caught AuthenticationException
     * @param request The HTTP servlet request
     * @return ResponseEntity with authentication error details
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDTO<Void>> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        logger.error("Authentication failed: {}", ex.getMessage());
        
        ResponseDTO<Void> responseDTO = ResponseDTO.error(ex.getMessage(), ex.getErrorCode());
        responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
        responseDTO.setTimestamp(java.time.LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(responseDTO);
    }

    /**
     * Handles AccessDeniedException by returning a 403 Forbidden response.
     * This occurs when a user attempts to access a resource without proper authorization.
     *
     * @param ex The caught AccessDeniedException
     * @param request The HTTP servlet request
     * @return ResponseEntity with authorization error details
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDTO<Void>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        String message = "You don't have permission to perform this operation. Please contact an administrator if you require access.";
        logger.error("Access denied: {}", ex.getMessage());
        
        ResponseDTO<Void> responseDTO = ResponseDTO.error(message, ErrorCodes.FORBIDDEN);
        responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
        responseDTO.setTimestamp(java.time.LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(responseDTO);
    }

    /**
     * Handles MethodArgumentNotValidException by returning a 400 Bad Request response.
     * This occurs when request parameters fail Spring validation.
     *
     * @param ex The caught MethodArgumentNotValidException
     * @param request The HTTP servlet request
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        BindingResult result = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        
        for (FieldError error : result.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        logger.error("Validation failed during request processing: {}", errors);
        
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
        responseDTO.setSuccess(false);
        responseDTO.setMessage("The request contains invalid data. Please review the errors and correct your submission.");
        responseDTO.setErrorCode(ErrorCodes.VALIDATION_ERROR);
        responseDTO.setData(errors);
        responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
        responseDTO.setTimestamp(java.time.LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseDTO);
    }

    /**
     * Handles MethodArgumentTypeMismatchException by returning a 400 Bad Request response.
     * This occurs when request parameters have incorrect types.
     *
     * @param ex The caught MethodArgumentTypeMismatchException
     * @param request The HTTP servlet request
     * @return ResponseEntity with type mismatch error details
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDTO<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        String message = String.format("The parameter '%s' has an invalid value: '%s'. Please provide a valid %s value.", 
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        
        logger.error("Type mismatch: {}", message);
        
        ResponseDTO<Void> responseDTO = ResponseDTO.error(message, ErrorCodes.VALIDATION_ERROR);
        responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
        responseDTO.setTimestamp(java.time.LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseDTO);
    }

    /**
     * Handles NoHandlerFoundException by returning a 404 Not Found response.
     * This occurs when a requested endpoint does not exist.
     *
     * @param ex The caught NoHandlerFoundException
     * @param request The HTTP servlet request
     * @return ResponseEntity with endpoint not found error details
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseDTO<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        String message = String.format("The requested endpoint '%s %s' does not exist. Please check the URL and HTTP method.", 
                ex.getHttpMethod(), ex.getRequestURL());
        
        logger.error("Endpoint not found: {}", message);
        
        ResponseDTO<Void> responseDTO = ResponseDTO.error(message, ErrorCodes.NOT_FOUND);
        responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
        responseDTO.setTimestamp(java.time.LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(responseDTO);
    }

    /**
     * Handles RestaurantCapacityException by returning a 409 Conflict response.
     * This occurs when restaurant capacity constraints are violated.
     *
     * @param ex The caught RestaurantCapacityException
     * @param request The HTTP servlet request
     * @return ResponseEntity with capacity error details
     */
    @ExceptionHandler(RestaurantCapacityException.class)
    public ResponseEntity<ResponseDTO<Void>> handleRestaurantCapacityException(RestaurantCapacityException ex, HttpServletRequest request) {
        logger.error("Restaurant capacity issue: {}", ex.getMessage());
        
        ResponseDTO<Void> responseDTO = ResponseDTO.error(ex.getMessage(), ex.getErrorCode());
        responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
        responseDTO.setTimestamp(java.time.LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(responseDTO);
    }
    
    /**
     * Handles TableStatusException by returning a 409 Conflict response.
     * This occurs when table status constraints are violated.
     *
     * @param ex The caught TableStatusException
     * @param request The HTTP servlet request
     * @return ResponseEntity with table status error details
     */
    @ExceptionHandler(TableStatusException.class)
    public ResponseEntity<ResponseDTO<Void>> handleTableStatusException(TableStatusException ex, HttpServletRequest request) {
        logger.error("Table status issue: {}", ex.getMessage());
        
        ResponseDTO<Void> responseDTO = ResponseDTO.error(ex.getMessage(), ex.getErrorCode());
        responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
        responseDTO.setTimestamp(java.time.LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(responseDTO);
    }

    /**
     * Handles BaseException by returning a 500 Internal Server Error response.
     * This is a catch-all for application-specific exceptions.
     *
     * @param ex The caught BaseException
     * @param request The HTTP servlet request
     * @return ResponseEntity with application error details
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseDTO<Void>> handleBaseException(BaseException ex, HttpServletRequest request) {
        logger.error("Application error: {} with error code: {}", 
                ex.getMessage(), ex.getErrorCode(), ex);
        
        ResponseDTO<Void> responseDTO = ResponseDTO.error(ex.getMessage(), ex.getErrorCode());
        responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
        responseDTO.setTimestamp(java.time.LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseDTO);
    }

    /**
     * Handles generic Exception by returning a 500 Internal Server Error response.
     * This is a catch-all for unhandled exceptions.
     *
     * @param ex The caught Exception
     * @param request The HTTP servlet request
     * @return ResponseEntity with generic error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Void>> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        
        String message = "An unexpected error occurred while processing your request. " +
                "Our technical team has been notified and is working to resolve the issue.";
        
        ResponseDTO<Void> responseDTO = ResponseDTO.error(message, ErrorCodes.GENERAL_ERROR);
        responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
        responseDTO.setTimestamp(java.time.LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseDTO);
    }
}