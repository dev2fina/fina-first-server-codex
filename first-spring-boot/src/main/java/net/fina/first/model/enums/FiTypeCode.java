package net.fina.first.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of Financial Institution type codes.
 * Based on the FIRST module documentation defining 14 FI types.
 */
@Getter
@RequiredArgsConstructor
public enum FiTypeCode {

    FEX("FEX", "Foreign Exchange Bureau"),
    LE("LE", "Leasing Company"),
    MFO("MFO", "Microfinance Organization"),
    CU("CU", "Credit Union"),
    PSP("PSP", "Payment Service Provider"),
    VASP("VASP", "Virtual Asset Service Provider"),
    BANK("BANK", "Commercial Bank"),
    MICROBANK("MICROBANK", "Microbank"),
    AMC("AMC", "Asset Management Company"),
    IF("IF", "Investment Fund"),
    BC("BC", "Brokerage Company"),
    SE("SE", "Stock Exchange"),
    SR("SR", "Securities Registrar"),
    CD("CD", "Central Depository");

    private final String code;
    private final String description;

    public static FiTypeCode fromCode(String code) {
        for (FiTypeCode type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown FI type code: " + code);
    }
}
