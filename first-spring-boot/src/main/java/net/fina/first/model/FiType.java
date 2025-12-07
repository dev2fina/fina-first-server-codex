package net.fina.first.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.fina.first.model.base.BaseEntity;
import net.fina.first.model.enums.FiTypeCode;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

/**
 * Financial Institution Type entity.
 * Defines the configuration for each type of FI including
 * which entities and fields are applicable.
 */
@Entity
@Table(name = "first_fi_types")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiType extends BaseEntity {

    @NotNull(message = "FI type code is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private FiTypeCode code;

    @NotBlank(message = "FI type name is required")
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_local")
    private String nameLocal;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "has_branches")
    @Builder.Default
    private boolean hasBranches = true;

    @Column(name = "has_administrators")
    @Builder.Default
    private boolean hasAdministrators = true;

    @Column(name = "has_beneficiaries")
    @Builder.Default
    private boolean hasBeneficiaries = true;

    @Column(name = "has_licenses")
    @Builder.Default
    private boolean hasLicenses = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "first_fi_type_branch_types",
            joinColumns = @JoinColumn(name = "fi_type_id")
    )
    @Column(name = "branch_type")
    @Builder.Default
    private Set<String> branchTypes = new HashSet<>();

    @Column(name = "registration_workflow_key")
    private String registrationWorkflowKey;

    @Column(name = "change_workflow_key")
    private String changeWorkflowKey;

    @Column(name = "disable_workflow_key")
    private String disableWorkflowKey;

    @Column(name = "branch_change_workflow_key")
    private String branchChangeWorkflowKey;

    @Column(name = "branch_edit_workflow_key")
    private String branchEditWorkflowKey;

    @Column(name = "document_withdrawal_workflow_key")
    private String documentWithdrawalWorkflowKey;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @Column(name = "sequence")
    private Integer sequence;

    /**
     * JSON configuration for FI type-specific field mappings.
     * Stores which fields are required/optional for each registration phase.
     */
    @Column(name = "field_configuration", columnDefinition = "TEXT")
    private String fieldConfiguration;
}
