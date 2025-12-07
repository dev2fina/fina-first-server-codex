package net.fina.server.returns.util;

import java.util.Collections;
import java.util.List;

public class ReturnTypeReservedCodesUtil {
    // Return Type Code for CREG //FINA-STAT//
    private final String TYPE_CREG = "CREG";

    private static ReturnTypeReservedCodesUtil instance;
    private List<String> reservedCodes;

    private ReturnTypeReservedCodesUtil() {
        reservedCodes = Collections.singletonList(TYPE_CREG);
    }

    public static ReturnTypeReservedCodesUtil getInstance() {
        if (instance == null) {
            instance = new ReturnTypeReservedCodesUtil();
        }
        return instance;
    }

    public List<String> getReservedCodes() {
        return reservedCodes;
    }

    public boolean isCregCode(String typeCode) {
        return reservedCodes.contains(typeCode.trim().toUpperCase());
    }
}
