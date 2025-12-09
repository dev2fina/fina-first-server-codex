package net.fina.first.model.enums;

import lombok.Getter;

/**
 * Workflow phases for FI Registration process.
 * Based on the FIRST + VASP + PSP Registration Workflow diagram.
 */
@Getter
public enum WorkflowPhase {

    PHASE1_INITIAL(1, "Phase 1 - Initial Fields", "Basic registration data entry"),
    PHASE2_GENERAL_INFO(2, "Phase 2 - General Info", "Company details, address, contact information"),
    PHASE3_DATA_ENTRY(3, "Phase 3 - Data Entry", "Managers, Beneficiaries, Branches, License (PSP/VASP)"),
    PHASE4_QUESTIONNAIRE(4, "Phase 4 - Questionnaire", "Complete required questionnaires"),
    PHASE5_SUBMITTED(5, "Phase 5 - Submitted", "Application submitted to Controller"),
    PHASE6_CONTROLLER_REVIEW(6, "Phase 6 - Controller Review", "Controller checking information"),
    PHASE7_CORRECTION(7, "Phase 7 - Correction", "Manager editing or requesting info from FI"),
    PHASE8_DOCUMENT_GENERATION(8, "Phase 8 - Document Generation", "Generating registration documents"),
    PHASE9_REGISTERED(9, "Phase 9 - Registered", "FI Registration completed");

    private final int order;
    private final String displayName;
    private final String description;

    WorkflowPhase(int order, String displayName, String description) {
        this.order = order;
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Check if this phase allows moving to the next phase.
     */
    public boolean canProgressTo(WorkflowPhase nextPhase) {
        // Special case: Correction can go back to Controller Review
        if (this == PHASE7_CORRECTION && nextPhase == PHASE6_CONTROLLER_REVIEW) {
            return true;
        }
        // Normal forward progression
        return nextPhase.order == this.order + 1;
    }

    /**
     * Get the next phase in normal progression.
     */
    public WorkflowPhase getNextPhase() {
        for (WorkflowPhase phase : values()) {
            if (phase.order == this.order + 1) {
                return phase;
            }
        }
        return null;
    }

    /**
     * Check if this is a terminal phase.
     */
    public boolean isTerminal() {
        return this == PHASE9_REGISTERED;
    }

    /**
     * Check if FI can edit data in this phase.
     */
    public boolean isFiEditable() {
        return this == PHASE1_INITIAL ||
               this == PHASE2_GENERAL_INFO ||
               this == PHASE3_DATA_ENTRY ||
               this == PHASE4_QUESTIONNAIRE ||
               this == PHASE7_CORRECTION;
    }

    /**
     * Check if Controller action is required.
     */
    public boolean requiresControllerAction() {
        return this == PHASE6_CONTROLLER_REVIEW;
    }
}
