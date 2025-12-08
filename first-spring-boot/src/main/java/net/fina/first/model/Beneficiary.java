package net.fina.first.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.fina.first.model.base.AuditableEntity;
import net.fina.first.model.enums.BeneficiaryType;
import net.fina.first.model.enums.LegalEntityType;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Beneficiary (Complex Structure) entity representing beneficial owners.
 * Can be either a natural person (PHYSICAL) or a legal entity (LEGAL).
 * Supports hierarchical ownership structures through parent-child relationships.
 */
@Entity
@Table(name = "first_beneficiaries")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Beneficiary extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id", nullable = false)
    private FiRegistry fiRegistry;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private BeneficiaryType type;

    // === Ownership Details ===

    @DecimalMin(value = "0.0001", message = "Capital percentage must be positive")
    @DecimalMax(value = "100.0000", message = "Capital percentage cannot exceed 100")
    @Column(name = "capital_percentage", precision = 8, scale = 4)
    private BigDecimal capitalPercentage;

    @Column(name = "voting_rights_percentage", precision = 8, scale = 4)
    private BigDecimal votingRightsPercentage;

    // === Physical Person Fields ===

    @Size(max = 255)
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 255)
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name", length = 255)
    private String middleName;

    @Column(name = "identification_number", length = 50)
    private String identificationNumber;

    @Column(name = "citizenship", length = 100)
    private String citizenship;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 1000)
    private String address;

    // === Legal Entity Fields ===

    @Column(name = "legal_entity_name", length = 500)
    private String legalEntityName;

    @Column(name = "legal_entity_code", length = 100)
    private String legalEntityCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "legal_entity_type", length = 30)
    private LegalEntityType legalEntityType;

    @Column(name = "legal_entity_country", length = 100)
    private String legalEntityCountry;

    @Column(name = "legal_entity_registration_number", length = 100)
    private String legalEntityRegistrationNumber;

    @Column(name = "legal_entity_address", length = 1000)
    private String legalEntityAddress;

    // === Hierarchical Structure ===

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Beneficiary parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @Builder.Default
    private List<Beneficiary> children = new ArrayList<>();

    @Column(name = "hierarchy_level")
    private Integer hierarchyLevel;

    // === Additional Fields ===

    @Column(name = "is_ultimate_beneficiary")
    @Builder.Default
    private boolean ultimateBeneficiary = false;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "notes", length = 2000)
    private String notes;

    // === Helper Methods ===

    public void addChild(Beneficiary child) {
        children.add(child);
        child.setParent(this);
        child.setHierarchyLevel(this.hierarchyLevel != null ? this.hierarchyLevel + 1 : 1);
    }

    public void removeChild(Beneficiary child) {
        children.remove(child);
        child.setParent(null);
    }

    /**
     * Returns the display name based on the beneficiary type.
     */
    public String getDisplayName() {
        if (type == BeneficiaryType.PHYSICAL) {
            StringBuilder name = new StringBuilder();
            if (firstName != null) name.append(firstName);
            if (middleName != null && !middleName.isBlank()) name.append(" ").append(middleName);
            if (lastName != null) name.append(" ").append(lastName);
            return name.toString().trim();
        } else {
            return legalEntityName != null ? legalEntityName : "";
        }
    }
}
