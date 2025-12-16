package net.fina.first.model.registry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.fina.first.model.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "fi_branch")
public class Branch extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id")
    private FinancialInstitution financialInstitution;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Size(max = 255)
    @Column(name = "address", length = 255)
    private String address;

    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 100)
    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "headquarters")
    private boolean headquarters;
}
