package net.fina.first.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.fina.first.model.base.BaseEntity;
import net.fina.first.model.enums.QuestionnaireStatus;
import org.hibernate.envers.Audited;

/**
 * Action Questionnaire entity for tracking questionnaire items within actions.
 */
@Entity
@Table(name = "first_action_questionnaires")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionQuestionnaire extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", nullable = false)
    private FiRegistryAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_id")
    private Questionnaire questionnaire;

    @NotBlank(message = "Question is required")
    @Size(max = 2000)
    @Column(name = "question", nullable = false, length = 2000)
    private String question;

    @Column(name = "answer", length = 4000)
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private QuestionnaireStatus status = QuestionnaireStatus.PENDING;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "group_code", length = 50)
    private String groupCode;

    @Column(name = "group_name", length = 255)
    private String groupName;

    @Column(name = "is_obligatory")
    @Builder.Default
    private boolean obligatory = false;

    @Column(name = "is_predefined")
    @Builder.Default
    private boolean predefined = false;

    @Column(name = "default_value", length = 1000)
    private String defaultValue;

    @Column(name = "note", length = 2000)
    private String note;

    @Column(name = "sequence")
    private Integer sequence;
}
