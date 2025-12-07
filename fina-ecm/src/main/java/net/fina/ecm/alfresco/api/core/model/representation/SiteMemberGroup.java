package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteMemberGroup implements BaseRepresentation {
    private String fullName;

    public SiteMemberGroup() {
    }

    public SiteMemberGroup(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
