package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.BranchStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Branch entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Branch response")
public class BranchResponse {

    @Schema(description = "Branch ID", example = "1")
    private Long id;

    @Schema(description = "FI Registry ID")
    private Long fiRegistryId;

    @Schema(description = "Branch code", example = "BR001")
    private String code;

    @Schema(description = "Branch name", example = "Main Branch")
    private String name;

    @Schema(description = "Branch type", example = "MAIN")
    private String branchType;

    @Schema(description = "Branch status")
    private BranchStatus status;

    @Schema(description = "Region")
    private RegionResponse region;

    @Schema(description = "City")
    private String city;

    @Schema(description = "Address")
    private String address;

    @Schema(description = "Phone number")
    private String phone;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Delegation person name")
    private String delegationPersonName;

    @Schema(description = "Delegation person identification number")
    private String delegationPersonIdentificationNumber;

    @Schema(description = "Registration date")
    private LocalDate registrationDate;

    @Schema(description = "Cancellation date")
    private LocalDate cancellationDate;

    @Schema(description = "Legal act number")
    private String legalActNumber;

    @Schema(description = "Legal act date")
    private LocalDate legalActDate;

    @Schema(description = "Sequence")
    private Integer sequence;

    @Schema(description = "Created at")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at")
    private LocalDateTime updatedAt;
}
