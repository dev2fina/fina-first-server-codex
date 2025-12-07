package net.fina.common.server.fi.sync.model;

import java.util.Date;
import java.util.List;

public class FiManagementSyncMetaModel {
    private List<FiDescriptionSyncMetaModel> firstNameDescriptions;
    private List<FiDescriptionSyncMetaModel> lastNameDescriptions;
    private List<FiDescriptionSyncMetaModel> positionDescriptions;
    private String phone;
    private Date appointmentDate;
    private Date cancellationDate;
    private boolean isResident;
    private String regForm1;
    private String regForm2;
    private String comment1;
    private String comment2;
    private String personalNumber;
    private long managementBodyId;
    private String finalStatus;
    private String nonResidentDocumentNumber;

    public List<FiDescriptionSyncMetaModel> getFirstNameDescriptions() {
        return firstNameDescriptions;
    }

    public void setFirstNameDescriptions(List<FiDescriptionSyncMetaModel> firstNameDescriptions) {
        this.firstNameDescriptions = firstNameDescriptions;
    }

    public List<FiDescriptionSyncMetaModel> getLastNameDescriptions() {
        return lastNameDescriptions;
    }

    public void setLastNameDescriptions(List<FiDescriptionSyncMetaModel> lastNameDescriptions) {
        this.lastNameDescriptions = lastNameDescriptions;
    }

    public List<FiDescriptionSyncMetaModel> getPositionDescriptions() {
        return positionDescriptions;
    }

    public void setPositionDescriptions(List<FiDescriptionSyncMetaModel> positionDescriptions) {
        this.positionDescriptions = positionDescriptions;
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

    public Date getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(Date cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public boolean isResident() {
        return isResident;
    }

    public void setResident(boolean resident) {
        isResident = resident;
    }

    public String getRegForm1() {
        return regForm1;
    }

    public void setRegForm1(String regForm1) {
        this.regForm1 = regForm1;
    }

    public String getRegForm2() {
        return regForm2;
    }

    public void setRegForm2(String regForm2) {
        this.regForm2 = regForm2;
    }

    public String getComment1() {
        return comment1;
    }

    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    public String getComment2() {
        return comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public long getManagementBodyId() {
        return managementBodyId;
    }

    public void setManagementBodyId(long managementBodyId) {
        this.managementBodyId = managementBodyId;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(String finalStatus) {
        this.finalStatus = finalStatus;
    }

    public String getNonResidentDocumentNumber() {
        return nonResidentDocumentNumber;
    }

    public void setNonResidentDocumentNumber(String nonResidentDocumentNumber) {
        this.nonResidentDocumentNumber = nonResidentDocumentNumber;
    }

}
