package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonBodyCreate implements BaseRepresentation {
    private String id = null;
    private String firstName = null;
    private String lastName = null;
    private String description = null;
    private String email = null;
    private String skypeId = null;
    private String googleId = null;
    private String instantMessageId = null;
    private String jobTitle = null;
    private String location = null;
    private CompanyBody company = null;
    private String mobile = null;
    private String telephone = null;
    private String userStatus = null;
    private Boolean enabled = true;
    private Boolean emailNotificationsEnabled = true;
    private String password = null;
    private List<String> aspectNames = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();

    public PersonBodyCreate() {
    }

    public PersonBodyCreate(String id, String firstName, String lastName, String email, String mobile, Boolean enabled, String password) {
        this.id = id;
        this.firstName = firstName != null && !firstName.trim().isEmpty() ? firstName : "NONAME";
        this.lastName = lastName != null && !lastName.trim().isEmpty() ? lastName : "NONAME";
        this.email = email != null && !email.trim().isEmpty() ? email : "NONAME";
        this.mobile = mobile;
        this.enabled = enabled;
        this.password = password == null || password.trim().isEmpty() ? UUID.randomUUID().toString() : password;
    }

    public PersonBodyCreate(String id, String firstName, String lastName, String email, String mobile) {
        this.id = id;
        this.firstName = firstName != null && !firstName.trim().isEmpty() ? firstName : "NONAME";
        this.lastName = lastName != null && !lastName.trim().isEmpty() ? lastName : "NONAME";
        this.email = email != null && !email.trim().isEmpty() ? email : "NONAME";
        this.mobile = mobile;
        this.enabled = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSkypeId() {
        return skypeId;
    }

    public void setSkypeId(String skypeId) {
        this.skypeId = skypeId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getInstantMessageId() {
        return instantMessageId;
    }

    public void setInstantMessageId(String instantMessageId) {
        this.instantMessageId = instantMessageId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public CompanyBody getCompany() {
        return company;
    }

    public void setCompany(CompanyBody company) {
        this.company = company;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public void setEmailNotificationsEnabled(Boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getAspectNames() {
        return aspectNames;
    }

    public void setAspectNames(List<String> aspectNames) {
        this.aspectNames = aspectNames;
    }

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
        PersonBodyCreate personBodyCreate = (PersonBodyCreate) o;
        return Objects.equals(this.id, personBodyCreate.id)
                && Objects.equals(this.firstName, personBodyCreate.firstName)
                && Objects.equals(this.lastName, personBodyCreate.lastName)
                && Objects.equals(this.description, personBodyCreate.description)
                && Objects.equals(this.email, personBodyCreate.email)
                && Objects.equals(this.skypeId, personBodyCreate.skypeId)
                && Objects.equals(this.googleId, personBodyCreate.googleId)
                && Objects.equals(this.instantMessageId, personBodyCreate.instantMessageId)
                && Objects.equals(this.jobTitle, personBodyCreate.jobTitle)
                && Objects.equals(this.location, personBodyCreate.location)
                && Objects.equals(this.company, personBodyCreate.company)
                && Objects.equals(this.mobile, personBodyCreate.mobile)
                && Objects.equals(this.telephone, personBodyCreate.telephone)
                && Objects.equals(this.userStatus, personBodyCreate.userStatus)
                && Objects.equals(this.enabled, personBodyCreate.enabled)
                && Objects.equals(this.emailNotificationsEnabled, personBodyCreate.emailNotificationsEnabled)
                && Objects.equals(this.password, personBodyCreate.password)
                && Objects.equals(this.aspectNames, personBodyCreate.aspectNames)
                && Objects.equals(this.properties, personBodyCreate.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, description, email, skypeId, googleId, instantMessageId, jobTitle,
                location, company, mobile, telephone, userStatus, enabled, emailNotificationsEnabled, password,
                aspectNames, properties);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PersonBodyCreate {");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    firstName: ").append(toIndentedString(firstName)).append(", ");
        sb.append("    lastName: ").append(toIndentedString(lastName)).append(", ");
        sb.append("    description: ").append(toIndentedString(description)).append(", ");
        sb.append("    email: ").append(toIndentedString(email)).append(", ");
        sb.append("    skypeId: ").append(toIndentedString(skypeId)).append(", ");
        sb.append("    googleId: ").append(toIndentedString(googleId)).append(", ");
        sb.append("    instantMessageId: ").append(toIndentedString(instantMessageId)).append(", ");
        sb.append("    jobTitle: ").append(toIndentedString(jobTitle)).append(", ");
        sb.append("    location: ").append(toIndentedString(location)).append(", ");
        sb.append("    company: ").append(toIndentedString(company)).append(", ");
        sb.append("    mobile: ").append(toIndentedString(mobile)).append(", ");
        sb.append("    telephone: ").append(toIndentedString(telephone)).append(", ");
        sb.append("    userStatus: ").append(toIndentedString(userStatus)).append(", ");
        sb.append("    enabled: ").append(toIndentedString(enabled)).append(", ");
        sb.append("    emailNotificationsEnabled: ").append(toIndentedString(emailNotificationsEnabled)).append(", ");
        sb.append("    password: ").append(toIndentedString(password)).append(", ");
        sb.append("    aspectNames: ").append(toIndentedString(aspectNames)).append(", ");
        sb.append("    properties: ").append(toIndentedString(properties));
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }


    PersonRepresentation toRepresentation() {
        return new PersonRepresentation(this.id, this.email, this.mobile, this.enabled, this.firstName, this.lastName);
    }
}
