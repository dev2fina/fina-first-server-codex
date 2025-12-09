package net.fina.ecm.util;

import org.apache.jackrabbit.util.ISO9075;

public class AlfrescoUtil {
    public static String getISO9075String(String value) {
        return ISO9075.encode(value);
    }
}
