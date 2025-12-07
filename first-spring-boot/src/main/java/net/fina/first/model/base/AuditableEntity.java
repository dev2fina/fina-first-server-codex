package net.fina.first.model.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * Auditable entity with Hibernate Envers support for full audit trail.
 */
@MappedSuperclass
@Audited
@Getter
@Setter
public abstract class AuditableEntity extends BaseEntity {

    @Column(name = "is_deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    /**
     * Marks the entity as soft-deleted.
     *
     * @param deletedBy the user who deleted the entity
     */
    public void markAsDeleted(String deletedBy) {
        this.deleted = true;
        this.deletedAt = java.time.LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
