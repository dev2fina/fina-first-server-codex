package net.fina.first.model.registry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.fina.first.model.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "fi_gap_detail")
public class GapDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id")
    private FinancialInstitution financialInstitution;

    @Size(max = 64)
    @Column(name = "question_reference", length = 64)
    private String questionReference;

    @NotBlank
    @Size(max = 512)
    @Column(name = "description", nullable = false, length = 512)
    private String description;

    @Size(max = 255)
    @Column(name = "resolution", length = 255)
    private String resolution;
}
