package net.fina.first.model.registry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.fina.first.model.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "fi_questionnaire_response")
public class QuestionnaireResponse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id")
    private FinancialInstitution financialInstitution;

    @NotBlank
    @Size(max = 512)
    @Column(name = "question", nullable = false, length = 512)
    private String question;

    @Size(max = 255)
    @Column(name = "category", length = 255)
    private String category;

    @Column(name = "obligatory")
    private boolean obligatory;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "answer", nullable = false, length = 16)
    private QuestionAnswer answer;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 16)
    private QuestionnaireState state = QuestionnaireState.CURRENT;
}
