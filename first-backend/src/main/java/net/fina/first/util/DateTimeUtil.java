package net.fina.first.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
