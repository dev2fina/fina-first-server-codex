package net.fina.server.fi.model;

import org.hibernate.envers.RevisionType;

import java.util.Date;

public class EntityHistoryModel<T extends Object> {
    private T entity;
    private Date modifiedAt;
    private long revisionNumber;
    private String modifiedBy;
    private RevisionType revisionType;

    public EntityHistoryModel() {
    }

    public EntityHistoryModel(T entity, Date modifiedAt, long revisionNumber, String modifiedBy, RevisionType revisionType) {
        this.entity = entity;
        this.modifiedAt = modifiedAt;
        this.revisionNumber = revisionNumber;
        this.modifiedBy = modifiedBy;
        this.revisionType = revisionType;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public long getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(long revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public RevisionType getRevisionType() {
        return revisionType;
    }

    public void setRevisionType(RevisionType revisionType) {
        this.revisionType = revisionType;
    }
}
