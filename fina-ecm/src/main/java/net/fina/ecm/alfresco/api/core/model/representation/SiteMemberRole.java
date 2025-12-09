package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteMemberRole implements BaseRepresentation {
    private SiteMemberGroup group;
    private String role;

    public SiteMemberRole() {
    }

    public SiteMemberRole(SiteMemberGroup group, String role) {
        this.group = group;
        this.role = role;
    }

    public SiteMemberGroup getGroup() {
        return group;
    }

    public void setGroup(SiteMemberGroup group) {
        this.group = group;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
