package net.fina.first.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.fina.first.model.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * Audit Log entity for tracking system operations.
 */
@Entity
@Table(name = "first_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuditLog extends BaseEntity {

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_name", length = 255)
    private String entityName;

    @Column(name = "entity_property", length = 255)
    private String entityProperty;

    @Column(name = "old_value", length = 4000)
    private String oldValue;

    @Column(name = "new_value", length = 4000)
    private String newValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", length = 30)
    private OperationType operationType;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "actor_login", length = 100)
    private String actorLogin;

    @Column(name = "relevance_time")
    private LocalDateTime relevanceTime;

    @Column(name = "client_ip", length = 50)
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "description", length = 2000)
    private String description;

    public enum OperationType {
        CREATE,
        UPDATE,
        DELETE,
        LOGIN,
        LOGOUT,
        PASSWORD_CHANGE,
        GENERATE,
        PROCESS,
        EXPORT,
        IMPORT
    }
}
