package net.fina.first.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class StringUtils {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern NON_ASCII_PATTERN = Pattern.compile("[^\\p{ASCII}]");

    private StringUtils() {
        // Utility class
    }

    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String trim(String str) {
        return str != null ? str.trim() : null;
    }

    public static String trimToNull(String str) {
        String trimmed = trim(str);
        return isBlank(trimmed) ? null : trimmed;
    }

    public static String trimToEmpty(String str) {
        return str != null ? str.trim() : "";
    }

    public static String normalize(String str) {
        if (str == null) {
            return null;
        }
        return WHITESPACE_PATTERN.matcher(str.trim()).replaceAll(" ");
    }

    public static String toUpperCase(String str) {
        return str != null ? str.toUpperCase(Locale.ROOT) : null;
    }

    public static String toLowerCase(String str) {
        return str != null ? str.toLowerCase(Locale.ROOT) : null;
    }

    public static String capitalize(String str) {
        if (isBlank(str)) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase(Locale.ROOT);
    }

    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }

    public static String truncateWithEllipsis(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    public static String removeAccents(String str) {
        if (str == null) {
            return null;
        }
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        return NON_ASCII_PATTERN.matcher(normalized).replaceAll("");
    }

    public static String slugify(String str) {
        if (str == null) {
            return null;
        }
        String normalized = removeAccents(str.toLowerCase(Locale.ROOT));
        return normalized.replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    public static String maskEmail(String email) {
        if (isBlank(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "***" + email.substring(atIndex);
    }

    public static String maskPhone(String phone) {
        if (isBlank(phone) || phone.length() < 6) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 3);
    }

    public static boolean containsIgnoreCase(String str, String search) {
        if (str == null || search == null) {
            return false;
        }
        return str.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
    }
}
