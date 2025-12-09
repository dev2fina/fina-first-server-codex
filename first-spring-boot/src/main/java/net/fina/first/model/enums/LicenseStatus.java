package net.fina.first.model.enums;

import lombok.Getter;

/**
 * License status for Financial Institutions.
 */
@Getter
public enum LicenseStatus {

    ACTIVE("Active", "License is currently active"),
    INACTIVE("Inactive", "License is inactive"),
    SUSPENDED("Suspended", "License has been suspended"),
    REVOKED("Revoked", "License has been revoked"),
    EXPIRED("Expired", "License has expired"),
    PENDING("Pending", "License application is pending");

    private final String displayName;
    private final String description;

    LicenseStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
