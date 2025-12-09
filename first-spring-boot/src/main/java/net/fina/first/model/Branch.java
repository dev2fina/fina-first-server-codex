package net.fina.first.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.fina.first.model.base.AuditableEntity;
import net.fina.first.model.enums.BranchStatus;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

/**
 * FI Branch entity representing subsidiary locations/offices.
 */
@Entity
@Table(name = "first_branches")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Branch extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id", nullable = false)
    private FiRegistry fiRegistry;

    @NotBlank(message = "Branch code is required")
    @Size(max = 50)
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @NotBlank(message = "Branch name is required")
    @Size(max = 500)
    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Column(name = "branch_type", length = 100)
    private String branchType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private BranchStatus status = BranchStatus.PENDING;

    // === Address Details ===

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @Column(name = "city", length = 255)
    private String city;

    @Column(name = "address", length = 1000)
    private String address;

    // === Contact Details ===

    @Column(name = "phone", length = 50)
    private String phone;

    @Email(message = "Invalid email format")
    @Column(name = "email", length = 255)
    private String email;

    // === Delegation Person ===

    @Column(name = "delegation_person_name", length = 255)
    private String delegationPersonName;

    @Column(name = "delegation_person_identification_number", length = 50)
    private String delegationPersonIdentificationNumber;

    // === Dates ===

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "cancellation_date")
    private LocalDate cancellationDate;

    @Column(name = "cancellation_reason", length = 1000)
    private String cancellationReason;

    @Column(name = "is_head_office")
    @Builder.Default
    private boolean headOffice = false;

    // === Legal Act ===

    @Column(name = "legal_act_number", length = 100)
    private String legalActNumber;

    @Column(name = "legal_act_date")
    private LocalDate legalActDate;

    @Column(name = "sequence")
    private Integer sequence;
}
