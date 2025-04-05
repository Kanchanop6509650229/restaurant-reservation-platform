package com.restaurant.reservation.exception;

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

@ControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(responseDTO);
        }

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

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(responseDTO);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ResponseDTO<Void>> handleAuthenticationException(AuthenticationException ex,
                        HttpServletRequest request) {
                logger.error("Authentication failed: {}", ex.getMessage());

                ResponseDTO<Void> responseDTO = ResponseDTO.error(ex.getMessage(), ex.getErrorCode());
                responseDTO.setRequestId(RequestIdFilter.getCurrentRequestId(request));

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(responseDTO);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ResponseDTO<Void>> handleAccessDeniedException(AccessDeniedException ex) {
                String message = "You don't have permission to perform this operation. Please contact an administrator if you require access.";
                logger.error("Access denied: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ResponseDTO.error(message, ErrorCodes.FORBIDDEN));
        }

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

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(responseDTO);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ResponseDTO<Void>> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex) {

                String message = String.format(
                                "The parameter '%s' has an invalid value: '%s'. Please provide a valid %s value.",
                                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

                logger.error("Type mismatch: {}", message);

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ResponseDTO.error(message, ErrorCodes.VALIDATION_ERROR));
        }

        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ResponseDTO<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
                String message = String.format(
                                "The requested endpoint '%s %s' does not exist. Please check the URL and HTTP method.",
                                ex.getHttpMethod(), ex.getRequestURL());

                logger.error("Endpoint not found: {}", message);

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ResponseDTO.error(message, ErrorCodes.NOT_FOUND));
        }

        @ExceptionHandler(ReservationConflictException.class)
        public ResponseEntity<ResponseDTO<Void>> handleReservationConflictException(ReservationConflictException ex) {
                logger.error("Reservation conflict: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ResponseDTO.error(ex.getMessage(), ErrorCodes.RESERVATION_CONFLICT));
        }

        @ExceptionHandler(RestaurantCapacityException.class)
        public ResponseEntity<ResponseDTO<Void>> handleRestaurantCapacityException(RestaurantCapacityException ex) {
                logger.error("Restaurant capacity issue: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ResponseDTO.error(ex.getMessage(), ErrorCodes.RESTAURANT_FULLY_BOOKED));
        }

        @ExceptionHandler(BaseException.class)
        public ResponseEntity<ResponseDTO<Void>> handleBaseException(BaseException ex) {
                logger.error("Application error: {} with error code: {}",
                                ex.getMessage(), ex.getErrorCode(), ex);

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ResponseDTO.error(ex.getMessage(), ex.getErrorCode()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ResponseDTO<Void>> handleGenericException(Exception ex) {
                logger.error("Unhandled exception: {}", ex.getMessage(), ex);

                String message = "An unexpected error occurred while processing your request. " +
                                "Our technical team has been notified and is working to resolve the issue.";

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ResponseDTO.error(message, ErrorCodes.GENERAL_ERROR));
        }
}