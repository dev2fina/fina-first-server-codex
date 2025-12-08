package net.fina.first.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.fina.first.model.base.BaseEntity;
import org.hibernate.envers.Audited;

/**
 * License Type catalog entity.
 * Defines the various types of licenses that can be issued.
 */
@Entity
@Table(name = "first_license_types")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LicenseType extends BaseEntity {

    @NotBlank(message = "License type code is required")
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "License type name is required")
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_local")
    private String nameLocal;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "validity_period_months")
    private Integer validityPeriodMonths;

    @Column(name = "renewable")
    @Builder.Default
    private boolean renewable = true;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;
}
