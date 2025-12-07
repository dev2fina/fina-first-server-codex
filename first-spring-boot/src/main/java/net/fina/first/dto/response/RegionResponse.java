package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for Region entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Region response")
public class RegionResponse {

    @Schema(description = "Region ID", example = "1")
    private Long id;

    @Schema(description = "Region code", example = "TBL")
    private String code;

    @Schema(description = "Region name", example = "Tbilisi")
    private String name;

    @Schema(description = "Region name in local language")
    private String nameLocal;

    @Schema(description = "Parent region ID")
    private Long parentId;

    @Schema(description = "Hierarchy level")
    private Integer level;

    @Schema(description = "Display sequence")
    private Integer sequence;

    @Schema(description = "Child regions")
    private List<RegionResponse> children;
}
