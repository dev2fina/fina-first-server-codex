package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyUpdate;

import java.util.*;

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonRepresentation extends AbstractRepresentation {
    private String id = null;
    private String firstName = null;
    private String lastName = null;
    private String description = null;
    private String avatarId = null;
    private String email = null;
    private String skypeId = null;
    private String googleId = null;
    private String instantMessageId = null;
    private String jobTitle = null;
    private String location = null;
    private CompanyRepresentation company = null;
    private String mobile = null;
    private String telephone = null;
    private Date statusUpdatedAt = null;
    private String userStatus = null;
    private Boolean enabled = true;
    private Boolean emailNotificationsEnabled = null;
    private List<String> aspectNames = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();
    private CapabilitiesRepresentation capabilities;

    public PersonRepresentation() {
    }

    public PersonRepresentation(String id, String email, String mobile, boolean enabled) {
        this.id = id;
        this.email = email;
        this.mobile = mobile;
        this.enabled = enabled;
    }

    public PersonRepresentation(String id, String email, String mobile, boolean enabled, String firstName, String lastName) {
        this(id, email, mobile, enabled);
        this.firstName = firstName;
        this.lastName = lastName;
    }


    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////

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
     * Get firstName
     *
     * @return firstName
     **/
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get lastName
     *
     * @return lastName
     **/
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get description
     *
     * @return description
     **/
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get avatarId
     *
     * @return avatarId
     **/
    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    /**
     * Get email
     *
     * @return email
     **/
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get skypeId
     *
     * @return skypeId
     **/
    public String getSkypeId() {
        return skypeId;
    }

    public void setSkypeId(String skypeId) {
        this.skypeId = skypeId;
    }

    /**
     * Get googleId
     *
     * @return googleId
     **/
    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    /**
     * Get instantMessageId
     *
     * @return instantMessageId
     **/
    public String getInstantMessageId() {
        return instantMessageId;
    }

    public void setInstantMessageId(String instantMessageId) {
        this.instantMessageId = instantMessageId;
    }

    /**
     * Get jobTitle
     *
     * @return jobTitle
     **/
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * Get location
     *
     * @return location
     **/
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get company
     *
     * @return company
     **/
    public CompanyRepresentation getCompany() {
        return company;
    }

    public void setCompany(CompanyRepresentation company) {
        this.company = company;
    }

    /**
     * Get mobile
     *
     * @return mobile
     **/
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * Get telephone
     *
     * @return telephone
     **/
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Get statusUpdatedAt
     *
     * @return statusUpdatedAt
     **/
    public Date getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(Date statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    /**
     * Get userStatus
     *
     * @return userStatus
     **/
    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * Get enabled
     *
     * @return enabled
     **/
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get emailNotificationsEnabled
     *
     * @return emailNotificationsEnabled
     **/
    public Boolean isEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public void setEmailNotificationsEnabled(Boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    /**
     * Get aspectNames
     *
     * @return aspectNames
     **/
    public List<String> getAspectNames() {
        return aspectNames;
    }

    public void setAspectNames(List<String> aspectNames) {
        this.aspectNames = aspectNames;
    }

    /**
     * Get properties
     *
     * @return properties
     **/
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersonRepresentation person = (PersonRepresentation) o;
        return Objects.equals(this.id, person.id);
    }

    public CapabilitiesRepresentation getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(CapabilitiesRepresentation capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, description, avatarId, email, skypeId, googleId, instantMessageId,
                jobTitle, location, company, mobile, telephone, statusUpdatedAt, userStatus, enabled,
                emailNotificationsEnabled, aspectNames, properties);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Person {");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    firstName: ").append(toIndentedString(firstName)).append(", ");
        sb.append("    lastName: ").append(toIndentedString(lastName)).append(", ");
        sb.append("    description: ").append(toIndentedString(description)).append(", ");
        sb.append("    avatarId: ").append(toIndentedString(avatarId)).append(", ");
        sb.append("    email: ").append(toIndentedString(email)).append(", ");
        sb.append("    skypeId: ").append(toIndentedString(skypeId)).append(", ");
        sb.append("    googleId: ").append(toIndentedString(googleId)).append(", ");
        sb.append("    instantMessageId: ").append(toIndentedString(instantMessageId)).append(", ");
        sb.append("    jobTitle: ").append(toIndentedString(jobTitle)).append(", ");
        sb.append("    location: ").append(toIndentedString(location)).append(", ");
        sb.append("    company: ").append(toIndentedString(company)).append(", ");
        sb.append("    mobile: ").append(toIndentedString(mobile)).append(", ");
        sb.append("    telephone: ").append(toIndentedString(telephone)).append(", ");
        sb.append("    statusUpdatedAt: ").append(toIndentedString(statusUpdatedAt)).append(", ");
        sb.append("    userStatus: ").append(toIndentedString(userStatus)).append(", ");
        sb.append("    enabled: ").append(toIndentedString(enabled)).append(", ");
        sb.append("    emailNotificationsEnabled: ").append(toIndentedString(emailNotificationsEnabled)).append(", ");
        sb.append("    aspectNames: ").append(toIndentedString(aspectNames)).append(", ");
        sb.append("    properties: ").append(toIndentedString(properties)).append(", ");
        sb.append("    capabilities: ").append(capabilities != null ? capabilities.toString() : "{}");
        sb.append("}");
        return sb.toString();
    }

    public PersonBodyUpdate toPersonBodyUpdate() {
        return new PersonBodyUpdate(this.id, this.firstName, this.lastName, this.email, this.mobile, null);
    }

    public PersonBodyCreate toPersonBodyCreate() {
        return new PersonBodyCreate(this.id, this.firstName, this.lastName, this.email, this.mobile, this.enabled, null);
    }
}
