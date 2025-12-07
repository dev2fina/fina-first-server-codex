package net.fina.first.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Administrator entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Administrator response")
public class AdministratorResponse {

    @Schema(description = "Administrator ID", example = "1")
    private Long id;

    @Schema(description = "FI Registry ID")
    private Long fiRegistryId;

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Middle name")
    private String middleName;

    @Schema(description = "Full name")
    private String fullName;

    @Schema(description = "Identification number")
    private String identificationNumber;

    @Schema(description = "Citizenship")
    private String citizenship;

    @Schema(description = "Date of birth")
    private LocalDate dateOfBirth;

    @Schema(description = "Phone")
    private String phone;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Address")
    private String address;

    @Schema(description = "Position")
    private String position;

    @Schema(description = "Position type")
    private String positionType;

    @Schema(description = "Appointment date")
    private LocalDate appointmentDate;

    @Schema(description = "Termination date")
    private LocalDate terminationDate;

    @Schema(description = "Document type")
    private String documentType;

    @Schema(description = "Document number")
    private String documentNumber;

    @Schema(description = "Document issue date")
    private LocalDate documentIssueDate;

    @Schema(description = "Document issuer")
    private String documentIssuer;

    @Schema(description = "Non-resident flag")
    private boolean nonResident;

    @Schema(description = "Active status")
    private boolean active;

    @Schema(description = "Created at")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at")
    private LocalDateTime updatedAt;
}
