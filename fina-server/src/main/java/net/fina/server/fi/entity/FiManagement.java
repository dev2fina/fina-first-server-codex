package net.fina.server.fi.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "IN_BANK_MANAGEMENT")
@Table(name = "IN_BANK_MANAGEMENT")
@org.hibernate.envers.Audited
public class FiManagement implements Audited {
    @Id
    @SequenceGenerator(name = "in_bank_management_sequence", sequenceName = "in_bank_management_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "in_bank_management_sequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description description;
    @Column(name = "LASTNAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description lastDescription;
    @OneToOne
    @JoinColumn(name = "MANAGINGBODYID")
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    protected Management management;
    @Column(name = "PHONE")
    protected String phone;
    @Column(name = "DATEOFAPPOINTMENT")
    @Temporal(TemporalType.DATE)
    protected Date appointmentDate;
    @Column(name = "DATEOFAPPROVAL")
    @Temporal(TemporalType.DATE)
    protected Date dateOfApproval;
    @Column(name = "CANCELDATE")
    @Temporal(TemporalType.DATE)
    protected Date cancelDate;
    @Column(name = "POSTSTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description post;

    @Column(name = "RESIDENT")
    protected boolean resident;
    @Column(name = "REGISTRATIONSTRID1")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description registrationId1;

    @Column(name = "REGISTRATIONSTRID2")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description registrationId2;

    @Column(name = "REGISTRATIONSTRID3")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description registrationId3;

    @Column(name = "COMMENTSSTRID1")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description commectId1;

    @Column(name = "COMMENTSSTRID2")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description commectId2;

    @Column(name = "BANKID")
    protected long fiId;

    @Column(name = "DISABLE")
    protected boolean disable;
    @Column(name = "POSITION")
    protected String position;
    @Column(name = "REPORTING_EMPLOYEES")
    protected int numberOfReportingEmployees;

    @Version
    @Column(name = "OPTLOCK")
    @NotAudited
    private Integer version;
    @OneToOne
    @JoinColumn(name = "FI_PERSON_ID", referencedColumnName = "ID")
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private FiPerson fiPerson;
    @Column(name = "MAIL")
    private String mail;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "PORTFOLIO")
    private String portfolio;
    @Column(name = "DEPENDENCY_STATUS")
    private boolean dependencyStatus;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "FI_MANAGEMENT_ID")
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @AuditJoinTable(name = "IN_BANK_MANAGEMENT_COMMITTEE_AUD")
    private List<FiManagementCommittee> fiManagementCommitteeList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Description getLastDescription() {
        return lastDescription;
    }

    public void setLastDescription(Description lastDescription) {
        this.lastDescription = lastDescription;
    }

    public Management getManagement() {
        return management;
    }

    public void setManagement(Management management) {
        this.management = management;
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

    public Description getPost() {
        return post;
    }

    public void setPost(Description post) {
        this.post = post;
    }

    public Description getRegistrationId1() {
        return registrationId1;
    }

    public void setRegistrationId1(Description registrationId1) {
        this.registrationId1 = registrationId1;
    }

    public Description getRegistrationId2() {
        return registrationId2;
    }

    public void setRegistrationId2(Description registrationId2) {
        this.registrationId2 = registrationId2;
    }

    public Description getRegistrationId3() {
        return registrationId3;
    }

    public void setRegistrationId3(Description registrationId3) {
        this.registrationId3 = registrationId3;
    }

    public Description getCommectId1() {
        return commectId1;
    }

    public void setCommectId1(Description commectId1) {
        this.commectId1 = commectId1;
    }

    public Description getCommectId2() {
        return commectId2;
    }

    public void setCommectId2(Description commectId2) {
        this.commectId2 = commectId2;
    }

    public long getFiId() {
        return fiId;
    }

    public void setFiId(long fiId) {
        this.fiId = fiId;
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

    public FiPerson getFiPerson() {
        return fiPerson;
    }

    public void setFiPerson(FiPerson fiPerson) {
        this.fiPerson = fiPerson;
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

    public List<FiManagementCommittee> getFiManagementCommitteeList() {
        return fiManagementCommitteeList == null ? new ArrayList<>() : fiManagementCommitteeList;
    }

    public void setFiManagementCommitteeList(List<FiManagementCommittee> fiManagementCommitteeList) {
        this.fiManagementCommitteeList = fiManagementCommitteeList;
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

    @Override
    public String toString() {
        return "FiManagement{" +
                "id=" + id +
                ", version=" + version +
                ", description=" + description +
                ", lastDescription=" + lastDescription +
                ", management=" + management +
                ", phone='" + phone + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", dateOfApproval=" + dateOfApproval +
                ", cancelDate=" + cancelDate +
                ", post=" + post +
                ", resident=" + resident +
                ", registrationId1=" + registrationId1 +
                ", registrationId2=" + registrationId2 +
                ", registrationId3=" + registrationId3 +
                ", commectId1=" + commectId1 +
                ", commectId2=" + commectId2 +
                ", fiId=" + fiId +
                ", disable=" + disable +
                ", fiPerson=" + fiPerson +
                ", mail='" + mail + '\'' +
                ", address='" + address + '\'' +
                ", portfolio='" + portfolio + '\'' +
                ", dependencyStatus=" + dependencyStatus +
                ", fiManagementCommitteeList=" + fiManagementCommitteeList +
                ", position='" + position + '\'' +
                ", numberOfReportingEmployees=" + numberOfReportingEmployees +
                '}';
    }
}
