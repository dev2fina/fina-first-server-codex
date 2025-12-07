package net.fina.server.fi.model;

import net.fina.server.person.model.PersonMetaModel;
import net.fina.server.util.UIConfigurationAttribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FiManagementMetaModel {
    private long id;
    private int version;
    private long fiId;

    private String firstName;
    private long firstNameStrId;

    private String lastName;
    private long lastNameStrId;

    private boolean resident;

    @UIConfigurationAttribute
    private boolean disable;

    private String postString;
    private long postStrId;

    private String registrationId1String;
    private long registrationId1StrId;
    private String registrationId2String;
    private long registrationId2StrId;
    private String registrationId3String;
    private long registrationId3StrId;

    @UIConfigurationAttribute
    private String commentId1String;
    private long commentId1StrId;
    private String commentId2String;
    private long commentId2StrId;

    @UIConfigurationAttribute
    private String phone;

    @UIConfigurationAttribute
    private Date appointmentDate;
    private Date cancelDate;

    private ManagementMetaModel managementModel;

    @UIConfigurationAttribute
    private String mail;

    @UIConfigurationAttribute
    private String address;

    @UIConfigurationAttribute
    private boolean dependencyStatus;

    @UIConfigurationAttribute
    private PersonMetaModel person;
    private long fiPersonId;
    private List<FiManagementCommitteeMetaModel> committeeList;

    @UIConfigurationAttribute
    protected String position;

    @UIConfigurationAttribute
    protected int numberOfReportingEmployees;

    @UIConfigurationAttribute
    private Date dateOfApproval;

    @UIConfigurationAttribute
    private String portfolio;

    public FiManagementMetaModel() {
    }

    public FiManagementMetaModel(String position) {
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getFiId() {
        return fiId;
    }

    public void setFiId(long fiId) {
        this.fiId = fiId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public long getFirstNameStrId() {
        return firstNameStrId;
    }

    public void setFirstNameStrId(long firstNameStrId) {
        this.firstNameStrId = firstNameStrId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getLastNameStrId() {
        return lastNameStrId;
    }

    public void setLastNameStrId(long lastNameStrId) {
        this.lastNameStrId = lastNameStrId;
    }

    public boolean isResident() {
        return resident;
    }

    public void setResident(boolean resident) {
        this.resident = resident;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getPostString() {
        return postString;
    }

    public void setPostString(String postString) {
        this.postString = postString;
    }

    public long getPostStrId() {
        return postStrId;
    }

    public void setPostStrId(long postStrId) {
        this.postStrId = postStrId;
    }

    public String getRegistrationId1String() {
        return registrationId1String;
    }

    public void setRegistrationId1String(String registrationId1String) {
        this.registrationId1String = registrationId1String;
    }

    public long getRegistrationId1StrId() {
        return registrationId1StrId;
    }

    public void setRegistrationId1StrId(long registrationId1StrId) {
        this.registrationId1StrId = registrationId1StrId;
    }

    public String getRegistrationId2String() {
        return registrationId2String;
    }

    public void setRegistrationId2String(String registrationId2String) {
        this.registrationId2String = registrationId2String;
    }

    public long getRegistrationId2StrId() {
        return registrationId2StrId;
    }

    public void setRegistrationId2StrId(long registrationId2StrId) {
        this.registrationId2StrId = registrationId2StrId;
    }

    public String getRegistrationId3String() {
        return registrationId3String;
    }

    public void setRegistrationId3String(String registrationId3String) {
        this.registrationId3String = registrationId3String;
    }

    public long getRegistrationId3StrId() {
        return registrationId3StrId;
    }

    public void setRegistrationId3StrId(long registrationId3StrId) {
        this.registrationId3StrId = registrationId3StrId;
    }

    public String getCommentId1String() {
        return commentId1String;
    }

    public void setCommentId1String(String commentId1String) {
        this.commentId1String = commentId1String;
    }

    public long getCommentId1StrId() {
        return commentId1StrId;
    }

    public void setCommentId1StrId(long commentId1StrId) {
        this.commentId1StrId = commentId1StrId;
    }

    public String getCommentId2String() {
        return commentId2String;
    }

    public void setCommentId2String(String commentId2String) {
        this.commentId2String = commentId2String;
    }

    public long getCommentId2StrId() {
        return commentId2StrId;
    }

    public void setCommentId2StrId(long commentId2StrId) {
        this.commentId2StrId = commentId2StrId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Date getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }

    public ManagementMetaModel getManagementModel() {
        return managementModel;
    }

    public void setManagementModel(ManagementMetaModel managementModel) {
        this.managementModel = managementModel;
    }


    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDependencyStatus() {
        return dependencyStatus;
    }

    public void setDependencyStatus(boolean dependencyStatus) {
        this.dependencyStatus = dependencyStatus;
    }

    public PersonMetaModel getPerson() {
        return person;
    }

    public void setPerson(PersonMetaModel person) {
        this.person = person;
    }

    public long getFiPersonId() {
        return fiPersonId;
    }

    public void setFiPersonId(long fiPersonId) {
        this.fiPersonId = fiPersonId;
    }

    public List<FiManagementCommitteeMetaModel> getCommitteeList() {
        return committeeList == null ? new ArrayList<>() : committeeList;
    }

    public void setCommitteeList(List<FiManagementCommitteeMetaModel> committeeList) {
        this.committeeList = committeeList;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getNumberOfReportingEmployees() {
        return numberOfReportingEmployees;
    }

    public void setNumberOfReportingEmployees(int numberOfReportingEmployees) {
        this.numberOfReportingEmployees = numberOfReportingEmployees;
    }

    public Date getDateOfApproval() {
        return dateOfApproval;
    }

    public void setDateOfApproval(Date dateOfApproval) {
        this.dateOfApproval = dateOfApproval;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }
}
