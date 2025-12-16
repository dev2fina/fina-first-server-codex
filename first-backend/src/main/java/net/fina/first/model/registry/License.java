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
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import net.fina.first.model.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "fi_license")
public class License extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id")
    private FinancialInstitution financialInstitution;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", nullable = false, length = 16)
    private LicenseType licenseType;

    @NotBlank
    @Size(max = 64)
    @Column(name = "license_number", nullable = false, length = 64)
    private String licenseNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "license_status", nullable = false, length = 32)
    private LicenseStatus licenseStatus = LicenseStatus.INACTIVE;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;
}
