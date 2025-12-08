package net.fina.first.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.fina.first.model.base.AuditableEntity;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

/**
 * FI Administrator entity representing management/board members.
 */
@Entity
@Table(name = "first_administrators")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Administrator extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id", nullable = false)
    private FiRegistry fiRegistry;

    // === Personal Information ===

    @NotBlank(message = "First name is required")
    @Size(max = 255)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 255)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name", length = 255)
    private String middleName;

    @Column(name = "identification_number", length = 50)
    private String identificationNumber;

    @Column(name = "citizenship", length = 100)
    private String citizenship;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // === Contact Details ===

    @Column(name = "phone", length = 50)
    private String phone;

    @Email(message = "Invalid email format")
    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "address", length = 1000)
    private String address;

    // === Position Information ===

    @NotBlank(message = "Position is required")
    @Size(max = 255)
    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "position_type", length = 100)
    private String positionType;

    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    // === Document Information ===

    @Column(name = "document_type", length = 100)
    private String documentType;

    @Column(name = "document_number", length = 100)
    private String documentNumber;

    @Column(name = "document_issue_date")
    private LocalDate documentIssueDate;

    @Column(name = "document_issuer", length = 255)
    private String documentIssuer;

    // === Non-resident fields ===

    @Column(name = "is_non_resident")
    @Builder.Default
    private boolean nonResident = false;

    @Column(name = "non_resident_document_type", length = 100)
    private String nonResidentDocumentType;

    @Column(name = "non_resident_document_number", length = 100)
    private String nonResidentDocumentNumber;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @Column(name = "sequence")
    private Integer sequence;

    /**
     * Returns the full name of the administrator.
     */
    public String getFullName() {
        StringBuilder name = new StringBuilder(firstName);
        if (middleName != null && !middleName.isBlank()) {
            name.append(" ").append(middleName);
        }
        name.append(" ").append(lastName);
        return name.toString();
    }
}
