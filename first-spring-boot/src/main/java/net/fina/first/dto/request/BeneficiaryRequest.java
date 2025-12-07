package net.fina.first.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.BeneficiaryType;
import net.fina.first.model.enums.LegalEntityType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating/updating Beneficiary.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Beneficiary create/update request")
public class BeneficiaryRequest {

    @NotNull(message = "Beneficiary type is required")
    @Schema(description = "Beneficiary type", required = true)
    private BeneficiaryType type;

    @DecimalMin(value = "0.0001", message = "Capital percentage must be positive")
    @DecimalMax(value = "100.0000", message = "Capital percentage cannot exceed 100")
    @Schema(description = "Capital percentage")
    private BigDecimal capitalPercentage;

    @DecimalMin(value = "0.0001", message = "Voting rights percentage must be positive")
    @DecimalMax(value = "100.0000", message = "Voting rights percentage cannot exceed 100")
    @Schema(description = "Voting rights percentage")
    private BigDecimal votingRightsPercentage;

    // Physical Person Fields
    @Size(max = 255)
    @Schema(description = "First name (required for physical persons)")
    private String firstName;

    @Size(max = 255)
    @Schema(description = "Last name (required for physical persons)")
    private String lastName;

    @Size(max = 255)
    @Schema(description = "Middle name")
    private String middleName;

    @Size(max = 50)
    @Schema(description = "Identification number")
    private String identificationNumber;

    @Size(max = 100)
    @Schema(description = "Citizenship")
    private String citizenship;

    @Schema(description = "Date of birth")
    private LocalDate dateOfBirth;

    @Size(max = 1000)
    @Schema(description = "Address")
    private String address;

    // Legal Entity Fields
    @Size(max = 500)
    @Schema(description = "Legal entity name (required for legal entities)")
    private String legalEntityName;

    @Size(max = 100)
    @Schema(description = "Legal entity code")
    private String legalEntityCode;

    @Schema(description = "Legal entity type")
    private LegalEntityType legalEntityType;

    @Size(max = 100)
    @Schema(description = "Legal entity country")
    private String legalEntityCountry;

    @Size(max = 100)
    @Schema(description = "Legal entity registration number")
    private String legalEntityRegistrationNumber;

    @Size(max = 1000)
    @Schema(description = "Legal entity address")
    private String legalEntityAddress;

    // Hierarchy
    @Schema(description = "Parent beneficiary ID")
    private Long parentId;

    @Schema(description = "Ultimate beneficiary flag")
    private boolean ultimateBeneficiary;

    @Size(max = 2000)
    @Schema(description = "Notes")
    private String notes;
}
