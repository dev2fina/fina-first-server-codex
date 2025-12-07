package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.List;
import java.util.Objects;

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupRepresentation extends AbstractRepresentation {
    private String id = null;
    private String displayName = null;
    private Boolean isRoot = true;
    private List<String> parentIds;

    /**
     * Get id
     *
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get displayName
     *
     * @return displayName
     **/
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get isRoot
     *
     * @return isRoot
     **/
    public Boolean getIsRoot() {
        return isRoot;
    }

    public void setIsRoot(Boolean isRoot) {
        this.isRoot = isRoot;
    }

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupRepresentation group = (GroupRepresentation) o;
        return Objects.equals(this.id, group.id) && Objects.equals(this.displayName, group.displayName)
                && Objects.equals(this.isRoot, group.isRoot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayName, isRoot);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Group { ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    displayName: ").append(toIndentedString(displayName)).append(", ");
        sb.append("    isRoot: ").append(toIndentedString(isRoot)).append(" ");
        sb.append("}");
        return sb.toString();
    }
}
