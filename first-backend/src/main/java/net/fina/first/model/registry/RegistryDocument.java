package net.fina.first.model.registry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import net.fina.first.model.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "fi_registry_document")
public class RegistryDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id")
    private FinancialInstitution financialInstitution;

    @Column(name = "document_type", length = 64)
    private String documentType;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "storage_reference", length = 255)
    private String storageReference;

    @Column(name = "generated_at")
    private LocalDate generatedAt;
}
