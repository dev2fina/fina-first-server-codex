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
import net.fina.first.model.base.AuditableEntity;
import net.fina.first.model.enums.LicenseStatus;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

/**
 * FI License entity representing licenses and certifications.
 */
@Entity
@Table(name = "first_licenses")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class License extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id", nullable = false)
    private FiRegistry fiRegistry;

    @NotBlank(message = "License number is required")
    @Size(max = 100)
    @Column(name = "license_number", nullable = false, length = 100)
    private String licenseNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_type_id")
    private LicenseType licenseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private LicenseStatus status = LicenseStatus.PENDING;

    @Column(name = "issuance_date")
    private LocalDate issuanceDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "suspension_date")
    private LocalDate suspensionDate;

    @Column(name = "revocation_date")
    private LocalDate revocationDate;

    // === Legal Act Reference ===

    @Column(name = "legal_act_number", length = 100)
    private String legalActNumber;

    @Column(name = "legal_act_date")
    private LocalDate legalActDate;

    // === License Details ===

    @Column(name = "scope", length = 2000)
    private String scope;

    @Column(name = "conditions", length = 2000)
    private String conditions;

    @Column(name = "restrictions", length = 2000)
    private String restrictions;

    @Column(name = "suspension_reason", length = 1000)
    private String suspensionReason;

    @Column(name = "revocation_reason", length = 1000)
    private String revocationReason;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "sequence")
    private Integer sequence;

    /**
     * Checks if the license is currently valid.
     */
    public boolean isValid() {
        if (status != LicenseStatus.ACTIVE) {
            return false;
        }
        LocalDate now = LocalDate.now();
        if (effectiveDate != null && now.isBefore(effectiveDate)) {
            return false;
        }
        return expirationDate == null || !now.isAfter(expirationDate);
    }
}
