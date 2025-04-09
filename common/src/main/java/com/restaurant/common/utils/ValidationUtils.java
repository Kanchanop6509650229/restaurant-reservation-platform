package com.restaurant.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.restaurant.common.exceptions.ValidationException;

/**
 * Utility class for validating various types of data.
 * Provides methods for common validation tasks such as null checks,
 * string validation, email validation, and more.
 * 
 * @author Restaurant Team
 * @version 1.0
 */
public class ValidationUtils {
    
    /** Regular expression pattern for validating email addresses */
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    
    /** Regular expression pattern for validating phone numbers */
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^\\+?[0-9]{10,15}$");
    
    /**
     * Validates that a value is not null.
     *
     * @param value The value to validate
     * @param fieldName The name of the field being validated
     * @throws ValidationException if the value is null
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, "Field cannot be null");
        }
    }
    
    /**
     * Validates that a string is not null or empty.
     *
     * @param value The string to validate
     * @param fieldName The name of the field being validated
     * @throws ValidationException if the string is null or empty
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, "Field cannot be empty");
        }
    }
    
    /**
     * Validates that a string is a valid email address.
     *
     * @param email The email address to validate
     * @param fieldName The name of the field being validated
     * @throws ValidationException if the email is invalid
     */
    public static void validateEmail(String email, String fieldName) {
        validateNotEmpty(email, fieldName);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(fieldName, "Invalid email format");
        }
    }
    
    /**
     * Validates that a string is a valid phone number.
     *
     * @param phone The phone number to validate
     * @param fieldName The name of the field being validated
     * @throws ValidationException if the phone number is invalid
     */
    public static void validatePhone(String phone, String fieldName) {
        validateNotEmpty(phone, fieldName);
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException(fieldName, "Invalid phone number format");
        }
    }
    
    /**
     * Validates that a string meets a minimum length requirement.
     *
     * @param value The string to validate
     * @param minLength The minimum required length
     * @param fieldName The name of the field being validated
     * @throws ValidationException if the string is too short
     */
    public static void validateMinLength(String value, int minLength, String fieldName) {
        validateNotNull(value, fieldName);
        if (value.length() < minLength) {
            throw new ValidationException(fieldName, "Field must be at least " + minLength + " characters long");
        }
    }
    
    /**
     * Validates that a string meets a maximum length requirement.
     *
     * @param value The string to validate
     * @param maxLength The maximum allowed length
     * @param fieldName The name of the field being validated
     * @throws ValidationException if the string is too long
     */
    public static void validateMaxLength(String value, int maxLength, String fieldName) {
        validateNotNull(value, fieldName);
        if (value.length() > maxLength) {
            throw new ValidationException(fieldName, "Field must be at most " + maxLength + " characters long");
        }
    }
    
    /**
     * Validates that a number falls within a specified range.
     *
     * @param value The number to validate
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @param fieldName The name of the field being validated
     * @throws ValidationException if the number is outside the range
     */
    public static void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new ValidationException(fieldName, "Field must be between " + min + " and " + max);
        }
    }
    
    /**
     * Validates an object that implements the Validatable interface.
     *
     * @param object The object to validate
     * @return Map of validation errors, empty if validation passes
     * @throws ValidationException if validation fails
     */
    public static Map<String, String> validate(Validatable object) {
        Map<String, String> errors = new HashMap<>();
        object.validate(errors);
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
        return errors;
    }
    
    /**
     * Interface for objects that can be validated.
     * Implementing classes should perform their validation logic
     * and add any errors to the provided map.
     */
    public interface Validatable {
        /**
         * Performs validation on the implementing object.
         *
         * @param errors Map to store validation errors
         */
        void validate(Map<String, String> errors);
    }
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ValidationUtils() {}
}