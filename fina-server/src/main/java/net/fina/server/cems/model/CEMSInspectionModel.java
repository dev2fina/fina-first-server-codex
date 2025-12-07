package net.fina.server.cems.model;

import net.fina.common.client.fis.FiModelSimple;
import net.fina.common.shared.KeyValuePair;
import net.fina.common.shared.user.UserModelSimple;
import net.fina.server.cems.entity.CEMSInspectionType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CEMSInspectionModel {
    private String id;
    private FiModelSimple fi;
    private CEMSInspectionType type;
    private String foundation;
    private UserModelSimple manager;
    private String managerPosition;
    private Date startDate;
    private Date endDate;
    private String info;
    private String purpose;
    private String verificationFoundation;
    private Date lastInspectionDate;
    private Date ongoingInspectionDate;
    private CEMSReportingYearInspectionModel reportingYearInspection;
    private CEMSInspectionMetaInfoModel metaInfo;
    private List<KeyValuePair<String, String>> decisions;
    private List<KeyValuePair<String, String>> recommendationList;
    private List<KeyValuePair<Long, String>> sanctionsStatuses;
    private int phase;

    public CEMSInspectionModel() {
    }

    public CEMSInspectionModel(String id, FiModelSimple fi, CEMSInspectionType type) {
        this.id = id;
        this.fi = fi;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FiModelSimple getFi() {
        return fi;
    }

    public void setFi(FiModelSimple fi) {
        this.fi = fi;
    }

    public UserModelSimple getManager() {
        return manager;
    }

    public void setManager(UserModelSimple manager) {
        this.manager = manager;
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

    public CEMSReportingYearInspectionModel getReportingYearInspection() {
        return reportingYearInspection;
    }

    public void setReportingYearInspection(CEMSReportingYearInspectionModel reportingYearInspection) {
        this.reportingYearInspection = reportingYearInspection;
    }

    public CEMSInspectionMetaInfoModel getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(CEMSInspectionMetaInfoModel metaInfo) {
        this.metaInfo = metaInfo;
    }

    public List<KeyValuePair<String, String>> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<KeyValuePair<String, String>> decisions) {
        this.decisions = decisions;
    }

    public List<KeyValuePair<String, String>> getRecommendationList() {
        return recommendationList;
    }

    public void setRecommendationList(List<KeyValuePair<String, String>> recommendationList) {
        this.recommendationList = recommendationList;
    }

    public String getManagerPosition() {
        return managerPosition;
    }

    public void setManagerPosition(String managerPosition) {
        this.managerPosition = managerPosition;
    }

    public List<KeyValuePair<Long, String>> getSanctionsStatuses() {
        return sanctionsStatuses;
    }

    public void setSanctionsStatuses(List<KeyValuePair<Long, String>> sanctionsStatuses) {
        this.sanctionsStatuses = sanctionsStatuses;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }
}
