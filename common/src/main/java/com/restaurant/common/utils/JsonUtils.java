package com.restaurant.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.restaurant.common.exceptions.BaseException;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new BaseException("Error converting object to JSON", e);
        }
    }
    
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            throw new BaseException("Error converting JSON to object", e);
        }
    }
    
    public static <T> T convertObject(Object object, Class<T> targetType) {
        try {
            return objectMapper.convertValue(object, targetType);
        } catch (Exception e) {
            throw new BaseException("Error converting object to target type", e);
        }
    }
    
    // Private constructor to prevent instantiation
    private JsonUtils() {}
}