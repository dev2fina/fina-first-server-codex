package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Permission entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Permission response")
public class PermissionResponse {

    @Schema(description = "Permission ID", example = "1")
    private Long id;

    @Schema(description = "Permission code", example = "FIRST_FI_REGISTRY_REVIEW")
    private String code;

    @Schema(description = "Permission name", example = "Review FI Registry")
    private String name;

    @Schema(description = "Permission description")
    private String description;

    @Schema(description = "Module name")
    private String module;

    @Schema(description = "Is active")
    private boolean active;
}
