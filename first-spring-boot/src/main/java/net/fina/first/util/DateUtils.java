package net.fina.first.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateUtils {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DISPLAY_DATETIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private DateUtils() {
        // Utility class
    }

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DISPLAY_DATE) : null;
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_DATETIME) : null;
    }

    public static String formatIsoDate(LocalDate date) {
        return date != null ? date.format(ISO_DATE) : null;
    }

    public static String formatIsoDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ISO_DATETIME) : null;
    }

    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        return LocalDate.parse(dateString, ISO_DATE);
    }

    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, ISO_DATETIME);
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static boolean isExpired(LocalDate expirationDate) {
        return expirationDate != null && expirationDate.isBefore(today());
    }

    public static boolean isExpiringSoon(LocalDate expirationDate, int daysThreshold) {
        if (expirationDate == null) {
            return false;
        }
        LocalDate thresholdDate = today().plusDays(daysThreshold);
        return expirationDate.isAfter(today()) && expirationDate.isBefore(thresholdDate);
    }

    public static LocalDate startOfYear(int year) {
        return LocalDate.of(year, 1, 1);
    }

    public static LocalDate endOfYear(int year) {
        return LocalDate.of(year, 12, 31);
    }

    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }
}
