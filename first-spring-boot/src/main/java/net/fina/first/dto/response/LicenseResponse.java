package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.LicenseStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for License entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "License response")
public class LicenseResponse {

    @Schema(description = "License ID", example = "1")
    private Long id;

    @Schema(description = "FI Registry ID")
    private Long fiRegistryId;

    @Schema(description = "License number")
    private String licenseNumber;

    @Schema(description = "License type")
    private LicenseTypeResponse licenseType;

    @Schema(description = "License status")
    private LicenseStatus status;

    @Schema(description = "Issuance date")
    private LocalDate issuanceDate;

    @Schema(description = "Effective date")
    private LocalDate effectiveDate;

    @Schema(description = "Expiration date")
    private LocalDate expirationDate;

    @Schema(description = "Suspension date")
    private LocalDate suspensionDate;

    @Schema(description = "Revocation date")
    private LocalDate revocationDate;

    @Schema(description = "Legal act number")
    private String legalActNumber;

    @Schema(description = "Legal act date")
    private LocalDate legalActDate;

    @Schema(description = "License scope")
    private String scope;

    @Schema(description = "License conditions")
    private String conditions;

    @Schema(description = "License restrictions")
    private String restrictions;

    @Schema(description = "Suspension reason")
    private String suspensionReason;

    @Schema(description = "Revocation reason")
    private String revocationReason;

    @Schema(description = "Notes")
    private String notes;

    @Schema(description = "Is valid")
    private boolean valid;

    @Schema(description = "Created at")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at")
    private LocalDateTime updatedAt;
}
