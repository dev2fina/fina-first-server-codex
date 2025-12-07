package net.fina.first.ecm.fi.model;

import java.io.Serializable;
import java.util.List;

public class FiTypeMetaModel implements Serializable {
    private String id;
    private String code;
    private String description;
    private String registrationWorkflowKey;
    private String changeWorkflowKey;
    private String disableWorkflowKey;
    private String branchChangeWorkflowKey;
    private String branchEditWorkflowKey;
    private String documentWithdrawalWorkflowKey;
    private List<String> branchTypes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegistrationWorkflowKey() {
        return registrationWorkflowKey;
    }

    public void setRegistrationWorkflowKey(String registrationWorkflowKey) {
        this.registrationWorkflowKey = registrationWorkflowKey;
    }

    public String getChangeWorkflowKey() {
        return changeWorkflowKey;
    }

    public void setChangeWorkflowKey(String changeWorkflowKey) {
        this.changeWorkflowKey = changeWorkflowKey;
    }

    public String getDisableWorkflowKey() {
        return disableWorkflowKey;
    }

    public void setDisableWorkflowKey(String disableWorkflowKey) {
        this.disableWorkflowKey = disableWorkflowKey;
    }

    public String getBranchChangeWorkflowKey() {
        return branchChangeWorkflowKey;
    }

    public void setBranchChangeWorkflowKey(String branchChangeWorkflowKey) {
        this.branchChangeWorkflowKey = branchChangeWorkflowKey;
    }

    public String getBranchEditWorkflowKey() {
        return branchEditWorkflowKey;
    }

    public void setBranchEditWorkflowKey(String branchEditWorkflowKey) {
        this.branchEditWorkflowKey = branchEditWorkflowKey;
    }

    public String getDocumentWithdrawalWorkflowKey() {
        return documentWithdrawalWorkflowKey;
    }

    public void setDocumentWithdrawalWorkflowKey(String documentWithdrawalWorkflowKey) {
        this.documentWithdrawalWorkflowKey = documentWithdrawalWorkflowKey;
    }

    public List<String> getBranchTypes() {
        return branchTypes;
    }

    public void setBranchTypes(List<String> branchTypes) {
        this.branchTypes = branchTypes;
    }
}
