package net.fina.server.dashboard.legacy.proxy;

import java.util.Calendar;
import java.util.Date;

public class LegacyDateCalculationsUtil {
    private static final String PERIOD_TYPE_MONTH = "M";

    public static Date getFirstDayOfPeriodFromString(String periodString, String periodType) {
        String[] parts;
        int year;
        int month;

        if (periodType.equals(PERIOD_TYPE_MONTH)) {
            parts = periodString.split("-");
            year = Integer.parseInt(parts[1].trim());
            month = Integer.parseInt(parts[0].trim()) - 1;
        } else {
            parts = periodString.split(" ");
            year = Integer.parseInt(parts[1].trim());
            int quarterIndex = Integer.parseInt(parts[0].replaceAll("[Qq]", "").trim());
            month = quarterIndex * 3 - 1;
        }

        if (month > 11 || month < 0) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR, c.getActualMinimum(Calendar.HOUR));
        c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE));
        c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND));
        c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));

        return c.getTime();
    }

    public static Date addDays(Date date, int numDays) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.DATE, numDays);

        return cal.getTime();
    }

    public static Date addHours(Date date, int numOfHours) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.HOUR, numOfHours);

        return cal.getTime();
    }

    public static Date addMinutes(Date date, int numOfMinutes) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.MINUTE, numOfMinutes);

        return cal.getTime();
    }

    public static Date addMonths(Date date, int numMonths) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.MONTH, numMonths);

        return cal.getTime();
    }
}
