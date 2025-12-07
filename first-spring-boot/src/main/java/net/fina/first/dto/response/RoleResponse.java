package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Response DTO for Role entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Role response")
public class RoleResponse {

    @Schema(description = "Role ID", example = "1")
    private Long id;

    @Schema(description = "Role code", example = "ADMIN")
    private String code;

    @Schema(description = "Role name", example = "Administrator")
    private String name;

    @Schema(description = "Role description")
    private String description;

    @Schema(description = "Is system role")
    private boolean system;

    @Schema(description = "Is active")
    private boolean active;

    @Schema(description = "Role permissions")
    private Set<PermissionResponse> permissions;
}
