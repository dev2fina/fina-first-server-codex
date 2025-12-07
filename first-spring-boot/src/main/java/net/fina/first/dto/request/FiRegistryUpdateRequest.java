package net.fina.first.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating FI Registry (Phase 2 - Additional Details).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update Financial Institution Registry")
public class FiRegistryUpdateRequest {

    // Registration Details
    @Size(max = 50)
    @Schema(description = "Registration number")
    private String registrationNumber;

    @Schema(description = "Registration date")
    private LocalDate registrationDate;

    // Legal Act Details
    @Size(max = 100)
    @Schema(description = "Legal act number")
    private String legalActNumber;

    @Schema(description = "Legal act date")
    private LocalDate legalActDate;

    // Formal Compliance
    @Schema(description = "Formal compliance confirmation date")
    private LocalDate formalComplianceConfirmationDate;

    @Size(max = 100)
    @Schema(description = "Formal suitability confirmation letter number")
    private String formalSuitabilityConfirmationLetterNumber;

    // Licensing Details
    @Schema(description = "Licensing deadline")
    private LocalDate licensingDeadline;

    @Size(max = 50)
    @Schema(description = "Licensing number")
    private String licensingNumber;

    @Schema(description = "License issuance date")
    private LocalDate licenseIssuanceDate;

    @Size(max = 100)
    @Schema(description = "License type")
    private String licenseType;

    // Company Details
    @Schema(description = "Legal form ID")
    private Long legalFormId;

    @Size(max = 500)
    @Schema(description = "Firm name")
    private String firmName;

    @Size(max = 500)
    @Schema(description = "Trade name")
    private String tradeName;

    @Schema(description = "Capital amount")
    private BigDecimal capital;

    // Address Details
    @Schema(description = "Legal address region ID")
    private Long legalAddressRegionId;

    @Size(max = 255)
    @Schema(description = "Legal address city")
    private String legalAddressCity;

    @Size(max = 1000)
    @Schema(description = "Legal address")
    private String legalAddress;

    @Schema(description = "Actual address same as legal")
    private Boolean actualAddressSameAsLegal;

    @Size(max = 1000)
    @Schema(description = "Actual or head office address")
    private String actualOrHeadOfficeAddress;

    // Contact Details
    @Size(max = 50)
    @Schema(description = "Phone number")
    private String phone;

    @Email(message = "Invalid email format")
    @Schema(description = "Email address")
    private String email;

    @Size(max = 500)
    @Schema(description = "Website")
    private String website;

    // Contact Person
    @Size(max = 255)
    @Schema(description = "Contact person full name")
    private String contactPersonFullName;

    @Size(max = 50)
    @Schema(description = "Contact person identification number")
    private String contactPersonIdentificationNumber;

    @Size(max = 50)
    @Schema(description = "Contact person phone number")
    private String contactPersonPhone;

    @Email(message = "Invalid contact person email format")
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

    // FEX/LE specific
    @Size(max = 1000)
    @Schema(description = "Servicing commercial banks")
    private String servicingCommercialBanks;

    @Schema(description = "Reference exchange rate flag")
    private Boolean referenceExchangeRate;

    @Schema(description = "Commission fee flag")
    private Boolean commissionFee;

    // Other Fields
    @Size(max = 500)
    @Schema(description = "Type of activity")
    private String typeOfActivity;

    @Size(max = 255)
    @Schema(description = "Binder reference")
    private String binder;
}
