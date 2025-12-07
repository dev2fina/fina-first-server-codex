package net.fina.server.license.model;

import net.fina.common.client.fis.FiModel;
import net.fina.common.client.fis.LicenceStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LicenseMetaModel {
    private long id;
    private String code;
    private Integer version;
    private Date creationDate;
    private Date dateOfChange;
    private LicenceStatus licenceStatus;
    private long reasonStrId;
    private String reason;
    private String change;
    private Boolean isDefault;

    private LicenseTypeMetaModel licenseType;
    private FiModel fiModel;

    private List<LicenseBankingOperationMetaModel> operations;

    private List<BankingOperationMetaModel> allBankingOperations;

    private List<LicenseCommentMetaModel> comments;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getDateOfChange() {
        return dateOfChange;
    }

    public void setDateOfChange(Date dateOfChange) {
        this.dateOfChange = dateOfChange;
    }

    public LicenceStatus getLicenceStatus() {
        return licenceStatus;
    }

    public void setLicenceStatus(LicenceStatus licenceStatus) {
        this.licenceStatus = licenceStatus;
    }

    public long getReasonStrId() {
        return reasonStrId;
    }

    public void setReasonStrId(long reasonStrId) {
        this.reasonStrId = reasonStrId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LicenseTypeMetaModel getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseTypeMetaModel licenseType) {
        this.licenseType = licenseType;
    }

    public FiModel getFiModel() {
        return fiModel;
    }

    public void setFiModel(FiModel fiModel) {
        this.fiModel = fiModel;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public List<LicenseBankingOperationMetaModel> getOperations() {
        return operations == null ? new ArrayList<>() : operations;
    }

    public void setOperations(List<LicenseBankingOperationMetaModel> operations) {
        this.operations = operations;
    }


    public List<BankingOperationMetaModel> getAllBankingOperations() {
        return allBankingOperations == null ? new ArrayList<>() : allBankingOperations;
    }

    public void setAllBankingOperations(List<BankingOperationMetaModel> allBankingOperations) {
        this.allBankingOperations = allBankingOperations;
    }

    public List<LicenseCommentMetaModel> getComments() {
        return comments == null ? new ArrayList<>() : comments;
    }

    public void setComments(List<LicenseCommentMetaModel> comments) {
        this.comments = comments;
    }
}
