package net.fina.first.service.workflow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.dto.workflow.WorkflowStatusResponse.ValidationError;
import net.fina.first.model.FiRegistry;
import net.fina.first.model.enums.WorkflowPhase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for validating workflow phase requirements.
 * Checks if all required data is present before allowing phase transitions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowValidationService {

    /**
     * Validate Phase 1 completion requirements.
     */
    public List<ValidationError> validatePhase1(FiRegistry registry) {
        List<ValidationError> errors = new ArrayList<>();

        if (registry.getFiType() == null) {
            errors.add(error("fiType", "FI Type is required"));
        }

        if (isBlank(registry.getFirmName())) {
            errors.add(error("firmName", "Firm name is required"));
        }

        if (registry.getLegalForm() == null) {
            errors.add(error("legalForm", "Legal form is required"));
        }

        return errors;
    }

    /**
     * Validate Phase 2 completion requirements.
     */
    public List<ValidationError> validatePhase2(FiRegistry registry) {
        List<ValidationError> errors = new ArrayList<>();

        // First validate Phase 1 is complete
        errors.addAll(validatePhase1(registry));

        // Phase 2 specific validations
        if (registry.getLegalAddressRegion() == null) {
            errors.add(error("legalAddressRegion", "Legal address region is required"));
        }

        if (isBlank(registry.getLegalAddressCity())) {
            errors.add(error("legalAddressCity", "Legal address city is required"));
        }

        if (isBlank(registry.getLegalAddress())) {
            errors.add(error("legalAddress", "Legal address is required"));
        }

        if (isBlank(registry.getPhone())) {
            errors.add(error("phone", "Phone number is required"));
        }

        if (isBlank(registry.getEmail())) {
            errors.add(error("email", "Email is required"));
        }

        if (isBlank(registry.getContactPersonFullName())) {
            errors.add(error("contactPersonFullName", "Contact person name is required"));
        }

        if (isBlank(registry.getContactPersonPhone())) {
            errors.add(error("contactPersonPhone", "Contact person phone is required"));
        }

        return errors;
    }

    /**
     * Validate Data Entry (Phase 3) completion requirements.
     */
    public List<ValidationError> validateDataEntry(FiRegistry registry) {
        List<ValidationError> errors = new ArrayList<>();

        // First validate Phase 2 is complete
        errors.addAll(validatePhase2(registry));

        // Must have at least one administrator
        if (registry.getActiveAdministratorCount() < 1) {
            errors.add(error("administrators", "At least one administrator is required"));
        }

        // Must have at least one beneficiary
        if (registry.getActiveBeneficiaryCount() < 1) {
            errors.add(error("beneficiaries", "At least one beneficiary is required"));
        }

        // Must have head office branch
        if (!registry.hasHeadOfficeBranch()) {
            errors.add(error("branches", "Head office branch is required"));
        }

        // PSP/VASP specific: must have license
        if (registry.requiresLicense() && registry.getActiveLicenseCount() < 1) {
            errors.add(error("licenses", "At least one license is required for PSP/VASP"));
        }

        return errors;
    }

    /**
     * Validate Questionnaire (Phase 4) completion requirements.
     */
    public List<ValidationError> validateQuestionnaire(FiRegistry registry) {
        List<ValidationError> errors = new ArrayList<>();

        // First validate Data Entry is complete
        errors.addAll(validateDataEntry(registry));

        // Check questionnaire completion through the current action
        if (registry.getActions() != null && !registry.getActions().isEmpty()) {
            var currentAction = registry.getActions().stream()
                    .filter(a -> !a.isFinalState())
                    .findFirst()
                    .orElse(null);

            if (currentAction != null && currentAction.getQuestionnaires() != null) {
                long unansweredObligatory = currentAction.getQuestionnaires().stream()
                        .filter(q -> q.isObligatory() && isBlank(q.getAnswer()))
                        .count();

                if (unansweredObligatory > 0) {
                    errors.add(error("questionnaires",
                            String.format("%d obligatory questions are not answered", unansweredObligatory)));
                }
            }
        }

        return errors;
    }

    /**
     * Validate submission to controller requirements.
     */
    public List<ValidationError> validateForSubmission(FiRegistry registry) {
        List<ValidationError> errors = new ArrayList<>();

        // All questionnaire validations must pass
        errors.addAll(validateQuestionnaire(registry));

        // Additional submission validations can be added here

        return errors;
    }

    /**
     * Validate that the registration can proceed to the next phase.
     */
    public List<ValidationError> validatePhaseTransition(FiRegistry registry, WorkflowPhase targetPhase) {
        return switch (targetPhase) {
            case PHASE2_GENERAL_INFO -> validatePhase1(registry);
            case PHASE3_DATA_ENTRY -> validatePhase2(registry);
            case PHASE4_QUESTIONNAIRE -> validateDataEntry(registry);
            case PHASE5_SUBMITTED -> validateQuestionnaire(registry);
            case PHASE6_CONTROLLER_REVIEW -> validateForSubmission(registry);
            default -> new ArrayList<>();
        };
    }

    /**
     * Check if the current phase can transition to the target phase.
     */
    public boolean canTransitionTo(FiRegistry registry, WorkflowPhase targetPhase) {
        List<ValidationError> errors = validatePhaseTransition(registry, targetPhase);
        return errors.isEmpty();
    }

    // === Helper Methods ===

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private ValidationError error(String field, String message) {
        return ValidationError.builder()
                .field(field)
                .message(message)
                .severity("ERROR")
                .build();
    }

    private ValidationError warning(String field, String message) {
        return ValidationError.builder()
                .field(field)
                .message(message)
                .severity("WARNING")
                .build();
    }
}
