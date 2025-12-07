package net.fina.common.shared.fi;

import net.fina.common.shared.i18n.DescriptionMetaModel;

import java.util.Date;

public class FiManagementMetaModel {
    private long id;
    private Integer version;
    protected DescriptionMetaModel description;
    protected DescriptionMetaModel lastDescription;
    protected ManagementMetaModel management;
    protected String phone;
    protected Date appointmentDate;
    protected Date cancelDate;
    protected DescriptionMetaModel post;
    protected boolean resident;
    protected DescriptionMetaModel registrationId1;
    protected DescriptionMetaModel registrationId2;
    protected DescriptionMetaModel registrationId3;
    protected DescriptionMetaModel commectId1;
    protected DescriptionMetaModel commectId2;
    protected long fiId;

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

    public DescriptionMetaModel getDescription() {
        return description;
    }

    public void setDescription(DescriptionMetaModel description) {
        this.description = description;
    }

    public DescriptionMetaModel getLastDescription() {
        return lastDescription;
    }

    public void setLastDescription(DescriptionMetaModel lastDescription) {
        this.lastDescription = lastDescription;
    }

    public ManagementMetaModel getManagement() {
        return management;
    }

    public void setManagement(ManagementMetaModel management) {
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

    public DescriptionMetaModel getPost() {
        return post;
    }

    public void setPost(DescriptionMetaModel post) {
        this.post = post;
    }

    public boolean isResident() {
        return resident;
    }

    public void setResident(boolean resident) {
        this.resident = resident;
    }

    public DescriptionMetaModel getRegistrationId1() {
        return registrationId1;
    }

    public void setRegistrationId1(DescriptionMetaModel registrationId1) {
        this.registrationId1 = registrationId1;
    }

    public DescriptionMetaModel getRegistrationId2() {
        return registrationId2;
    }

    public void setRegistrationId2(DescriptionMetaModel registrationId2) {
        this.registrationId2 = registrationId2;
    }

    public DescriptionMetaModel getRegistrationId3() {
        return registrationId3;
    }

    public void setRegistrationId3(DescriptionMetaModel registrationId3) {
        this.registrationId3 = registrationId3;
    }

    public DescriptionMetaModel getCommectId1() {
        return commectId1;
    }

    public void setCommectId1(DescriptionMetaModel commectId1) {
        this.commectId1 = commectId1;
    }

    public DescriptionMetaModel getCommectId2() {
        return commectId2;
    }

    public void setCommectId2(DescriptionMetaModel commectId2) {
        this.commectId2 = commectId2;
    }

    public long getFiId() {
        return fiId;
    }

    public void setFiId(long fiId) {
        this.fiId = fiId;
    }

    @Override
    public String toString() {
        return "FiManagement{" +
                "id=" + id +
                ", version=" + version +
                ", description=" + description +
                ", lastDescription=" + lastDescription +
                ", phone='" + phone + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", cancelDate=" + cancelDate +
                ", post=" + post +
                ", registrationId1=" + registrationId1 +
                ", registrationId2=" + registrationId2 +
                ", registrationId3=" + registrationId3 +
                ", commectId1=" + commectId1 +
                ", commectId2=" + commectId2 +
                ", fiId=" + fiId +
                ", resident=" + resident +
                '}';
    }
}
