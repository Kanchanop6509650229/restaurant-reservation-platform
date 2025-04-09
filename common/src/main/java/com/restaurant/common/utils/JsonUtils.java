package com.restaurant.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.restaurant.common.exceptions.BaseException;

/**
 * Utility class for JSON serialization and deserialization.
 * Provides methods for converting objects to/from JSON and between different object types.
 * Uses Jackson ObjectMapper with Java 8 date/time support.
 * 
 * @author Restaurant Team
 * @version 1.0
 */
public class JsonUtils {
    /** Jackson ObjectMapper instance configured for Java 8 date/time support */
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    
    /**
     * Converts an object to its JSON string representation.
     *
     * @param object The object to convert
     * @return JSON string representation of the object
     * @throws BaseException if conversion fails
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new BaseException("Error converting object to JSON", e);
        }
    }
    
    /**
     * Converts a JSON string to an object of the specified type.
     *
     * @param <T> The type of object to convert to
     * @param json The JSON string to convert
     * @param valueType The class of the target type
     * @return Object of the specified type
     * @throws BaseException if conversion fails
     */
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            throw new BaseException("Error converting JSON to object", e);
        }
    }
    
    /**
     * Converts an object to another type using Jackson's conversion capabilities.
     *
     * @param <T> The target type to convert to
     * @param object The object to convert
     * @param targetType The class of the target type
     * @return Object converted to the target type
     * @throws BaseException if conversion fails
     */
    public static <T> T convertObject(Object object, Class<T> targetType) {
        try {
            return objectMapper.convertValue(object, targetType);
        } catch (Exception e) {
            throw new BaseException("Error converting object to target type", e);
        }
    }
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private JsonUtils() {}
}