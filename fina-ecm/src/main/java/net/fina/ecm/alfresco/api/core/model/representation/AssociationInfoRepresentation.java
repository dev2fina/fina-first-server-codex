package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Objects;

public class AssociationInfoRepresentation implements BaseRepresentation {
    @JsonProperty("assocType")
    protected String assocType = null;

    // High simplification to get same object for getParent or getAssoc
    @JsonProperty("isPrimary")
    private Boolean isPrimary = null;

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

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AssociationInfoRepresentation assocChild = (AssociationInfoRepresentation) o;
        return Objects.equals(this.assocType, assocChild.assocType)
                && Objects.equals(this.isPrimary, assocChild.isPrimary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assocType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AssocChild {");

        sb.append("    assocType: ").append(toIndentedString(assocType)).append(",");
        sb.append("    isPrimary: ").append(toIndentedString(isPrimary)).append(",");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
