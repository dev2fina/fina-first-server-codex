package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Objects;

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssociationRepresentation extends AbstractRepresentation implements BaseRepresentation {
    @JsonProperty("targetId")
    private String targetId;

    @JsonProperty("assocType")
    private String assocType;

    /**
     * Get targetId
     *
     * @return targetId
     **/
    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    /**
     * Get assocType
     *
     * @return assocType
     **/
    public String getAssocType() {
        return assocType;
    }

    public void setAssocType(String assocType) {
        this.assocType = assocType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AssociationRepresentation assocChild = (AssociationRepresentation) o;
        return Objects.equals(this.targetId, assocChild.targetId)
                && Objects.equals(this.assocType, assocChild.assocType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetId, assocType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AssocChild {");

        sb.append("    targetId: ").append(toIndentedString(targetId)).append(",");
        sb.append("    assocType: ").append(toIndentedString(assocType)).append(",");
        sb.append("}");
        return sb.toString();
    }

}
