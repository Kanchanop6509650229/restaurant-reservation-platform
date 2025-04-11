package com.restaurant.reservation.exception;

import java.time.LocalDateTime;
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
import com.restaurant.reservation.filters.RequestIdFilter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler for the Reservation Service that centralizes
 * the handling of all exceptions thrown by the application. This class
 * provides consistent error responses across all endpoints and ensures
 * proper logging of exceptions.
 *
 * The handler processes various types of exceptions:
 * - Entity not found exceptions
 * - Validation exceptions
 * - Authentication and authorization exceptions
 * - Reservation-specific exceptions
 * - General application exceptions
 *
 * Each exception handler method returns a standardized ResponseDTO
 * with appropriate HTTP status codes and error messages.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

        /** Logger instance for tracking exception occurrences */
        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /**
         * Handles exceptions when a requested entity cannot be found in the system.
         * Provides specific error messages based on the entity type and includes
         * the entity ID in the response.
         *
         * @param ex The EntityNotFoundException that was thrown
         * @param request The web request that caused the exception
         * @param httpRequest The HTTP servlet request for request ID tracking
         * @return ResponseEntity containing error details with HTTP 404 status
         */
        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ResponseDTO<Void>> handleEntityNotFoundException(EntityNotFoundException ex,
                        WebRequest request, HttpServletRequest httpRequest) {
                String message;
                String errorCode = ex.getErrorCode();

                // Customize the message based on entity type
                if ("Restaurant".equals(ex.getEntityType())) {
                        message = String.format(
                                        "Restaurant with ID '%s' does not exist. Please check the restaurant ID and try again.",
                                        ex.getEntityId());
                        errorCode = ErrorCodes.RESTAURANT_NOT_FOUND; // We should add this to ErrorCodes
                } else {
                        message = String.format(
                                        "The requested %s with ID '%s' could not be found. Please verify the ID and try again.",
                                        ex.getEntityType(), ex.getEntityId());
                }

                logger.error("Entity not found: {}", message);

                ResponseDTO<Void> responseDTO = ResponseDTO.error(message, errorCode);
                responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(httpRequest));
                responseDTO.setTimestamp(LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(responseDTO);
        }

        /**
         * Handles validation exceptions that occur during request processing.
         * Returns a detailed map of validation errors for each field that failed validation.
         *
         * @param ex The ValidationException containing validation errors
         * @param request The HTTP servlet request for request ID tracking
         * @return ResponseEntity containing validation errors with HTTP 400 status
         */
        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ResponseDTO<Map<String, String>>> handleValidationException(ValidationException ex,
                        HttpServletRequest request) {
                logger.error("Validation failed: {} with errors: {}",
                                ex.getMessage(), ex.getValidationErrors());

                ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
                responseDTO.setSuccess(false);
                responseDTO.setMessage("The provided data failed validation. Please correct the errors and try again.");
                responseDTO.setErrorCode(ErrorCodes.VALIDATION_ERROR);
                responseDTO.setData(ex.getValidationErrors());
                responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
                responseDTO.setTimestamp(LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(responseDTO);
        }

        /**
         * Handles authentication-related exceptions, such as invalid credentials
         * or missing authentication tokens.
         *
         * @param ex The AuthenticationException that was thrown
         * @param request The HTTP servlet request for request ID tracking
         * @return ResponseEntity containing authentication error with HTTP 401 status
         */
        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ResponseDTO<Void>> handleAuthenticationException(AuthenticationException ex,
                        HttpServletRequest request) {
                logger.error("Authentication failed: {}", ex.getMessage());

                ResponseDTO<Void> responseDTO = ResponseDTO.error(ex.getMessage(), ex.getErrorCode());
                responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));
                responseDTO.setTimestamp(LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(responseDTO);
        }

        /**
         * Handles access denied exceptions when a user attempts to perform
         * an operation they don't have permission for.
         *
         * @param ex The AccessDeniedException that was thrown
         * @return ResponseEntity containing access denied error with HTTP 403 status
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ResponseDTO<Void>> handleAccessDeniedException(AccessDeniedException ex) {
                String message = "You don't have permission to perform this operation. Please contact an administrator if you require access.";
                logger.error("Access denied: {}", ex.getMessage());

                ResponseDTO<Void> responseDTO = ResponseDTO.error(message, ErrorCodes.FORBIDDEN);
                responseDTO.setTimestamp(LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(responseDTO);
        }

        /**
         * Handles method argument validation failures during request processing.
         * Extracts field-level validation errors and returns them in a structured format.
         *
         * @param ex The MethodArgumentNotValidException containing validation errors
         * @return ResponseEntity containing field validation errors with HTTP 400 status
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ResponseDTO<Map<String, String>>> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex) {

                BindingResult result = ex.getBindingResult();
                Map<String, String> errors = new HashMap<>();

                for (FieldError error : result.getFieldErrors()) {
                        errors.put(error.getField(), error.getDefaultMessage());
                }

                logger.error("Validation failed during request processing: {}", errors);

                ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
                responseDTO.setSuccess(false);
                responseDTO.setMessage(
                                "The request contains invalid data. Please review the errors and correct your submission.");
                responseDTO.setErrorCode(ErrorCodes.VALIDATION_ERROR);
                responseDTO.setData(errors);
                responseDTO.setTimestamp(LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(responseDTO);
        }

        /**
         * Handles type mismatch exceptions when request parameters cannot be
         * converted to their expected types.
         *
         * @param ex The MethodArgumentTypeMismatchException that was thrown
         * @return ResponseEntity containing type mismatch error with HTTP 400 status
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ResponseDTO<Void>> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex) {

                String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "appropriate";
                String value = ex.getValue() != null ? ex.getValue().toString() : "null";

                String message = String.format(
                                "The parameter '%s' has an invalid value: '%s'. Please provide a valid %s value.",
                                ex.getName(), value, requiredType);

                logger.error("Type mismatch: {}", message);

                ResponseDTO<Void> responseDTO = ResponseDTO.error(message, ErrorCodes.VALIDATION_ERROR);
                responseDTO.setTimestamp(LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(responseDTO);
        }

        /**
         * Handles cases where no handler is found for a requested endpoint.
         * Provides a clear error message indicating the invalid URL or HTTP method.
         *
         * @param ex The NoHandlerFoundException that was thrown
         * @return ResponseEntity containing endpoint not found error with HTTP 404 status
         */
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ResponseDTO<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
                String message = String.format(
                                "The requested endpoint '%s %s' does not exist. Please check the URL and HTTP method.",
                                ex.getHttpMethod(), ex.getRequestURL());

                logger.error("Endpoint not found: {}", message);

                ResponseDTO<Void> responseDTO = ResponseDTO.error(message, ErrorCodes.NOT_FOUND);
                responseDTO.setTimestamp(LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(responseDTO);
        }

        /**
         * Handles reservation conflict exceptions, such as overlapping reservations
         * or invalid reservation times.
         *
         * @param ex The ReservationConflictException that was thrown
         * @return ResponseEntity containing conflict error with HTTP 409 status
         */
        @ExceptionHandler(ReservationConflictException.class)
        public ResponseEntity<ResponseDTO<Void>> handleReservationConflictException(ReservationConflictException ex) {
                logger.error("Reservation conflict: {}", ex.getMessage());

                ResponseDTO<Void> responseDTO = ResponseDTO.error(ex.getMessage(), ErrorCodes.RESERVATION_CONFLICT);
                responseDTO.setTimestamp(LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(responseDTO);
        }

        /**
         * Handles restaurant capacity exceptions when a restaurant cannot
         * accommodate a reservation due to capacity constraints.
         *
         * @param ex The RestaurantCapacityException that was thrown
         * @return ResponseEntity containing capacity error with HTTP 409 status
         */
        @ExceptionHandler(RestaurantCapacityException.class)
        public ResponseEntity<ResponseDTO<Void>> handleRestaurantCapacityException(RestaurantCapacityException ex) {
                logger.error("Restaurant capacity issue: {}", ex.getMessage());

                ResponseDTO<Void> responseDTO = ResponseDTO.error(ex.getMessage(), ErrorCodes.RESTAURANT_FULLY_BOOKED);
                responseDTO.setTimestamp(LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(responseDTO);
        }

        /**
         * Handles base application exceptions that don't have specific handlers.
         * Provides a generic error response for unexpected application errors.
         *
         * @param ex The BaseException that was thrown
         * @return ResponseEntity containing application error with HTTP 500 status
         */
        @ExceptionHandler(BaseException.class)
        public ResponseEntity<ResponseDTO<Void>> handleBaseException(BaseException ex) {
                logger.error("Application error: {} with error code: {}",
                                ex.getMessage(), ex.getErrorCode(), ex);

                ResponseDTO<Void> responseDTO = ResponseDTO.error(ex.getMessage(), ex.getErrorCode());
                responseDTO.setTimestamp(LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(responseDTO);
        }

        /**
         * Handles any unhandled exceptions that occur during request processing.
         * Provides a generic error message and logs the full exception details
         * for debugging purposes.
         *
         * @param ex The Exception that was thrown
         * @return ResponseEntity containing generic error with HTTP 500 status
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ResponseDTO<Void>> handleGenericException(Exception ex) {
                logger.error("Unhandled exception: {}", ex.getMessage(), ex);

                String message = "An unexpected error occurred while processing your request. " +
                                "Our technical team has been notified and is working to resolve the issue.";

                ResponseDTO<Void> responseDTO = ResponseDTO.error(message, ErrorCodes.GENERAL_ERROR);
                responseDTO.setTimestamp(LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(responseDTO);
        }
}