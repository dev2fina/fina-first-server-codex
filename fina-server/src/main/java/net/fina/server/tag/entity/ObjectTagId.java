package net.fina.server.tag.entity;

import java.io.Serializable;
import java.util.Objects;

public class ObjectTagId implements Serializable {

    private Tag tag;
    private long objectId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectTagId that = (ObjectTagId) o;
        return objectId == that.objectId &&
                Objects.equals(tag, that.tag) &&
                objectType == that.objectType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, objectId, objectType);
    }
}
