package net.fina.server.cems.model;

import java.util.Date;

public class CEMSInspectionMetaInfoModel {
    private long id;
    private Date preliminaryDiscussionDate;
    private Date finalDiscussionDate;
    private Date reportSentDate;
    private String problemDescription;
    private int numberOfOrders;
    private int numberOfOrdersAML;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getPreliminaryDiscussionDate() {
        return preliminaryDiscussionDate;
    }

    public void setPreliminaryDiscussionDate(Date preliminaryDiscussionDate) {
        this.preliminaryDiscussionDate = preliminaryDiscussionDate;
    }

    public Date getFinalDiscussionDate() {
        return finalDiscussionDate;
    }

    public void setFinalDiscussionDate(Date finalDiscussionDate) {
        this.finalDiscussionDate = finalDiscussionDate;
    }

    public Date getReportSentDate() {
        return reportSentDate;
    }

    public void setReportSentDate(Date reportSentDate) {
        this.reportSentDate = reportSentDate;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(int numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    public int getNumberOfOrdersAML() {
        return numberOfOrdersAML;
    }

    public void setNumberOfOrdersAML(int numberOfOrdersAML) {
        this.numberOfOrdersAML = numberOfOrdersAML;
    }
}
