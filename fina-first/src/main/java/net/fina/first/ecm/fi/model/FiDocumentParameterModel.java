package net.fina.first.ecm.fi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.first.ecm.registry.model.FiRegistryActionType;
import net.fina.first.ecm.registry.model.FiRegistryDocumentType;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiDocumentParameterModel implements Serializable {
    private String fiRegistryId;
    private String fiActionId;
    private String processId;
    private FiRegistryActionType actionType;
    private FiRegistryDocumentType documentType;
    private String documentNumber;
    private String documentDate;
    private String fiTypeCode;
    private String branchId;
    private Boolean keepExistingDocumentType;
    private String langCode = "en_US";
    boolean filterActiveBranches;

    public FiDocumentParameterModel() {
    }

    public FiDocumentParameterModel(String fiRegistryId, String fiActionId, String processId, FiRegistryActionType actionType, FiRegistryDocumentType documentType, String fiTypeCode, String documentNumber, String documentDate, String branchId, Boolean keepExistingDocumentType, String langCode) {
        this.fiRegistryId = fiRegistryId;
        this.fiActionId = fiActionId;
        this.processId = processId;
        this.actionType = actionType;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.documentDate = documentDate;
        this.fiTypeCode = fiTypeCode;
        this.branchId = branchId;
        this.keepExistingDocumentType = keepExistingDocumentType;
        this.langCode = langCode;
    }

    public String getFiRegistryId() {
        return fiRegistryId;
    }

    public void setFiRegistryId(String fiRegistryId) {
        this.fiRegistryId = fiRegistryId;
    }

    public String getFiActionId() {
        return fiActionId;
    }

    public void setFiActionId(String fiActionId) {
        this.fiActionId = fiActionId;
    }

    public FiRegistryActionType getActionType() {
        return actionType;
    }

    public void setActionType(FiRegistryActionType actionType) {
        this.actionType = actionType;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public FiRegistryDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(FiRegistryDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getFiTypeCode() {
        return fiTypeCode;
    }

    public void setFiTypeCode(String fiTypeCode) {
        this.fiTypeCode = fiTypeCode;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public Boolean getKeepExistingDocumentType() {
        return keepExistingDocumentType;
    }

    public void setKeepExistingDocumentType(Boolean keepExistingDocumentType) {
        this.keepExistingDocumentType = keepExistingDocumentType;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public boolean isFilterActiveBranches() {
        return filterActiveBranches;
    }

    public void setFilterActiveBranches(boolean filterActiveBranches) {
        this.filterActiveBranches = filterActiveBranches;
    }
}
