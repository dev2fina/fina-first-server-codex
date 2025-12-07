package net.fina.first.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Types of workflow actions on FI Registry.
 * Corresponds to legacy FiRegistryActionType.
 */
@Getter
@RequiredArgsConstructor
public enum ActionType {

    REGISTRATION("Registration", "New FI registration process"),
    CHANGE("Change", "Modification of FI details"),
    CANCELLATION("Cancellation", "FI registration cancellation"),
    BRANCHES_CHANGE("Branches Change", "Changes to branch network"),
    BRANCHES_EDIT("Branches Edit", "Edit branch details"),
    DOCUMENT_WITHDRAWAL("Document Withdrawal", "Withdrawal of submitted documents"),
    LICENSE_RENEWAL("License Renewal", "License renewal process"),
    LICENSE_SUSPENSION("License Suspension", "License suspension process"),
    LICENSE_REVOCATION("License Revocation", "License revocation process");

    private final String displayName;
    private final String description;
}
