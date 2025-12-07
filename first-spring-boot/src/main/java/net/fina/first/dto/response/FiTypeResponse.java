package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.FiTypeCode;

import java.util.Set;

/**
 * Response DTO for FiType entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Financial Institution Type response")
public class FiTypeResponse {

    @Schema(description = "FI Type ID", example = "1")
    private Long id;

    @Schema(description = "FI Type code", example = "BANK")
    private FiTypeCode code;

    @Schema(description = "FI Type name", example = "Commercial Bank")
    private String name;

    @Schema(description = "FI Type name in local language")
    private String nameLocal;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Has branches flag")
    private boolean hasBranches;

    @Schema(description = "Has administrators flag")
    private boolean hasAdministrators;

    @Schema(description = "Has beneficiaries flag")
    private boolean hasBeneficiaries;

    @Schema(description = "Has licenses flag")
    private boolean hasLicenses;

    @Schema(description = "Available branch types")
    private Set<String> branchTypes;

    @Schema(description = "Active status")
    private boolean active;
}
