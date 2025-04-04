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

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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