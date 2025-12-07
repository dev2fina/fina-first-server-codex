package net.fina.first.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Status of workflow actions.
 */
@Getter
@RequiredArgsConstructor
public enum ActionStatus {

    INITIATED("Initiated", "Action has been initiated"),
    IN_PROGRESS("In Progress", "Action is being processed"),
    PENDING_APPROVAL("Pending Approval", "Waiting for approval"),
    APPROVED("Approved", "Action has been approved"),
    REJECTED("Rejected", "Action has been rejected"),
    COMPLETED("Completed", "Action has been completed"),
    CANCELLED("Cancelled", "Action has been cancelled");

    private final String displayName;
    private final String description;
}
