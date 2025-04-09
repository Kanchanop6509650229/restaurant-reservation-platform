package com.restaurant.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for handling date and time operations.
 * Provides methods for formatting, parsing, and comparing dates and times.
 * 
 * @author Restaurant Team
 * @version 1.0
 */
public class DateTimeUtils {
    
    /** Formatter for date strings in yyyy-MM-dd format */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /** Formatter for time strings in HH:mm format */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    /** Formatter for date-time strings in yyyy-MM-dd HH:mm:ss format */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Formats a LocalDate object into a string using the DATE_FORMATTER pattern.
     *
     * @param date The date to format
     * @return Formatted date string or null if input is null
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }
    
    /**
     * Formats a LocalTime object into a string using the TIME_FORMATTER pattern.
     *
     * @param time The time to format
     * @return Formatted time string or null if input is null
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : null;
    }
    
    /**
     * Formats a LocalDateTime object into a string using the DATETIME_FORMATTER pattern.
     *
     * @param dateTime The date-time to format
     * @return Formatted date-time string or null if input is null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }
    
    /**
     * Parses a date string into a LocalDate object using the DATE_FORMATTER pattern.
     *
     * @param dateString The date string to parse
     * @return Parsed LocalDate or null if input is null
     */
    public static LocalDate parseDate(String dateString) {
        return dateString != null ? LocalDate.parse(dateString, DATE_FORMATTER) : null;
    }
    
    /**
     * Parses a time string into a LocalTime object using the TIME_FORMATTER pattern.
     *
     * @param timeString The time string to parse
     * @return Parsed LocalTime or null if input is null
     */
    public static LocalTime parseTime(String timeString) {
        return timeString != null ? LocalTime.parse(timeString, TIME_FORMATTER) : null;
    }
    
    /**
     * Parses a date-time string into a LocalDateTime object using the DATETIME_FORMATTER pattern.
     *
     * @param dateTimeString The date-time string to parse
     * @return Parsed LocalDateTime or null if input is null
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, DATETIME_FORMATTER) : null;
    }
    
    /**
     * Gets the current date and time, truncated to seconds.
     *
     * @return Current date and time
     */
    public static LocalDateTime now() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
    
    /**
     * Gets the current date.
     *
     * @return Current date
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * Checks if a time falls within a specified range.
     * Handles cases where the range spans midnight.
     *
     * @param time The time to check
     * @param startTime The start of the range
     * @param endTime The end of the range
     * @return true if the time is within the range, false otherwise
     */
    public static boolean isTimeInRange(LocalTime time, LocalTime startTime, LocalTime endTime) {
        if (time == null || startTime == null || endTime == null) {
            return false;
        }
        
        if (startTime.isAfter(endTime)) { // Range spans midnight
            return !time.isAfter(endTime) || !time.isBefore(startTime);
        } else {
            return !time.isBefore(startTime) && !time.isAfter(endTime);
        }
    }
    
    /**
     * Calculates the number of minutes between two date-time values.
     *
     * @param start The start date-time
     * @param end The end date-time
     * @return Number of minutes between the two date-times
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DateTimeUtils() {}
}