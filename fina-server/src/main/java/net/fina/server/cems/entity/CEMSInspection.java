package net.fina.server.cems.entity;

import net.fina.server.cems.entity.sanction.CEMSSanction;
import net.fina.server.fi.entity.Fi;
import net.fina.server.security.entity.User;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "CEMS_INSPECTIONS")
@Table(name = "CEMS_INSPECTIONS")
public class CEMSInspection {

    @Id
    private String id;
    @OneToOne
    @JoinColumn(name = "FI_ID")
    private Fi fi;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "TYPE")
    private CEMSInspectionType type;

    @Column(name = "FOUNDATION")
    private String foundation;

    @OneToOne
    @JoinColumn(name = "MANAGER_ID")
    private User manager;

    @Column(name = "MANAGER_POSITION")
    private String managerPosition;

    @Column(name = "START_DATE")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "END_DATE")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "INFO")
    private String info;

    @Column(name = "PURPOSE")
    private String purpose;

    @Column(name = "VERIFICATION_FOUNDATION")
    private String verificationFoundation;

    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_INSPECTION_DATE")
    private Date lastInspectionDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "ONGOING_INSPECTION_DATE")
    private Date ongoingInspectionDate;
    @Column(name = "PHASE")
    private int phase;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "REPORTING_YEAR_ID")
    private CEMSReportingYearInspection reportingYearInspection;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "META_INFO_ID")
    private CEMSInspectionMetaInfo metaInfo;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSPECTION_ID")
    private List<CEMSRecommendation> recommendations;

    @Column(name = "RECORD_CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordCreateDate;

    @OneToMany
    @JoinColumn(name = "INSPECTION_ID")
    private List<CEMSSanction> sanctionList;

    public CEMSInspection() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Fi getFi() {
        return fi;
    }

    public void setFi(Fi fi) {
        this.fi = fi;
    }

    public CEMSInspectionType getType() {
        return type;
    }

    public void setType(CEMSInspectionType type) {
        this.type = type;
    }

    public String getFoundation() {
        return foundation;
    }

    public void setFoundation(String foundation) {
        this.foundation = foundation;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getVerificationFoundation() {
        return verificationFoundation;
    }

    public void setVerificationFoundation(String verificationFoundation) {
        this.verificationFoundation = verificationFoundation;
    }

    public Date getLastInspectionDate() {
        return lastInspectionDate;
    }

    public void setLastInspectionDate(Date lastInspectionDate) {
        this.lastInspectionDate = lastInspectionDate;
    }

    public Date getOngoingInspectionDate() {
        return ongoingInspectionDate;
    }

    public void setOngoingInspectionDate(Date ongoingInspectionDate) {
        this.ongoingInspectionDate = ongoingInspectionDate;
    }

    public CEMSReportingYearInspection getReportingYearInspection() {
        return reportingYearInspection;
    }

    public void setReportingYearInspection(CEMSReportingYearInspection reportingYearInspection) {
        this.reportingYearInspection = reportingYearInspection;
    }

    public CEMSInspectionMetaInfo getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(CEMSInspectionMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    public List<CEMSRecommendation> getRecommendations() {
        return recommendations == null ? new ArrayList<>() : recommendations;
    }

    public void setRecommendations(List<CEMSRecommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public Date getRecordCreateDate() {
        return recordCreateDate;
    }

    public void setRecordCreateDate(Date recordCreateDate) {
        this.recordCreateDate = recordCreateDate;
    }

    public String getManagerPosition() {
        return managerPosition;
    }

    public void setManagerPosition(String managerPosition) {
        this.managerPosition = managerPosition;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public List<CEMSSanction> getSanctionList() {
        return sanctionList == null ? new ArrayList<>() : sanctionList;
    }

    public void setSanctionList(List<CEMSSanction> sanctionList) {
        this.sanctionList = sanctionList;
    }
}
