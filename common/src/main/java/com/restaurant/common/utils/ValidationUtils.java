package com.restaurant.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.restaurant.common.exceptions.ValidationException;

public class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^\\+?[0-9]{10,15}$");
    
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, "Field cannot be null");
        }
    }
    
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, "Field cannot be empty");
        }
    }
    
    public static void validateEmail(String email, String fieldName) {
        validateNotEmpty(email, fieldName);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(fieldName, "Invalid email format");
        }
    }
    
    public static void validatePhone(String phone, String fieldName) {
        validateNotEmpty(phone, fieldName);
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException(fieldName, "Invalid phone number format");
        }
    }
    
    public static void validateMinLength(String value, int minLength, String fieldName) {
        validateNotNull(value, fieldName);
        if (value.length() < minLength) {
            throw new ValidationException(fieldName, "Field must be at least " + minLength + " characters long");
        }
    }
    
    public static void validateMaxLength(String value, int maxLength, String fieldName) {
        validateNotNull(value, fieldName);
        if (value.length() > maxLength) {
            throw new ValidationException(fieldName, "Field must be at most " + maxLength + " characters long");
        }
    }
    
    public static void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new ValidationException(fieldName, "Field must be between " + min + " and " + max);
        }
    }
    
    public static Map<String, String> validate(Validatable object) {
        Map<String, String> errors = new HashMap<>();
        object.validate(errors);
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
        return errors;
    }
    
    // Interface for validatable objects
    public interface Validatable {
        void validate(Map<String, String> errors);
    }
    
    // Private constructor to prevent instantiation
    private ValidationUtils() {}
}