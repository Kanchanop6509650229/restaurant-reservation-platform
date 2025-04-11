package com.restaurant.common.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * Utility class for handling date and time operations.
 * Provides methods for formatting, parsing, and comparing dates and times.
 *
 * This class includes:
 * - Date and time formatting with various patterns
 * - Parsing methods with error handling
 * - Time range calculations and validations
 * - Date manipulation utilities
 * - Conversion between different date/time types
 *
 * @author Restaurant Team
 * @version 1.1
 */
public class DateTimeUtils {

    /** Formatter for date strings in yyyy-MM-dd format */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Formatter for time strings in HH:mm format */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /** Formatter for date-time strings in yyyy-MM-dd HH:mm:ss format */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Formatter for ISO date-time strings */
    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /** Formatter for displaying dates in a user-friendly format (e.g., "January 1, 2023") */
    private static final DateTimeFormatter FRIENDLY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    /** Formatter for displaying times in a user-friendly format (e.g., "1:30 PM") */
    private static final DateTimeFormatter FRIENDLY_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    /** Default zone ID for date/time operations */
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

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
     * Formats a LocalDate object into a user-friendly string (e.g., "January 1, 2023").
     *
     * @param date The date to format
     * @return Formatted date string or null if input is null
     */
    public static String formatDateFriendly(LocalDate date) {
        return date != null ? date.format(FRIENDLY_DATE_FORMATTER) : null;
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
     * Formats a LocalTime object into a user-friendly string (e.g., "1:30 PM").
     *
     * @param time The time to format
     * @return Formatted time string or null if input is null
     */
    public static String formatTimeFriendly(LocalTime time) {
        return time != null ? time.format(FRIENDLY_TIME_FORMATTER) : null;
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
     * Formats a LocalDateTime object into an ISO-8601 string.
     *
     * @param dateTime The date-time to format
     * @return Formatted ISO date-time string or null if input is null
     */
    public static String formatDateTimeISO(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ISO_DATETIME_FORMATTER) : null;
    }

    /**
     * Formats a LocalDateTime object into a user-friendly string (e.g., "January 1, 2023 at 1:30 PM").
     *
     * @param dateTime The date-time to format
     * @return Formatted user-friendly date-time string or null if input is null
     */
    public static String formatDateTimeFriendly(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(FRIENDLY_DATE_FORMATTER) + " at " + dateTime.format(FRIENDLY_TIME_FORMATTER);
    }

    /**
     * Parses a date string into a LocalDate object using the DATE_FORMATTER pattern.
     *
     * @param dateString The date string to parse
     * @return Parsed LocalDate or null if input is null or invalid
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a time string into a LocalTime object using the TIME_FORMATTER pattern.
     *
     * @param timeString The time string to parse
     * @return Parsed LocalTime or null if input is null or invalid
     */
    public static LocalTime parseTime(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(timeString.trim(), TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a date-time string into a LocalDateTime object using the DATETIME_FORMATTER pattern.
     *
     * @param dateTimeString The date-time string to parse
     * @return Parsed LocalDateTime or null if input is null or invalid
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString.trim(), DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses an ISO-8601 date-time string into a LocalDateTime object.
     *
     * @param isoDateTimeString The ISO date-time string to parse
     * @return Parsed LocalDateTime or null if input is null or invalid
     */
    public static LocalDateTime parseDateTimeISO(String isoDateTimeString) {
        if (isoDateTimeString == null || isoDateTimeString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(isoDateTimeString.trim(), ISO_DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
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
     * @return Number of minutes between the two date-times, or 0 if either input is null
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Calculates the number of days between two date values.
     *
     * @param start The start date
     * @param end The end date
     * @return Number of days between the two dates, or 0 if either input is null
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Converts a java.util.Date to LocalDateTime.
     *
     * @param date The Date to convert
     * @return Converted LocalDateTime or null if input is null
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    /**
     * Converts a LocalDateTime to java.util.Date.
     *
     * @param dateTime The LocalDateTime to convert
     * @return Converted Date or null if input is null
     */
    public static Date toDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Date.from(dateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * Gets the start of the day (00:00:00) for a given date.
     *
     * @param date The date
     * @return LocalDateTime representing the start of the day
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }

    /**
     * Gets the end of the day (23:59:59.999999999) for a given date.
     *
     * @param date The date
     * @return LocalDateTime representing the end of the day
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(LocalTime.MAX);
    }

    /**
     * Gets the next occurrence of a specific day of the week from a given date.
     *
     * @param date The starting date
     * @param dayOfWeek The day of week to find
     * @return The date of the next occurrence of the specified day of week
     */
    public static LocalDate nextDayOfWeek(LocalDate date, DayOfWeek dayOfWeek) {
        if (date == null || dayOfWeek == null) {
            return null;
        }
        return date.with(TemporalAdjusters.next(dayOfWeek));
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DateTimeUtils() {}
}