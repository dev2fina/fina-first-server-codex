package net.fina.first.model.registry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import net.fina.first.model.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "fi_registration_action")
public class RegistrationAction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id")
    private FinancialInstitution financialInstitution;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 64)
    private RegistrationStatus status;

    @Column(name = "action", length = 128)
    private String action;

    @Column(name = "performed_by", length = 128)
    private String performedBy;

    @Column(name = "performed_at")
    private LocalDate performedAt;

    @Column(name = "comment", length = 512)
    private String comment;
}
