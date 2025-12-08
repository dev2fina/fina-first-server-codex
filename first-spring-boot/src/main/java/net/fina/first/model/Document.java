package net.fina.first.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.fina.first.model.base.AuditableEntity;
import net.fina.first.model.enums.DocumentType;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

/**
 * Document entity for managing FI-related documents.
 */
@Entity
@Table(name = "first_documents")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Document extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fi_registry_id")
    private FiRegistry fiRegistry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id")
    private FiRegistryAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;

    @NotBlank(message = "Document name is required")
    @Size(max = 500)
    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Column(name = "number", length = 100)
    private String number;

    @Column(name = "document_date")
    private LocalDate documentDate;

    @Column(name = "description", length = 2000)
    private String description;

    // === ECM Integration ===

    @Column(name = "ecm_node_id", length = 100)
    private String ecmNodeId;

    // === File Information ===

    @Column(name = "file_uuid", length = 100)
    private String fileUuid;

    @Column(name = "file_name", length = 500)
    private String fileName;

    @Column(name = "original_file_name", length = 500)
    private String originalFileName;

    @Column(name = "file_path", length = 1000)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "checksum", length = 100)
    private String checksum;

    // === Correction Fields ===

    @Column(name = "correction_deadline")
    private LocalDate correctionDeadline;

    @Column(name = "correction_days")
    private Integer correctionDays;

    // === Status Fields ===

    @Column(name = "is_signed")
    @Builder.Default
    private boolean signed = false;

    @Column(name = "is_verified")
    @Builder.Default
    private boolean verified = false;

    @Column(name = "sequence")
    private Integer sequence;

    // === Alias methods for compatibility ===

    public String getContentType() {
        return mimeType;
    }

    public void setContentType(String contentType) {
        this.mimeType = contentType;
    }

    public String getDocumentNumber() {
        return number;
    }

    public void setDocumentNumber(String documentNumber) {
        this.number = documentNumber;
    }

    public String getDisplayName() {
        return name;
    }

    public void setDisplayName(String displayName) {
        this.name = displayName;
    }
}
