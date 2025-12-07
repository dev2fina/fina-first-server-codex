package net.fina.first.ecm.registry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.common.shared.ecm.model.ECMPersonMetaModel;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiRegistryMetaModel {

    private String id;
    private String identity;
    private String lastProcessId;
    private Date lastActionDate;
    private String fiTypeCode;
    private String code;
    private String name;
    private FiRegistryActionType actionType;
    private String author;
    private Date createdAt;
    private String status;
    private float progress;
    private String lastActionId;
    private ECMPersonMetaModel lastInspector;
    private String controlStatus;
    private String redactingStatus;
    private ECMPersonMetaModel lastEditor;
    private FiRegistryLicenseStatus licenseStatus = FiRegistryLicenseStatus.INACTIVE;
    private String cancellationReason;
    private boolean isHistoricData;
    private boolean leaf = true;
    private boolean checked;
    private String phone;
    private String mail;
    private String binder;
    private int archivedGapTaskCount;
    private String legalFormType;
    private String legalAddressRegion;
    private String legalAddressCity;
    private String legalAddress;
    private Date registrationDate;
    private String lastLegalActNumber;
    private Date lastLegalActDate;
    private String directorFullName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getLastProcessId() {
        return lastProcessId;
    }

    public void setLastProcessId(String lastProcessId) {
        this.lastProcessId = lastProcessId;
    }

    public Date getLastActionDate() {
        return lastActionDate != null ? new Date(lastActionDate.getTime()) : null;
    }

    public void setLastActionDate(Date lastActionDate) {
        this.lastActionDate = lastActionDate;
    }

    public String getFiTypeCode() {
        return fiTypeCode;
    }

    public void setFiTypeCode(String fiTypeCode) {
        this.fiTypeCode = fiTypeCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FiRegistryActionType getActionType() {
        return actionType;
    }

    public void setActionType(FiRegistryActionType actionType) {
        this.actionType = actionType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getLastActionId() {
        return lastActionId;
    }

    public void setLastActionId(String lastActionId) {
        this.lastActionId = lastActionId;
    }

    public ECMPersonMetaModel getLastInspector() {
        return lastInspector;
    }

    public void setLastInspector(ECMPersonMetaModel lastInspector) {
        this.lastInspector = lastInspector;
    }

    public String getControlStatus() {
        return controlStatus;
    }

    public void setControlStatus(String controlStatus) {
        this.controlStatus = controlStatus;
    }

    public String getRedactingStatus() {
        return redactingStatus;
    }

    public void setRedactingStatus(String redactingStatus) {
        this.redactingStatus = redactingStatus;
    }

    public ECMPersonMetaModel getLastEditor() {
        return lastEditor;
    }

    public void setLastEditor(ECMPersonMetaModel lastEditor) {
        this.lastEditor = lastEditor;
    }

    public FiRegistryLicenseStatus getLicenseStatus() {
        return licenseStatus;
    }

    public void setLicenseStatus(FiRegistryLicenseStatus licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public boolean getIsHistoricData() {
        return isHistoricData;
    }

    public void setIsHistoricData(boolean isHistoricData) {
        this.isHistoricData = isHistoricData;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getLegalAddress() {
        return legalAddress;
    }

    public void setLegalAddress(String legalAddress) {
        this.legalAddress = legalAddress;
    }

    public boolean isHistoricData() {
        return isHistoricData;
    }

    public void setHistoricData(boolean historicData) {
        isHistoricData = historicData;
    }

    public String getBinder() {
        return binder;
    }

    public void setBinder(String binder) {
        this.binder = binder;
    }

    public int getArchivedGapTaskCount() {
        return archivedGapTaskCount;
    }

    public void setArchivedGapTaskCount(int archivedGapTaskCount) {
        this.archivedGapTaskCount = archivedGapTaskCount;
    }

    public String getLegalFormType() {
        return legalFormType;
    }

    public void setLegalFormType(String legalFormType) {
        this.legalFormType = legalFormType;
    }

    public String getLegalAddressRegion() {
        return legalAddressRegion;
    }

    public void setLegalAddressRegion(String legalAddressRegion) {
        this.legalAddressRegion = legalAddressRegion;
    }

    public String getLegalAddressCity() {
        return legalAddressCity;
    }

    public void setLegalAddressCity(String legalAddressCity) {
        this.legalAddressCity = legalAddressCity;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastLegalActDate() {
        return lastLegalActDate;
    }

    public void setLastLegalActDate(Date lastLegalActDate) {
        this.lastLegalActDate = lastLegalActDate;
    }

    public String getLastLegalActNumber() {
        return lastLegalActNumber;
    }

    public void setLastLegalActNumber(String lastLegalActNumber) {
        this.lastLegalActNumber = lastLegalActNumber;
    }

    public String getDirectorFullName() {
        return directorFullName;
    }

    public void setDirectorFullName(String directorFullName) {
        this.directorFullName = directorFullName;
    }
}
