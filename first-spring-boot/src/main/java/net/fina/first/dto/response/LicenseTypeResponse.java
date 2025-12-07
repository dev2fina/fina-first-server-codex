package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for LicenseType entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "License Type response")
public class LicenseTypeResponse {

    @Schema(description = "License Type ID", example = "1")
    private Long id;

    @Schema(description = "License Type code", example = "BANKING")
    private String code;

    @Schema(description = "License Type name", example = "Banking License")
    private String name;

    @Schema(description = "License Type name in local language")
    private String nameLocal;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Validity period in months")
    private Integer validityPeriodMonths;

    @Schema(description = "Is renewable")
    private boolean renewable;

    @Schema(description = "Active status")
    private boolean active;
}
