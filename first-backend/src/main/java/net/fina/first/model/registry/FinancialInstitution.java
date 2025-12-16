package net.fina.first.model.registry;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.fina.first.model.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "fi_registry")
public class FinancialInstitution extends BaseEntity {

    @NotBlank
    @Size(max = 64)
    @Column(name = "registry_code", nullable = false, unique = true, length = 64)
    private String registryCode;

    @NotBlank
    @Size(max = 255)
    @Column(name = "legal_name", nullable = false, length = 255)
    private String legalName;

    @Size(max = 255)
    @Column(name = "brand_name", length = 255)
    private String brandName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "fi_type", nullable = false, length = 16)
    private FiType fiType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 64)
    private RegistrationStatus status = RegistrationStatus.DRAFT_PHASE1;

    @Column(name = "phase_one_submitted_at")
    private LocalDate phaseOneSubmittedAt;

    @Column(name = "phase_two_submitted_at")
    private LocalDate phaseTwoSubmittedAt;

    @Column(name = "registration_completed_at")
    private LocalDate registrationCompletedAt;

    @Size(max = 255)
    @Column(name = "hq_address", length = 255)
    private String headquartersAddress;

    @Size(max = 100)
    @Column(name = "hq_city", length = 100)
    private String headquartersCity;

    @Size(max = 100)
    @Column(name = "hq_country", length = 100)
    private String headquartersCountry;

    @Size(max = 100)
    @Column(name = "registration_country", length = 100)
    private String registrationCountry;

    @Column(name = "establishment_date")
    private LocalDate establishmentDate;

    @Size(max = 255)
    @Column(name = "controller_comments", length = 255)
    private String controllerComments;

    @OneToMany(mappedBy = "financialInstitution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Manager> managers = new HashSet<>();

    @OneToMany(mappedBy = "financialInstitution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Beneficiary> beneficiaries = new HashSet<>();

    @OneToMany(mappedBy = "financialInstitution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Branch> branches = new HashSet<>();

    @OneToMany(mappedBy = "financialInstitution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<License> licenses = new HashSet<>();

    @OneToMany(mappedBy = "financialInstitution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<QuestionnaireResponse> questionnaireResponses = new HashSet<>();

    @OneToMany(mappedBy = "financialInstitution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<GapDetail> gapDetails = new HashSet<>();

    @OneToMany(mappedBy = "financialInstitution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<RegistrationAction> actions = new HashSet<>();

    @OneToMany(mappedBy = "financialInstitution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<RegistryDocument> documents = new HashSet<>();
}
