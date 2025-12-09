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
import jakarta.persistence.OrderBy;
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
import net.fina.first.model.enums.ControllerDecision;
import net.fina.first.model.enums.LicenseStatus;
import net.fina.first.model.enums.RegistrationStatus;
import net.fina.first.model.enums.WorkflowPhase;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Financial Institution Registry - Main entity representing a registered FI.
 * Contains general information, contact details, and registration data.
 * This is the core entity of the FIRST module.
 */
@Entity
@Table(name = "first_fi_registry")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FiRegistry extends AuditableEntity {

    // === Identification ===

    @Column(name = "identification_number", unique = true, length = 50)
    private String identificationNumber;

    @NotBlank(message = "FI code is required")
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    // === FI Type ===

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_type_id", nullable = false)
    private FiType fiType;

    // === Registration Status ===

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "license_status", length = 20)
    private LicenseStatus licenseStatus;

    // === Application Details ===

    @Column(name = "application_number", length = 50)
    private String applicationNumber;

    @Column(name = "application_received_date")
    private LocalDate applicationReceivedDate;

    @Column(name = "application_registration_deadline")
    private LocalDate applicationRegistrationDeadline;

    // === Registration Details ===

    @Column(name = "registration_number", length = 50)
    private String registrationNumber;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    // === Legal Act Details ===

    @Column(name = "legal_act_number", length = 100)
    private String legalActNumber;

    @Column(name = "legal_act_date")
    private LocalDate legalActDate;

    // === Formal Compliance ===

    @Column(name = "formal_compliance_confirmation_date")
    private LocalDate formalComplianceConfirmationDate;

    @Column(name = "formal_suitability_confirmation_letter_number", length = 100)
    private String formalSuitabilityConfirmationLetterNumber;

    // === Licensing Details ===

    @Column(name = "licensing_deadline")
    private LocalDate licensingDeadline;

    @Column(name = "licensing_number", length = 50)
    private String licensingNumber;

    @Column(name = "license_issuance_date")
    private LocalDate licenseIssuanceDate;

    @Column(name = "license_type", length = 100)
    private String licenseType;

    // === Company Details ===

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_form_id")
    private LegalForm legalForm;

    @NotBlank(message = "Firm name is required")
    @Size(max = 500)
    @Column(name = "firm_name", nullable = false, length = 500)
    private String firmName;

    @Column(name = "trade_name", length = 500)
    private String tradeName;

    @Column(name = "capital", precision = 19, scale = 4)
    private BigDecimal capital;

    // === Address Details ===

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_address_region_id")
    private Region legalAddressRegion;

    @Column(name = "legal_address_city", length = 255)
    private String legalAddressCity;

    @Column(name = "legal_address", length = 1000)
    private String legalAddress;

    @Column(name = "actual_address_same_as_legal")
    @Builder.Default
    private boolean actualAddressSameAsLegal = false;

    @Column(name = "actual_or_head_office_address", length = 1000)
    private String actualOrHeadOfficeAddress;

    // === Contact Details ===

    @Column(name = "phone", length = 50)
    private String phone;

    @Email(message = "Invalid email format")
    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "website", length = 500)
    private String website;

    // === Contact Person ===

    @Column(name = "contact_person_full_name", length = 255)
    private String contactPersonFullName;

    @Column(name = "contact_person_identification_number", length = 50)
    private String contactPersonIdentificationNumber;

    @Column(name = "contact_person_phone", length = 50)
    private String contactPersonPhone;

    @Email(message = "Invalid contact person email format")
    @Column(name = "contact_person_email", length = 255)
    private String contactPersonEmail;

    // === Bank-Specific Fields ===

    @Column(name = "foreign_bank_branch_or_subsidiary")
    private Boolean foreignBankBranchOrSubsidiary;

    @Column(name = "foreign_reliable_bank_branch_or_subsidiary")
    private Boolean foreignReliableBankBranchOrSubsidiary;

    // === PSP/VASP-Specific Fields ===

    @Column(name = "significant_service_provider")
    private Boolean significantServiceProvider;

    // === Other Fields ===

    @Column(name = "type_of_activity", length = 500)
    private String typeOfActivity;

    @Column(name = "servicing_commercial_banks", length = 1000)
    private String servicingCommercialBanks;

    @Column(name = "reference_exchange_rate")
    private Boolean referenceExchangeRate;

    @Column(name = "commission_fee")
    private Boolean commissionFee;

    @Column(name = "binder", length = 255)
    private String binder;

    // === Historical Data Flag ===

    @Column(name = "is_historic_data")
    @Builder.Default
    private boolean historicData = false;

    @Column(name = "cancellation_reason", length = 1000)
    private String cancellationReason;

    // === ECM Integration ===

    @Column(name = "ecm_folder_id", length = 100)
    private String ecmFolderId;

    // === Workflow Phase Tracking ===

    @Enumerated(EnumType.STRING)
    @Column(name = "current_phase", length = 30)
    @Builder.Default
    private WorkflowPhase currentPhase = WorkflowPhase.PHASE1_INITIAL;

    @Column(name = "phase1_completed_at")
    private LocalDateTime phase1CompletedAt;

    @Column(name = "phase2_completed_at")
    private LocalDateTime phase2CompletedAt;

    @Column(name = "data_entry_completed_at")
    private LocalDateTime dataEntryCompletedAt;

    @Column(name = "questionnaire_completed_at")
    private LocalDateTime questionnaireCompletedAt;

    // === Controller Review Tracking ===

    @Column(name = "submitted_to_controller_at")
    private LocalDateTime submittedToControllerAt;

    @Column(name = "submitted_to_controller_by", length = 100)
    private String submittedToControllerBy;

    @Column(name = "assigned_controller", length = 100)
    private String assignedController;

    @Column(name = "controller_reviewed_at")
    private LocalDateTime controllerReviewedAt;

    @Column(name = "controller_reviewed_by", length = 100)
    private String controllerReviewedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "controller_decision", length = 20)
    private ControllerDecision controllerDecision;

    @Column(name = "controller_decision_comment", length = 2000)
    private String controllerDecisionComment;

    // === Correction Tracking ===

    @Column(name = "correction_requested_at")
    private LocalDateTime correctionRequestedAt;

    @Column(name = "correction_reason", length = 2000)
    private String correctionReason;

    @Column(name = "correction_deadline")
    private LocalDate correctionDeadline;

    @Column(name = "correction_count")
    @Builder.Default
    private Integer correctionCount = 0;

    // === Final Registration ===

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "registered_by", length = 100)
    private String registeredBy;

    // === Relationships ===

    @OneToMany(mappedBy = "fiRegistry", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("registrationDate DESC")
    @NotAudited
    @Builder.Default
    private List<Branch> branches = new ArrayList<>();

    @OneToMany(mappedBy = "fiRegistry", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @Builder.Default
    private List<Administrator> administrators = new ArrayList<>();

    @OneToMany(mappedBy = "fiRegistry", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @Builder.Default
    private List<Beneficiary> beneficiaries = new ArrayList<>();

    @OneToMany(mappedBy = "fiRegistry", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("issuanceDate DESC")
    @NotAudited
    @Builder.Default
    private List<License> licenses = new ArrayList<>();

    @OneToMany(mappedBy = "fiRegistry", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    @NotAudited
    @Builder.Default
    private List<FiRegistryAction> actions = new ArrayList<>();

    // === Helper Methods ===

    public void addBranch(Branch branch) {
        branches.add(branch);
        branch.setFiRegistry(this);
    }

    public void removeBranch(Branch branch) {
        branches.remove(branch);
        branch.setFiRegistry(null);
    }

    public void addAdministrator(Administrator administrator) {
        administrators.add(administrator);
        administrator.setFiRegistry(this);
    }

    public void removeAdministrator(Administrator administrator) {
        administrators.remove(administrator);
        administrator.setFiRegistry(null);
    }

    public void addBeneficiary(Beneficiary beneficiary) {
        beneficiaries.add(beneficiary);
        beneficiary.setFiRegistry(this);
    }

    public void removeBeneficiary(Beneficiary beneficiary) {
        beneficiaries.remove(beneficiary);
        beneficiary.setFiRegistry(null);
    }

    public void addLicense(License license) {
        licenses.add(license);
        license.setFiRegistry(this);
    }

    public void removeLicense(License license) {
        licenses.remove(license);
        license.setFiRegistry(null);
    }

    public void addAction(FiRegistryAction action) {
        actions.add(action);
        action.setFiRegistry(this);
    }

    public void removeAction(FiRegistryAction action) {
        actions.remove(action);
        action.setFiRegistry(null);
    }

    // === Workflow Helper Methods ===

    /**
     * Check if the FI type requires license information (PSP or VASP).
     */
    public boolean requiresLicense() {
        if (fiType == null || fiType.getCode() == null) {
            return false;
        }
        String code = fiType.getCode().name();
        return "PSP".equals(code) || "VASP".equals(code);
    }

    /**
     * Check if the registration is in a phase that allows FI editing.
     */
    public boolean isEditableByFi() {
        return currentPhase != null && currentPhase.isFiEditable();
    }

    /**
     * Check if the registration requires controller action.
     */
    public boolean requiresControllerAction() {
        return currentPhase != null && currentPhase.requiresControllerAction();
    }

    /**
     * Check if the registration is complete.
     */
    public boolean isRegistered() {
        return currentPhase == WorkflowPhase.PHASE9_REGISTERED;
    }

    /**
     * Check if there's an active head office branch.
     */
    public boolean hasHeadOfficeBranch() {
        return branches != null && branches.stream()
                .anyMatch(b -> b.isHeadOffice() && !b.isDeleted());
    }

    /**
     * Get count of active administrators.
     */
    public long getActiveAdministratorCount() {
        if (administrators == null) return 0;
        return administrators.stream()
                .filter(a -> !a.isDeleted())
                .count();
    }

    /**
     * Get count of active beneficiaries.
     */
    public long getActiveBeneficiaryCount() {
        if (beneficiaries == null) return 0;
        return beneficiaries.stream()
                .filter(b -> !b.isDeleted())
                .count();
    }

    /**
     * Get count of active licenses.
     */
    public long getActiveLicenseCount() {
        if (licenses == null) return 0;
        return licenses.stream()
                .filter(l -> !l.isDeleted())
                .count();
    }

    /**
     * Advance to the next workflow phase.
     */
    public void advanceToNextPhase() {
        if (currentPhase != null && !currentPhase.isTerminal()) {
            WorkflowPhase nextPhase = currentPhase.getNextPhase();
            if (nextPhase != null) {
                this.currentPhase = nextPhase;
            }
        }
    }
}
