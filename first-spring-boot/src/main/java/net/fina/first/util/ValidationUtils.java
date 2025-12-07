package net.fina.first.util;

import net.fina.first.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9]{9,15}$");
    private static final Pattern IDENTITY_PATTERN = Pattern.compile(
            "^[A-Z0-9]{6,20}$");

    private ValidationUtils() {
        // Utility class
    }

    public static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " is required");
        }
    }

    public static void requireNotBlank(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            throw new ValidationException(fieldName + " is required and cannot be blank");
        }
    }

    public static void requireNotEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException(fieldName + " is required and cannot be empty");
        }
    }

    public static void requireNotEmpty(Map<?, ?> map, String fieldName) {
        if (map == null || map.isEmpty()) {
            throw new ValidationException(fieldName + " is required and cannot be empty");
        }
    }

    public static void requireLength(String value, int minLength, int maxLength, String fieldName) {
        if (value == null) {
            return;
        }
        if (value.length() < minLength || value.length() > maxLength) {
            throw new ValidationException(
                    fieldName + " must be between " + minLength + " and " + maxLength + " characters");
        }
    }

    public static void requireMinLength(String value, int minLength, String fieldName) {
        if (value != null && value.length() < minLength) {
            throw new ValidationException(
                    fieldName + " must be at least " + minLength + " characters");
        }
    }

    public static void requireMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new ValidationException(
                    fieldName + " must not exceed " + maxLength + " characters");
        }
    }

    public static void requirePositive(Number value, String fieldName) {
        if (value == null) {
            return;
        }
        if (value.doubleValue() <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }

    public static void requireNonNegative(Number value, String fieldName) {
        if (value == null) {
            return;
        }
        if (value.doubleValue() < 0) {
            throw new ValidationException(fieldName + " must not be negative");
        }
    }

    public static void requireRange(Number value, Number min, Number max, String fieldName) {
        if (value == null) {
            return;
        }
        double val = value.doubleValue();
        if (val < min.doubleValue() || val > max.doubleValue()) {
            throw new ValidationException(
                    fieldName + " must be between " + min + " and " + max);
        }
    }

    public static void requireValidEmail(String email, String fieldName) {
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(fieldName + " must be a valid email address");
        }
    }

    public static void requireValidPhone(String phone, String fieldName) {
        if (phone != null) {
            String cleaned = phone.replaceAll("[\\s()-]", "");
            if (!PHONE_PATTERN.matcher(cleaned).matches()) {
                throw new ValidationException(fieldName + " must be a valid phone number");
            }
        }
    }

    public static void requireValidIdentity(String identity, String fieldName) {
        if (identity != null && !IDENTITY_PATTERN.matcher(identity).matches()) {
            throw new ValidationException(
                    fieldName + " must contain only uppercase letters and numbers (6-20 characters)");
        }
    }

    public static void requirePercentage(BigDecimal value, String fieldName) {
        if (value != null) {
            if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new ValidationException(fieldName + " must be between 0 and 100");
            }
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        String cleaned = phone.replaceAll("[\\s()-]", "");
        return PHONE_PATTERN.matcher(cleaned).matches();
    }
}
