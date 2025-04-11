package com.restaurant.common.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import com.restaurant.common.exceptions.ValidationException;

/**
 * Utility class for validating various types of data.
 * Provides methods for common validation tasks such as null checks,
 * string validation, email validation, and more.
 *
 * This class includes:
 * - Basic validation (null checks, empty checks)
 * - Format validation (email, phone, UUID)
 * - Length validation (min/max length)
 * - Range validation for numeric values
 * - Collection validation
 * - Support for custom validation through the Validatable interface
 *
 * @author Restaurant Team
 * @version 1.1
 */
public class ValidationUtils {

    /** Regular expression pattern for validating email addresses */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /** Regular expression pattern for validating phone numbers */
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");

    /** Regular expression pattern for validating UUIDs */
    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

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
        // Skip validation if email is null or empty
        if (email == null || email.trim().isEmpty()) {
            return;
        }

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
        // Skip validation if phone is null or empty
        if (phone == null || phone.trim().isEmpty()) {
            return;
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException(fieldName, "Invalid phone number format. Expected format: +1 555-123-4567");
        }
    }

    /**
     * Validates that a string is a valid UUID.
     *
     * @param uuid The UUID string to validate
     * @param fieldName The name of the field being validated
     * @throws ValidationException if the UUID is invalid
     */
    public static void validateUUID(String uuid, String fieldName) {
        validateNotEmpty(uuid, fieldName);

        if (!UUID_PATTERN.matcher(uuid).matches()) {
            throw new ValidationException(fieldName, "Invalid UUID format");
        }

        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(fieldName, "Invalid UUID format");
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
     * Validates that a collection is not null or empty.
     *
     * @param collection The collection to validate
     * @param fieldName The name of the field being validated
     * @throws ValidationException if the collection is null or empty
     */
    public static void validateNotEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException(fieldName, "Collection cannot be empty");
        }
    }

    /**
     * Validates that a collection does not exceed a maximum size.
     *
     * @param collection The collection to validate
     * @param maxSize The maximum allowed size
     * @param fieldName The name of the field being validated
     * @throws ValidationException if the collection exceeds the maximum size
     */
    public static void validateMaxSize(Collection<?> collection, int maxSize, String fieldName) {
        if (collection != null && collection.size() > maxSize) {
            throw new ValidationException(fieldName, "Collection size must not exceed " + maxSize);
        }
    }

    /**
     * Validates that a string matches a regular expression pattern.
     *
     * @param value The string to validate
     * @param pattern The regular expression pattern
     * @param fieldName The name of the field being validated
     * @param errorMessage The error message to use if validation fails
     * @throws ValidationException if the string doesn't match the pattern
     */
    public static void validatePattern(String value, Pattern pattern, String fieldName, String errorMessage) {
        if (value != null && !pattern.matcher(value).matches()) {
            throw new ValidationException(fieldName, errorMessage);
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