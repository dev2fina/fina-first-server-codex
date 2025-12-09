package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonBodyUpdate implements BaseRepresentation {
    @JsonIgnore
    private String id;
    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("email")
    private String email;

    @JsonProperty("skypeId")
    private String skypeId;

    @JsonProperty("googleId")
    private String googleId;

    @JsonProperty("instantMessageId")
    private String instantMessageId;

    @JsonProperty("jobTitle")
    private String jobTitle;

    @JsonProperty("location")
    private String location;

    @JsonProperty("company")
    private CompanyBody company;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("userStatus")
    private String userStatus;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("emailNotificationsEnabled")
    private Boolean emailNotificationsEnabled;

    @JsonProperty("password")
    private String password;

    @JsonProperty("oldPassword")
    private String oldPassword;

    @JsonProperty("aspectNames")
    private List<String> aspectNames = new ArrayList<String>();

    @JsonProperty("properties")
    private Map<String, String> properties = new HashMap<String, String>();

    public PersonBodyUpdate() {
    }

    public PersonBodyUpdate(String id, String firstName, String lastName, String email, String mobile, String password) {
        this.id = id;
        this.firstName = firstName != null && !firstName.trim().isEmpty() ? firstName : "NONAME";
        this.lastName = lastName != null && !lastName.trim().isEmpty() ? lastName : "NONAME";
        this.email = email != null && !email.trim().isEmpty() ? email : "NONAME";
        this.mobile=mobile;
        this.password = password;
    }

    public PersonBodyUpdate firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

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

    public PersonBodyUpdate lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    /**
     * Get lastName
     *
     * @return lastName
     **/
    public String getLastName() {
        return lastName;
    }

    public PersonBodyUpdate description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     **/
    public String getDescription() {
        return description;
    }

    public PersonBodyUpdate email(String email) {
        this.email = email;
        return this;
    }

    /**
     * Get email
     *
     * @return email
     **/
    public String getEmail() {
        return email;
    }

    public PersonBodyUpdate skypeId(String skypeId) {
        this.skypeId = skypeId;
        return this;
    }

    /**
     * Get skypeId
     *
     * @return skypeId
     **/
    public String getSkypeId() {
        return skypeId;
    }

    public PersonBodyUpdate googleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

    /**
     * Get googleId
     *
     * @return googleId
     **/
    public String getGoogleId() {
        return googleId;
    }

    public PersonBodyUpdate instantMessageId(String instantMessageId) {
        this.instantMessageId = instantMessageId;
        return this;
    }

    /**
     * Get instantMessageId
     *
     * @return instantMessageId
     **/
    public String getInstantMessageId() {
        return instantMessageId;
    }

    public PersonBodyUpdate jobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }

    /**
     * Get jobTitle
     *
     * @return jobTitle
     **/
    public String getJobTitle() {
        return jobTitle;
    }

    public PersonBodyUpdate location(String location) {
        this.location = location;
        return this;
    }

    /**
     * Get location
     *
     * @return location
     **/
    public String getLocation() {
        return location;
    }

    public PersonBodyUpdate company(CompanyBody company) {
        this.company = company;
        return this;
    }

    /**
     * Get company
     *
     * @return company
     **/
    public CompanyBody getCompany() {
        return company;
    }

    public PersonBodyUpdate mobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    /**
     * Get mobile
     *
     * @return mobile
     **/
    public String getMobile() {
        return mobile;
    }

    public PersonBodyUpdate telephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    /**
     * Get telephone
     *
     * @return telephone
     **/
    public String getTelephone() {
        return telephone;
    }

    public PersonBodyUpdate userStatus(String userStatus) {
        this.userStatus = userStatus;
        return this;
    }

    /**
     * Get userStatus
     *
     * @return userStatus
     **/
    public String getUserStatus() {
        return userStatus;
    }

    public PersonBodyUpdate enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Get enabled
     *
     * @return enabled
     **/
    public Boolean getEnabled() {
        return enabled;
    }

    public PersonBodyUpdate emailNotificationsEnabled(Boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        return this;
    }

    /**
     * Get emailNotificationsEnabled
     *
     * @return emailNotificationsEnabled
     **/
    public Boolean getEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public PersonBodyUpdate password(String password) {
        this.password = password;
        return this;
    }

    /**
     * Get password
     *
     * @return password
     **/
    public String getPassword() {
        return password;
    }

    public PersonBodyUpdate oldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
        return this;
    }

    /**
     * Get oldPassword
     *
     * @return oldPassword
     **/
    public String getOldPassword() {
        return oldPassword;
    }

    public PersonBodyUpdate aspectNames(List<String> aspectNames) {
        this.aspectNames = aspectNames;
        return this;
    }

    public PersonBodyUpdate addAspectNamesItem(String aspectNamesItem) {
        this.aspectNames.add(aspectNamesItem);
        return this;
    }

    /**
     * Get aspectNames
     *
     * @return aspectNames
     **/
    public List<String> getAspectNames() {
        return aspectNames;
    }

    public PersonBodyUpdate properties(Map<String, String> properties) {
        this.properties = properties;
        return this;
    }

    public PersonBodyUpdate putPropertiesItem(String key, String propertiesItem) {
        this.properties.put(key, propertiesItem);
        return this;
    }

    /**
     * Get properties
     *
     * @return properties
     **/
    public Map<String, String> getProperties() {
        return properties;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSkypeId(String skypeId) {
        this.skypeId = skypeId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setInstantMessageId(String instantMessageId) {
        this.instantMessageId = instantMessageId;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCompany(CompanyBody company) {
        this.company = company;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setEmailNotificationsEnabled(Boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setAspectNames(List<String> aspectNames) {
        this.aspectNames = aspectNames;
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
        PersonBodyUpdate personBodyUpdate = (PersonBodyUpdate) o;
        return Objects.equals(this.firstName, personBodyUpdate.firstName)
                && Objects.equals(this.lastName, personBodyUpdate.lastName)
                && Objects.equals(this.description, personBodyUpdate.description)
                && Objects.equals(this.email, personBodyUpdate.email)
                && Objects.equals(this.skypeId, personBodyUpdate.skypeId)
                && Objects.equals(this.googleId, personBodyUpdate.googleId)
                && Objects.equals(this.instantMessageId, personBodyUpdate.instantMessageId)
                && Objects.equals(this.jobTitle, personBodyUpdate.jobTitle)
                && Objects.equals(this.location, personBodyUpdate.location)
                && Objects.equals(this.company, personBodyUpdate.company)
                && Objects.equals(this.mobile, personBodyUpdate.mobile)
                && Objects.equals(this.telephone, personBodyUpdate.telephone)
                && Objects.equals(this.userStatus, personBodyUpdate.userStatus)
                && Objects.equals(this.enabled, personBodyUpdate.enabled)
                && Objects.equals(this.emailNotificationsEnabled, personBodyUpdate.emailNotificationsEnabled)
                && Objects.equals(this.password, personBodyUpdate.password)
                && Objects.equals(this.oldPassword, personBodyUpdate.oldPassword)
                && Objects.equals(this.aspectNames, personBodyUpdate.aspectNames)
                && Objects.equals(this.properties, personBodyUpdate.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, description, email, skypeId, googleId, instantMessageId, jobTitle,
                location, company, mobile, telephone, userStatus, enabled, emailNotificationsEnabled, password,
                oldPassword, aspectNames, properties);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PersonBodyUpdate {");

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
        sb.append("    oldPassword: ").append(toIndentedString(oldPassword)).append(", ");
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
}
