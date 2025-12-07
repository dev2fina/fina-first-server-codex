package net.fina.first.ecm.organization.model;

import net.fina.first.ecm.node.model.NodeMetaModel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OrganizationIndividualLicenseCertificateMetaModel implements Serializable {
    private String id;
    private String uniqueNumber;
    private String status;
    private Date issueDate;
    private String resolutionDocNumber;
    private OrganizationIndividualLicenseCertificateTypeMetaModel type;
    private Date expirationDate;
    private Date suspendDate;
    private String suspendReason;
    private Date createdAt;
    private Date modifiedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniqueNumber() {
        return uniqueNumber;
    }

    public void setUniqueNumber(String uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getResolutionDocNumber() {
        return resolutionDocNumber;
    }

    public void setResolutionDocNumber(String resolutionDocNumber) {
        this.resolutionDocNumber = resolutionDocNumber;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getSuspendDate() {
        return suspendDate;
    }

    public void setSuspendDate(Date suspendDate) {
        this.suspendDate = suspendDate;
    }

    public String getSuspendReason() {
        return suspendReason;
    }

    public void setSuspendReason(String suspendReason) {
        this.suspendReason = suspendReason;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public OrganizationIndividualLicenseCertificateTypeMetaModel getType() {
        return type;
    }

    public void setType(OrganizationIndividualLicenseCertificateTypeMetaModel type) {
        this.type = type;
    }
}
