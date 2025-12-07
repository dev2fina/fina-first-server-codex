
package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Date;
import java.util.Objects;

/**
 * Favorite
 */

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FavoriteRepresentation<T> extends AbstractRepresentation {
    @JsonProperty("targetGuid")
    protected String targetGuid = null;

    @JsonProperty("createdAt")
    protected Date createdAt = null;

    @JsonProperty("target")
    protected T target = null;

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * The guid of the object that is a favorite.
     *
     * @return targetGuid
     **/
    public String getTargetGuid() {
        return targetGuid;
    }

    public void setTargetGuid(String targetGuid) {
        this.targetGuid = targetGuid;
    }

    /**
     * The time the object was made a favorite.
     *
     * @return createdAt
     **/
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get target
     *
     * @return target
     **/
    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FavoriteRepresentation favorite = (FavoriteRepresentation) o;
        return Objects.equals(this.targetGuid, favorite.targetGuid)
                && Objects.equals(this.createdAt, favorite.createdAt) && Objects.equals(this.target, favorite.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetGuid, createdAt, target);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Favorite {");

        sb.append("    targetGuid: ").append(toIndentedString(targetGuid)).append(", ");
        sb.append("    createdAt: ").append(toIndentedString(createdAt)).append(", ");
        sb.append("    target: ").append(toIndentedString(target));
        sb.append("}");
        return sb.toString();
    }
}
