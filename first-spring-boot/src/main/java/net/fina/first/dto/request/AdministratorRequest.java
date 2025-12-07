package net.fina.first.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for creating/updating Administrator.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Administrator create/update request")
public class AdministratorRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 255)
    @Schema(description = "First name", required = true)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 255)
    @Schema(description = "Last name", required = true)
    private String lastName;

    @Size(max = 255)
    @Schema(description = "Middle name")
    private String middleName;

    @Size(max = 50)
    @Schema(description = "Identification number")
    private String identificationNumber;

    @Size(max = 100)
    @Schema(description = "Citizenship")
    private String citizenship;

    @Schema(description = "Date of birth")
    private LocalDate dateOfBirth;

    @Size(max = 50)
    @Schema(description = "Phone")
    private String phone;

    @Email(message = "Invalid email format")
    @Schema(description = "Email")
    private String email;

    @Size(max = 1000)
    @Schema(description = "Address")
    private String address;

    @NotBlank(message = "Position is required")
    @Size(max = 255)
    @Schema(description = "Position", required = true)
    private String position;

    @Size(max = 100)
    @Schema(description = "Position type")
    private String positionType;

    @Schema(description = "Appointment date")
    private LocalDate appointmentDate;

    @Schema(description = "Termination date")
    private LocalDate terminationDate;

    @Size(max = 100)
    @Schema(description = "Document type")
    private String documentType;

    @Size(max = 100)
    @Schema(description = "Document number")
    private String documentNumber;

    @Schema(description = "Document issue date")
    private LocalDate documentIssueDate;

    @Size(max = 255)
    @Schema(description = "Document issuer")
    private String documentIssuer;

    @Schema(description = "Non-resident flag")
    private boolean nonResident;

    @Size(max = 100)
    @Schema(description = "Non-resident document type")
    private String nonResidentDocumentType;

    @Size(max = 100)
    @Schema(description = "Non-resident document number")
    private String nonResidentDocumentNumber;

    @Schema(description = "Active status")
    private boolean active = true;
}
