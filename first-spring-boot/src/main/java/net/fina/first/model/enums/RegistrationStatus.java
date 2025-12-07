package net.fina.first.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Status of FI registration process.
 */
@Getter
@RequiredArgsConstructor
public enum RegistrationStatus {

    DRAFT("Draft", "Registration started but not submitted"),
    PENDING_REVIEW("Pending Review", "Submitted and awaiting review"),
    UNDER_REVIEW("Under Review", "Currently being reviewed"),
    PENDING_DOCUMENTS("Pending Documents", "Waiting for additional documents"),
    APPROVED("Approved", "Registration approved"),
    REJECTED("Rejected", "Registration rejected"),
    CANCELLED("Cancelled", "Registration cancelled"),
    SUSPENDED("Suspended", "Registration suspended"),
    ACTIVE("Active", "FI is active and licensed"),
    INACTIVE("Inactive", "FI is inactive");

    private final String displayName;
    private final String description;
}
