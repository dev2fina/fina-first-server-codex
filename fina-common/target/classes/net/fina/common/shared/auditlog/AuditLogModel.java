package net.fina.common.shared.auditlog;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.common.client.constants.OperationType;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditLogModel {
    private String id;
    private String entityId;
    private String entityName;
    private String entityProperty;
    private String entityPropertyOldValue;
    private String entityPropertyNewValue;
    private OperationType operationType;
    private String actorId;
    private Date relevanceTime;

    public AuditLogModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityProperty() {
        return entityProperty;
    }

    public void setEntityProperty(String entityProperty) {
        this.entityProperty = entityProperty;
    }

    public String getEntityPropertyOldValue() {
        return entityPropertyOldValue;
    }

    public void setEntityPropertyOldValue(String entityPropertyOldValue) {
        this.entityPropertyOldValue = entityPropertyOldValue;
    }

    public String getEntityPropertyNewValue() {
        return entityPropertyNewValue;
    }

    public void setEntityPropertyNewValue(String entityPropertyNewValue) {
        this.entityPropertyNewValue = entityPropertyNewValue;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public Date getRelevanceTime() {
        return relevanceTime;
    }

    public void setRelevanceTime(Date relevanceTime) {
        this.relevanceTime = relevanceTime;
    }
}