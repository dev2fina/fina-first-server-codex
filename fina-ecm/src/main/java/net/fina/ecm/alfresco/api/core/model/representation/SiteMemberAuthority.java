package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteMemberAuthority implements BaseRepresentation {
    private SiteMemberAuthorityType authorityType;
    private String shortName;
    private String firstName;
    private String lastName;
    private String fullName;
    private String displayName;

    public SiteMemberAuthority() {
    }

    public SiteMemberAuthorityType getAuthorityType() {
        return authorityType;
    }

    public void setAuthorityType(SiteMemberAuthorityType authorityType) {
        this.authorityType = authorityType;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
