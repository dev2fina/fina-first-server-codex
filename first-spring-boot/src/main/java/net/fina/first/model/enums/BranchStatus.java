package net.fina.first.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Status of FI branches.
 */
@Getter
@RequiredArgsConstructor
public enum BranchStatus {

    ACTIVE("Active", "Branch is currently active"),
    INACTIVE("Inactive", "Branch is inactive"),
    PENDING("Pending", "Branch registration pending"),
    CLOSED("Closed", "Branch has been closed"),
    SUSPENDED("Suspended", "Branch operations suspended");

    private final String displayName;
    private final String description;
}
