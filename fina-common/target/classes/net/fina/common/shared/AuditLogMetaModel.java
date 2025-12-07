package net.fina.common.shared;

import net.fina.common.client.constants.OperationType;
import net.fina.common.client.util.Operation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AuditLogMetaModel {
    private String id;
    private String entityId;
    private String entityName;
    private String entityProperty;
    private String entityPropertyOldValue;
    private String entityPropertyNewValue;
    private OperationType operationType;
    private String actorId;
    private Date relevanceTime;
    private String clientName;

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

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public static class Diff implements Serializable {

        Operation operation;
        String value;

        public Diff(Operation operation, String value) {
            this.operation = operation;
            this.value = value;
        }

        public Diff() {
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Operation getOperation() {
            return operation;
        }

        public void setOperation(Operation operation) {
            this.operation = operation;
        }
    }
}
