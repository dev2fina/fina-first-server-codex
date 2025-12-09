package net.fina.ecm.alfresco.api.core.model.representation;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteMemberAuthorityRepresentation extends AbstractRepresentation {
    private String id;
    private String role;
    private SiteMemberAuthority authority;
    @JsonProperty(value = "isMemberOfGroup")
    private boolean memberOfGroup;

    public SiteMemberAuthorityRepresentation() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public SiteMemberAuthority getAuthority() {
        return authority;
    }

    public void setAuthority(SiteMemberAuthority authority) {
        this.authority = authority;
    }

    public boolean isMemberOfGroup() {
        return memberOfGroup;
    }

    public void setMemberOfGroup(boolean memberOfGroup) {
        this.memberOfGroup = memberOfGroup;
    }

    public String getId() {
        return authority.getFullName();
    }

    public void setId(String id) {
        this.id = id;
    }
}
