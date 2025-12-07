package net.fina.first.model.enums;

import lombok.Getter;

/**
 * Status of questionnaire items.
 */
@Getter
public enum QuestionnaireStatus {

    CURRENT("Current", "Currently active questionnaire"),
    ARCHIVE("Archive", "Archived questionnaire"),
    PENDING("Pending", "Pending review"),
    COMPLETED("Completed", "All questions answered"),
    INCOMPLETE("Incomplete", "Some questions unanswered");

    private final String displayName;
    private final String description;

    QuestionnaireStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
