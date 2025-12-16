package net.fina.first.model.registry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.fina.first.model.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "fi_manager")
public class Manager extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id")
    private FinancialInstitution financialInstitution;

    @NotBlank
    @Size(max = 255)
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @NotBlank
    @Size(max = 32)
    @Column(name = "personal_code", nullable = false, length = 32)
    private String personalCode;

    @Size(max = 64)
    @Column(name = "position", length = 64)
    private String position;

    @Email
    @Size(max = 128)
    @Column(name = "email", length = 128)
    private String email;

    @Size(max = 32)
    @Column(name = "phone", length = 32)
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 32)
    private ManagerRole role;
}
