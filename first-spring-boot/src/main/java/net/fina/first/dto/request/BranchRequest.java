package net.fina.first.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.BranchStatus;

import java.time.LocalDate;

/**
 * Request DTO for creating/updating Branch.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Branch create/update request")
public class BranchRequest {

    @NotBlank(message = "Branch code is required")
    @Size(max = 50)
    @Schema(description = "Branch code", example = "BR001", required = true)
    private String code;

    @NotBlank(message = "Branch name is required")
    @Size(max = 500)
    @Schema(description = "Branch name", example = "Main Branch", required = true)
    private String name;

    @Size(max = 100)
    @Schema(description = "Branch type", example = "MAIN")
    private String branchType;

    @Schema(description = "Branch status")
    private BranchStatus status;

    @Schema(description = "Region ID")
    private Long regionId;

    @Size(max = 255)
    @Schema(description = "City")
    private String city;

    @Size(max = 1000)
    @Schema(description = "Address")
    private String address;

    @Size(max = 50)
    @Schema(description = "Phone number")
    private String phone;

    @Email(message = "Invalid email format")
    @Schema(description = "Email")
    private String email;

    @Size(max = 255)
    @Schema(description = "Delegation person name")
    private String delegationPersonName;

    @Size(max = 50)
    @Schema(description = "Delegation person identification number")
    private String delegationPersonIdentificationNumber;

    @Schema(description = "Registration date")
    private LocalDate registrationDate;

    @Schema(description = "Cancellation date")
    private LocalDate cancellationDate;

    @Size(max = 100)
    @Schema(description = "Legal act number")
    private String legalActNumber;

    @Schema(description = "Legal act date")
    private LocalDate legalActDate;

    @Schema(description = "Sequence")
    private Integer sequence;
}
