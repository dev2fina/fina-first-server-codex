package net.fina.server.cems.entity.sanction;

import net.fina.server.cems.entity.CEMSInspection;

import jakarta.persistence.*;
import java.util.*;

@Entity(name = "CEMS_SANCTIONS")
@Table(name = "CEMS_SANCTIONS")
public class CEMSSanction {
    @Id
    @SequenceGenerator(name = "cems_sanction_sequence", sequenceName = "cems_sanction_sequence", allocationSize = 1)
    @GeneratedValue(generator = "cems_sanction_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "ORGANIZATION_TYPE")
    @Enumerated(EnumType.STRING)
    private CEMSOrganizationType organizationType;

    @Column(name = "SUBJECT_LEGAL_NAME")
    private String subjectLegalName;

    @Column(name = "SUBJECT_ID")
    private String subjectID;

    @Column(name = "LICENSE_NUMBER")
    private String licenseNumber;

    @Column(name = "REG_LETTER_NUMBER")
    private String registrationLetterNumber;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "NOTE")
    private String sanctionNote;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSPECTION_ID")
    private CEMSInspection inspection;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "CEMS_SANCTION_MEASURE_INFLUENCE_TABLE", joinColumns = @JoinColumn(name = "SANCTION_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "INFLUENCE_ID", referencedColumnName = "ID"))
    private List<CEMSSanctionMeasureInfluence> measureOfInfluence;

    @Column(name = "DECISION_MAKING_BODY")
    @Enumerated(EnumType.STRING)
    private CEMSDecisionMakingBodyCatalog decisionMakingBodyCatalog;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ACTION_DATE")
    private Date actionDate;

    @Column(name = "DOCUMENT_NUMBER")
    private String documentNumber;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "CEMS_SANCTION_MEASURE_REASONS_TABLE", joinColumns = @JoinColumn(name = "SANCTION_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "REASON_ID", referencedColumnName = "ID"))
    private List<CEMSSanctionMeasureReasons> measureReasonCatalog;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EXECUTION_PERIOD")
    private Date executionPeriod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "VALIDITY_PERIOD_FROM")
    private Date validityPeriodFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "VALIDITY_PERIOD_TO")
    private Date validityPeriodTo;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "sanction_id")
    private List<CEMSSanctionRegulation> regulationList;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "sanction_id")
    private List<CEMSSanctionedEmployeeInfo> sanctionedEmployeeList;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INITIAL_COURT_APPEAL_DATE")
    private Date initialCourtAppealDate;

    @Column(name = "INITIAL_COURT_APPEAL_DECISION")
    private String initialCourtAppealDecision;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FINAl_COURT_APPEAL_DATE")
    private Date finalCourtAppealDate;

    @Column(name = "COURT_DECISION")
    private String courtDecision;

    @Column(name = "RESPONSIBLE_PERSONS")
    private String responsiblePersonNames;

    @JoinColumn(name = "STATUS")
    @OneToOne
    private CEMSSanctionStatus status;

    @Column(name = "ADDITIONAL_NOTE")
    private String note;

    public CEMSSanction() {
    }

    public CEMSSanction(long id,
                        CEMSOrganizationType organizationType,
                        String subjectLegalName,
                        String subjectID,
                        String licenseNumber,
                        String registrationLetterNumber,
                        CEMSDecisionMakingBodyCatalog decisionMakingBodyCatalog,
                        Date actionDate,
                        String documentNumber,
                        Date executionPeriod,
                        Date validityPeriodFrom,
                        Date validityPeriodTo,
                        Date initialCourtAppealDate,
                        Date finalCourtAppealDate,
                        CEMSSanctionStatus status) {
        this.id = id;
        this.organizationType = organizationType;
        this.subjectLegalName = subjectLegalName;
        this.subjectID = subjectID;
        this.licenseNumber = licenseNumber;
        this.registrationLetterNumber = registrationLetterNumber;
        this.decisionMakingBodyCatalog = decisionMakingBodyCatalog;
        this.actionDate = actionDate;
        this.documentNumber = documentNumber;
        this.executionPeriod = executionPeriod;
        this.validityPeriodFrom = validityPeriodFrom;
        this.validityPeriodTo = validityPeriodTo;
        this.initialCourtAppealDate = initialCourtAppealDate;
        this.finalCourtAppealDate = finalCourtAppealDate;
        this.status = status;
    }

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

    public List<CEMSSanctionMeasureInfluence> getMeasureOfInfluence() {
        return measureOfInfluence == null ? new ArrayList<>() : measureOfInfluence;
    }

    public void setMeasureOfInfluence(List<CEMSSanctionMeasureInfluence> measureOfInfluence) {
        this.measureOfInfluence = measureOfInfluence;
    }

    public CEMSDecisionMakingBodyCatalog getDecisionMakingBodyCatalog() {
        return decisionMakingBodyCatalog;
    }

    public void setDecisionMakingBodyCatalog(CEMSDecisionMakingBodyCatalog decisionMakingBodyCatalog) {
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

    public List<CEMSSanctionMeasureReasons> getMeasureReasonCatalog() {
        return measureReasonCatalog == null ? new ArrayList<>() : measureReasonCatalog;
    }

    public void setMeasureReasonCatalog(List<CEMSSanctionMeasureReasons> measureReasonCatalog) {
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

    public void setValidityPeriodFrom(Date validityPeriod) {
        this.validityPeriodFrom = validityPeriod;
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

    public void setInitialCourtAppealDecision(String preCourtAppealDecision) {
        this.initialCourtAppealDecision = preCourtAppealDecision;
    }

    public Date getFinalCourtAppealDate() {
        return finalCourtAppealDate;
    }

    public void setFinalCourtAppealDate(Date postCourtAppealDate) {
        this.finalCourtAppealDate = postCourtAppealDate;
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

    public CEMSSanctionStatus getStatus() {
        return status;
    }

    public void setStatus(CEMSSanctionStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<CEMSSanctionRegulation> getRegulationList() {
        return regulationList == null ? new ArrayList<>() : regulationList;
    }

    public void setRegulationList(List<CEMSSanctionRegulation> regulationList) {
        this.regulationList = regulationList;
    }

    public List<CEMSSanctionedEmployeeInfo> getSanctionedEmployeeList() {
        return sanctionedEmployeeList == null ? new ArrayList<>() : sanctionedEmployeeList;
    }

    public void setSanctionedEmployeeList(List<CEMSSanctionedEmployeeInfo> sanctionedEmployeeList) {
        this.sanctionedEmployeeList = sanctionedEmployeeList;
    }

    public CEMSInspection getInspection() {
        return inspection;
    }

    public void setInspection(CEMSInspection inspection) {
        this.inspection = inspection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CEMSSanction sanction = (CEMSSanction) o;
        return getId() == sanction.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
