package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for LegalForm entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Legal Form response")
public class LegalFormResponse {

    @Schema(description = "Legal Form ID", example = "1")
    private Long id;

    @Schema(description = "Legal Form code", example = "LLC")
    private String code;

    @Schema(description = "Legal Form name", example = "Limited Liability Company")
    private String name;

    @Schema(description = "Legal Form name in local language")
    private String nameLocal;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Active status")
    private boolean active;
}
