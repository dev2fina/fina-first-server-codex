package net.fina.first.dto.workflow;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for Phase 2 - General Info.
 * Contains company details, address, and contact information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Phase2Request {

    // === Address Details ===

    @NotNull(message = "Legal address region is required")
    private Long legalAddressRegionId;

    @NotBlank(message = "Legal address city is required")
    @Size(max = 255, message = "Legal address city must not exceed 255 characters")
    private String legalAddressCity;

    @NotBlank(message = "Legal address is required")
    @Size(max = 1000, message = "Legal address must not exceed 1000 characters")
    private String legalAddress;

    @Builder.Default
    private boolean actualAddressSameAsLegal = false;

    @Size(max = 1000, message = "Actual address must not exceed 1000 characters")
    private String actualOrHeadOfficeAddress;

    // === Contact Details ===

    @NotBlank(message = "Phone is required")
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 500, message = "Website must not exceed 500 characters")
    private String website;

    // === Contact Person ===

    @NotBlank(message = "Contact person full name is required")
    @Size(max = 255, message = "Contact person name must not exceed 255 characters")
    private String contactPersonFullName;

    @Size(max = 50, message = "Contact person ID must not exceed 50 characters")
    private String contactPersonIdentificationNumber;

    @NotBlank(message = "Contact person phone is required")
    @Size(max = 50, message = "Contact person phone must not exceed 50 characters")
    private String contactPersonPhone;

    @Email(message = "Invalid contact person email format")
    @Size(max = 255, message = "Contact person email must not exceed 255 characters")
    private String contactPersonEmail;

    // === Capital ===

    private BigDecimal capital;

    // === Type-Specific Fields ===

    private Boolean foreignBankBranchOrSubsidiary;
    private Boolean foreignReliableBankBranchOrSubsidiary;
    private Boolean significantServiceProvider;
    private Boolean referenceExchangeRate;
    private Boolean commissionFee;

    @Size(max = 500, message = "Type of activity must not exceed 500 characters")
    private String typeOfActivity;

    @Size(max = 1000, message = "Servicing commercial banks must not exceed 1000 characters")
    private String servicingCommercialBanks;
}
