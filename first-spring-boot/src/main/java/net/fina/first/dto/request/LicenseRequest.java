package net.fina.first.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.LicenseStatus;

import java.time.LocalDate;

/**
 * Request DTO for creating/updating License.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "License create/update request")
public class LicenseRequest {

    @NotBlank(message = "License number is required")
    @Size(max = 100)
    @Schema(description = "License number", required = true)
    private String licenseNumber;

    @Schema(description = "License type ID")
    private Long licenseTypeId;

    @Schema(description = "License status")
    private LicenseStatus status;

    @Schema(description = "Issuance date")
    private LocalDate issuanceDate;

    @Schema(description = "Effective date")
    private LocalDate effectiveDate;

    @Schema(description = "Expiration date")
    private LocalDate expirationDate;

    @Size(max = 100)
    @Schema(description = "Legal act number")
    private String legalActNumber;

    @Schema(description = "Legal act date")
    private LocalDate legalActDate;

    @Size(max = 2000)
    @Schema(description = "License scope")
    private String scope;

    @Size(max = 2000)
    @Schema(description = "License conditions")
    private String conditions;

    @Size(max = 2000)
    @Schema(description = "License restrictions")
    private String restrictions;

    @Size(max = 2000)
    @Schema(description = "Notes")
    private String notes;
}
