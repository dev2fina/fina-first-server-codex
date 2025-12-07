package net.fina.common.client.util;

import java.util.Date;

public class SafeValueUtil {

    public static Double getValueSafe(Double value) {
        return value == null ? new Double(0) : value;
    }

    public static Integer getValueSafe(Integer value) {
        return value == null ? Integer.valueOf(0) : value;
    }

    public static String getValueSafe(String value) {
        return value == null ? "" : value;
    }

    public static Date getValueSafe(Date value) {
        return value == null ? new Date() : value;
    }

    public static byte[] getValueSafe(byte[] value) {
        return value == null ? new byte[0] : value;
    }

    public static String getTrimmedValueSafe(String value) {
        return getValueSafe(value).trim();
    }

}
