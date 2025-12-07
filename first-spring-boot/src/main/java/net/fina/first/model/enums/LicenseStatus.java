package net.fina.first.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * License status for Financial Institutions.
 */
@Getter
@RequiredArgsConstructor
public enum LicenseStatus {

    ACTIVE("Active", "License is currently active"),
    INACTIVE("Inactive", "License is inactive"),
    SUSPENDED("Suspended", "License has been suspended"),
    REVOKED("Revoked", "License has been revoked"),
    EXPIRED("Expired", "License has expired"),
    PENDING("Pending", "License application is pending");

    private final String displayName;
    private final String description;
}
