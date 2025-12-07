package net.fina.server.tag.entity;

import jakarta.persistence.*;

@Entity(name = "IN_OBJECT_TAGS")
@Table(name = "IN_OBJECT_TAGS")
@IdClass(ObjectTagId.class)
public class ObjectTag {

    @Id
    @OneToOne
    private Tag tag;

    @Id
    private long objectId;

    @Id
    @Enumerated(EnumType.ORDINAL)
    private ObjectType objectType;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }
}
