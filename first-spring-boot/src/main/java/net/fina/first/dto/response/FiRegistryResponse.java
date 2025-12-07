package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.LicenseStatus;
import net.fina.first.model.enums.RegistrationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for FiRegistry entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Financial Institution Registry response")
public class FiRegistryResponse {

    @Schema(description = "FI Registry ID", example = "1")
    private Long id;

    @Schema(description = "Identification number", example = "FI-2024-001")
    private String identificationNumber;

    @Schema(description = "FI code", example = "BANK001")
    private String code;

    @Schema(description = "FI Type information")
    private FiTypeResponse fiType;

    @Schema(description = "Registration status")
    private RegistrationStatus status;

    @Schema(description = "License status")
    private LicenseStatus licenseStatus;

    // Application Details
    @Schema(description = "Application number")
    private String applicationNumber;

    @Schema(description = "Application received date")
    private LocalDate applicationReceivedDate;

    @Schema(description = "Application registration deadline")
    private LocalDate applicationRegistrationDeadline;

    // Registration Details
    @Schema(description = "Registration number")
    private String registrationNumber;

    @Schema(description = "Registration date")
    private LocalDate registrationDate;

    // Legal Act Details
    @Schema(description = "Legal act number")
    private String legalActNumber;

    @Schema(description = "Legal act date")
    private LocalDate legalActDate;

    // Licensing Details
    @Schema(description = "Licensing deadline")
    private LocalDate licensingDeadline;

    @Schema(description = "Licensing number")
    private String licensingNumber;

    @Schema(description = "License issuance date")
    private LocalDate licenseIssuanceDate;

    @Schema(description = "License type")
    private String licenseType;

    // Company Details
    @Schema(description = "Legal form")
    private LegalFormResponse legalForm;

    @Schema(description = "Firm name")
    private String firmName;

    @Schema(description = "Trade name")
    private String tradeName;

    @Schema(description = "Capital")
    private BigDecimal capital;

    // Address Details
    @Schema(description = "Legal address region")
    private RegionResponse legalAddressRegion;

    @Schema(description = "Legal address city")
    private String legalAddressCity;

    @Schema(description = "Legal address")
    private String legalAddress;

    @Schema(description = "Actual address same as legal")
    private boolean actualAddressSameAsLegal;

    @Schema(description = "Actual or head office address")
    private String actualOrHeadOfficeAddress;

    // Contact Details
    @Schema(description = "Phone number")
    private String phone;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Website")
    private String website;

    // Contact Person
    @Schema(description = "Contact person full name")
    private String contactPersonFullName;

    @Schema(description = "Contact person identification number")
    private String contactPersonIdentificationNumber;

    @Schema(description = "Contact person phone")
    private String contactPersonPhone;

    @Schema(description = "Contact person email")
    private String contactPersonEmail;

    // Bank-Specific Fields
    @Schema(description = "Foreign bank branch or subsidiary flag")
    private Boolean foreignBankBranchOrSubsidiary;

    @Schema(description = "Foreign reliable bank branch or subsidiary flag")
    private Boolean foreignReliableBankBranchOrSubsidiary;

    // PSP/VASP-Specific Fields
    @Schema(description = "Significant service provider flag")
    private Boolean significantServiceProvider;

    // Other Fields
    @Schema(description = "Type of activity")
    private String typeOfActivity;

    @Schema(description = "Binder")
    private String binder;

    // Counts
    @Schema(description = "Number of branches")
    private Integer branchCount;

    @Schema(description = "Number of administrators")
    private Integer administratorCount;

    @Schema(description = "Number of beneficiaries")
    private Integer beneficiaryCount;

    @Schema(description = "Number of licenses")
    private Integer licenseCount;

    // Audit Fields
    @Schema(description = "Created at timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Created by user")
    private String createdBy;

    @Schema(description = "Updated at timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Updated by user")
    private String updatedBy;
}
