package net.fina.first.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Status of questionnaire items.
 */
@Getter
@RequiredArgsConstructor
public enum QuestionnaireStatus {

    CURRENT("Current", "Currently active questionnaire"),
    ARCHIVE("Archive", "Archived questionnaire"),
    PENDING("Pending", "Pending review"),
    COMPLETED("Completed", "All questions answered"),
    INCOMPLETE("Incomplete", "Some questions unanswered");

    private final String displayName;
    private final String description;
}
