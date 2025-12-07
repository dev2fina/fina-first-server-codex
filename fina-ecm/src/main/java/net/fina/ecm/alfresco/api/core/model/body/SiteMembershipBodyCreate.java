package net.fina.ecm.alfresco.api.core.model.body;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteMembershipBodyCreate implements BaseRepresentation {
    private String role;
    private String id;
    @JsonIgnore
    private MemberType memberType;

    public SiteMembershipBodyCreate() {
    }

    public SiteMembershipBodyCreate(String personId, String role) {
        this.role = role;
        this.id = personId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public enum MemberType {
        PERSON,
        GROUP
    }
}
