package net.fina.server.cems.model;

import net.fina.common.shared.KeyValuePair;
import net.fina.server.cems.entity.sanction.CEMSOrganizationType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CEMSSanctionModel {
    private long id;
    private CEMSOrganizationType organizationType;
    private String subjectLegalName;
    private String subjectID;
    private String licenseNumber;
    private String registrationLetterNumber;
    private String address;
    private String sanctionNote;
    private List<KeyValuePair<String, String>> measureOfInfluence;
    private KeyValuePair<String, String> decisionMakingBodyCatalog;
    private Date actionDate;
    private String documentNumber;
    private List<KeyValuePair<String, String>> measureReasonCatalog;
    private Date executionPeriod;
    private Date validityPeriodFrom;
    private Date validityPeriodTo;
    private Date initialCourtAppealDate;
    private String initialCourtAppealDecision;
    private Date finalCourtAppealDate;
    private String courtDecision;
    private String responsiblePersonNames;
    private String status;
    private String note;
    private List<CEMSSanctionedEmployeeInfoModel> sanctionedEmployees;
    private List<CEMSSanctionRegulationModel> regulations;

    private CEMSInspectionModel inspection;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CEMSOrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(CEMSOrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public String getSubjectLegalName() {
        return subjectLegalName;
    }

    public void setSubjectLegalName(String subjectLegalName) {
        this.subjectLegalName = subjectLegalName;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getRegistrationLetterNumber() {
        return registrationLetterNumber;
    }

    public void setRegistrationLetterNumber(String registrationLetterNumber) {
        this.registrationLetterNumber = registrationLetterNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSanctionNote() {
        return sanctionNote;
    }

    public void setSanctionNote(String sanctionNote) {
        this.sanctionNote = sanctionNote;
    }

    public List<KeyValuePair<String, String>> getMeasureOfInfluence() {
        return measureOfInfluence == null ? new ArrayList<>() : measureOfInfluence;
    }

    public void setMeasureOfInfluence(List<KeyValuePair<String, String>> measureOfInfluence) {
        this.measureOfInfluence = measureOfInfluence;
    }

    public KeyValuePair<String, String> getDecisionMakingBodyCatalog() {
        return decisionMakingBodyCatalog;
    }

    public void setDecisionMakingBodyCatalog(KeyValuePair<String, String> decisionMakingBodyCatalog) {
        this.decisionMakingBodyCatalog = decisionMakingBodyCatalog;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public List<KeyValuePair<String, String>> getMeasureReasonCatalog() {
        return measureReasonCatalog;
    }

    public void setMeasureReasonCatalog(List<KeyValuePair<String, String>> measureReasonCatalog) {
        this.measureReasonCatalog = measureReasonCatalog;
    }

    public Date getExecutionPeriod() {
        return executionPeriod;
    }

    public void setExecutionPeriod(Date executionPeriod) {
        this.executionPeriod = executionPeriod;
    }

    public Date getValidityPeriodFrom() {
        return validityPeriodFrom;
    }

    public void setValidityPeriodFrom(Date validityPeriodFrom) {
        this.validityPeriodFrom = validityPeriodFrom;
    }

    public Date getValidityPeriodTo() {
        return validityPeriodTo;
    }

    public void setValidityPeriodTo(Date validityPeriodTo) {
        this.validityPeriodTo = validityPeriodTo;
    }

    public Date getInitialCourtAppealDate() {
        return initialCourtAppealDate;
    }

    public void setInitialCourtAppealDate(Date initialCourtAppealDate) {
        this.initialCourtAppealDate = initialCourtAppealDate;
    }

    public String getInitialCourtAppealDecision() {
        return initialCourtAppealDecision;
    }

    public void setInitialCourtAppealDecision(String initialCourtAppealDecision) {
        this.initialCourtAppealDecision = initialCourtAppealDecision;
    }

    public Date getFinalCourtAppealDate() {
        return finalCourtAppealDate;
    }

    public void setFinalCourtAppealDate(Date finalCourtAppealDate) {
        this.finalCourtAppealDate = finalCourtAppealDate;
    }

    public String getCourtDecision() {
        return courtDecision;
    }

    public void setCourtDecision(String courtDecision) {
        this.courtDecision = courtDecision;
    }

    public String getResponsiblePersonNames() {
        return responsiblePersonNames;
    }

    public void setResponsiblePersonNames(String responsiblePersonNames) {
        this.responsiblePersonNames = responsiblePersonNames;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<CEMSSanctionedEmployeeInfoModel> getSanctionedEmployees() {
        return sanctionedEmployees;
    }

    public void setSanctionedEmployees(List<CEMSSanctionedEmployeeInfoModel> sanctionedEmployees) {
        this.sanctionedEmployees = sanctionedEmployees;
    }

    public List<CEMSSanctionRegulationModel> getRegulations() {
        return regulations;
    }

    public void setRegulations(List<CEMSSanctionRegulationModel> regulations) {
        this.regulations = regulations;
    }

    public CEMSInspectionModel getInspection() {
        return inspection;
    }

    public void setInspection(CEMSInspectionModel inspection) {
        this.inspection = inspection;
    }
}
