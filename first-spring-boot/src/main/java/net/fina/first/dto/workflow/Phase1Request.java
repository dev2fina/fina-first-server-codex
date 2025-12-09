package net.fina.first.dto.workflow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fina.first.model.enums.FiTypeCode;

import java.time.LocalDate;

/**
 * Request DTO for Phase 1 - Initial Fields.
 * Contains basic registration data for starting an FI registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Phase1Request {

    @NotNull(message = "FI Type is required")
    private FiTypeCode fiTypeCode;

    @NotBlank(message = "Firm name is required")
    @Size(max = 500, message = "Firm name must not exceed 500 characters")
    private String firmName;

    @Size(max = 500, message = "Trade name must not exceed 500 characters")
    private String tradeName;

    @NotNull(message = "Legal form is required")
    private Long legalFormId;

    @Size(max = 50, message = "Identification number must not exceed 50 characters")
    private String identificationNumber;

    @Size(max = 50, message = "Application number must not exceed 50 characters")
    private String applicationNumber;

    private LocalDate applicationReceivedDate;
}
