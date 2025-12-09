package net.fina.first.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.experimental.SuperBuilder;
import net.fina.first.model.base.BaseEntity;
import org.hibernate.envers.Audited;

/**
 * Questionnaire template entity defining standard questions for FI types.
 */
@Entity
@Table(name = "first_questionnaires")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Questionnaire extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_type_id")
    private FiType fiType;

    @NotBlank(message = "Question code is required")
    @Size(max = 50)
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @NotBlank(message = "Question text is required")
    @Size(max = 2000)
    @Column(name = "question", nullable = false, length = 2000)
    private String question;

    @Column(name = "question_local", length = 2000)
    private String questionLocal;

    @Column(name = "group_code", length = 50)
    private String groupCode;

    @Column(name = "group_name", length = 255)
    private String groupName;

    @Column(name = "is_obligatory")
    @Builder.Default
    private boolean obligatory = false;

    @Column(name = "default_value", length = 1000)
    private String defaultValue;

    @Column(name = "validation_pattern", length = 500)
    private String validationPattern;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;
}
