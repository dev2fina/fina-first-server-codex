package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.BeneficiaryType;
import net.fina.first.model.enums.LegalEntityType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for Beneficiary entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Beneficiary response")
public class BeneficiaryResponse {

    @Schema(description = "Beneficiary ID", example = "1")
    private Long id;

    @Schema(description = "FI Registry ID")
    private Long fiRegistryId;

    @Schema(description = "Beneficiary type")
    private BeneficiaryType type;

    @Schema(description = "Capital percentage")
    private BigDecimal capitalPercentage;

    @Schema(description = "Voting rights percentage")
    private BigDecimal votingRightsPercentage;

    // Physical Person Fields
    @Schema(description = "First name (for physical persons)")
    private String firstName;

    @Schema(description = "Last name (for physical persons)")
    private String lastName;

    @Schema(description = "Middle name")
    private String middleName;

    @Schema(description = "Identification number")
    private String identificationNumber;

    @Schema(description = "Citizenship")
    private String citizenship;

    @Schema(description = "Date of birth")
    private LocalDate dateOfBirth;

    @Schema(description = "Address")
    private String address;

    // Legal Entity Fields
    @Schema(description = "Legal entity name")
    private String legalEntityName;

    @Schema(description = "Legal entity code")
    private String legalEntityCode;

    @Schema(description = "Legal entity type")
    private LegalEntityType legalEntityType;

    @Schema(description = "Legal entity country")
    private String legalEntityCountry;

    @Schema(description = "Legal entity registration number")
    private String legalEntityRegistrationNumber;

    @Schema(description = "Legal entity address")
    private String legalEntityAddress;

    // Hierarchy
    @Schema(description = "Parent beneficiary ID")
    private Long parentId;

    @Schema(description = "Hierarchy level")
    private Integer hierarchyLevel;

    @Schema(description = "Child beneficiaries")
    private List<BeneficiaryResponse> children;

    @Schema(description = "Ultimate beneficiary flag")
    private boolean ultimateBeneficiary;

    @Schema(description = "Display name")
    private String displayName;

    @Schema(description = "Active status")
    private boolean active;

    @Schema(description = "Notes")
    private String notes;

    @Schema(description = "Created at")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at")
    private LocalDateTime updatedAt;
}
