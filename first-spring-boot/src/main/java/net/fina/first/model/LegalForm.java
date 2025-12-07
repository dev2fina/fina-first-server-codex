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
import net.fina.first.model.base.BaseEntity;
import org.hibernate.envers.Audited;

/**
 * Legal form catalog entity.
 * Defines the various legal forms that Financial Institutions can have.
 */
@Entity
@Table(name = "first_legal_forms")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalForm extends BaseEntity {

    @NotBlank(message = "Legal form code is required")
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Legal form name is required")
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_local")
    private String nameLocal;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;
}
