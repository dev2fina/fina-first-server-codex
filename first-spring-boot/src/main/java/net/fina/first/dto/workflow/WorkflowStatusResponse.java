package net.fina.first.dto.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.ControllerDecision;
import net.fina.first.model.enums.RegistrationStatus;
import net.fina.first.model.enums.WorkflowPhase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for workflow status information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStatusResponse {

    private Long registrationId;
    private String code;
    private String firmName;
    private String fiTypeCode;
    private String fiTypeName;

    // === Current Status ===
    private WorkflowPhase currentPhase;
    private String currentPhaseDisplayName;
    private RegistrationStatus registrationStatus;
    private boolean isEditableByFi;
    private boolean requiresControllerAction;
    private boolean isRegistered;

    // === Phase Completion Status ===
    private boolean phase1Completed;
    private boolean phase2Completed;
    private boolean dataEntryCompleted;
    private boolean questionnaireCompleted;

    private LocalDateTime phase1CompletedAt;
    private LocalDateTime phase2CompletedAt;
    private LocalDateTime dataEntryCompletedAt;
    private LocalDateTime questionnaireCompletedAt;

    // === Data Entry Status ===
    private long administratorCount;
    private long beneficiaryCount;
    private long branchCount;
    private long licenseCount;
    private boolean hasHeadOfficeBranch;
    private boolean requiresLicense;

    // === Controller Review Status ===
    private LocalDateTime submittedToControllerAt;
    private String submittedToControllerBy;
    private String assignedController;
    private LocalDateTime controllerReviewedAt;
    private String controllerReviewedBy;
    private ControllerDecision controllerDecision;
    private String controllerDecisionComment;

    // === Correction Status ===
    private LocalDateTime correctionRequestedAt;
    private String correctionReason;
    private LocalDate correctionDeadline;
    private Integer correctionCount;

    // === Final Registration ===
    private LocalDateTime registeredAt;
    private String registeredBy;
    private String registrationNumber;

    // === Validation Errors ===
    private List<ValidationError> validationErrors;
    private boolean canProceedToNextPhase;

    // === Available Actions ===
    private List<String> availableActions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
        private String severity; // ERROR, WARNING
    }
}
