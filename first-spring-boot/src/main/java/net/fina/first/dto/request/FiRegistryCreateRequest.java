package net.fina.first.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.FiTypeCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating a new FI Registry (Phase 1 - Initial Registration).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create a new Financial Institution Registry")
public class FiRegistryCreateRequest {

    @NotNull(message = "FI type code is required")
    @Schema(description = "FI Type code", example = "BANK", required = true)
    private FiTypeCode fiTypeCode;

    @NotBlank(message = "Application number is required")
    @Size(max = 50)
    @Schema(description = "Application number", example = "APP-2024-001", required = true)
    private String applicationNumber;

    @NotNull(message = "Application received date is required")
    @Schema(description = "Application received date", example = "2024-01-15", required = true)
    private LocalDate applicationReceivedDate;

    @Schema(description = "Application registration deadline", example = "2024-02-15")
    private LocalDate applicationRegistrationDeadline;

    @NotNull(message = "Legal form ID is required")
    @Schema(description = "Legal form ID", example = "1", required = true)
    private Long legalFormId;

    @NotBlank(message = "Firm name is required")
    @Size(max = 500)
    @Schema(description = "Firm name", example = "ABC Bank Ltd", required = true)
    private String firmName;

    @Size(max = 500)
    @Schema(description = "Trade name", example = "ABC Bank")
    private String tradeName;

    @NotNull(message = "Legal address region ID is required")
    @Schema(description = "Legal address region ID", example = "1", required = true)
    private Long legalAddressRegionId;

    @NotBlank(message = "Legal address city is required")
    @Size(max = 255)
    @Schema(description = "Legal address city", example = "Tbilisi", required = true)
    private String legalAddressCity;

    @NotBlank(message = "Legal address is required")
    @Size(max = 1000)
    @Schema(description = "Legal address", example = "123 Main Street", required = true)
    private String legalAddress;

    @Schema(description = "Actual address same as legal", example = "true")
    private boolean actualAddressSameAsLegal;

    @Size(max = 1000)
    @Schema(description = "Actual or head office address")
    private String actualOrHeadOfficeAddress;

    @NotBlank(message = "Phone is required")
    @Size(max = 50)
    @Schema(description = "Phone number", example = "+995555123456", required = true)
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "info@abcbank.ge", required = true)
    private String email;

    @Size(max = 500)
    @Schema(description = "Website", example = "https://www.abcbank.ge")
    private String website;

    // Contact Person (PSP/VASP/BANK)
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

    // PSP/VASP specific
    @Schema(description = "License type")
    private String licenseType;

    @Schema(description = "Significant service provider flag")
    private Boolean significantServiceProvider;

    @Schema(description = "Capital amount")
    private BigDecimal capital;

    // Bank specific
    @Schema(description = "Foreign bank branch or subsidiary flag")
    private Boolean foreignBankBranchOrSubsidiary;

    // Other fields
    @Size(max = 500)
    @Schema(description = "Type of activity")
    private String typeOfActivity;

    @Size(max = 255)
    @Schema(description = "Binder reference")
    private String binder;
}
