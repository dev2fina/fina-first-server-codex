package net.fina.server.cems.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity(name = "CEMS_INSPECTIONS_META_INFO")
@Table(name = "CEMS_INSPECTIONS_META_INFO")
public class CEMSInspectionMetaInfo {

    @Id
    @SequenceGenerator(name = "cems_inspection_meta_info", sequenceName = "cems_inspection_meta_info", allocationSize = 1)
    @GeneratedValue(generator = "cems_inspection_meta_info", strategy = GenerationType.SEQUENCE)
    private long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "PRELIMINARY_DISCUSSION_DATE")
    private Date preliminaryDiscussionDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "FINAL_DISCUSSION_DATE")
    private Date finalDiscussionDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "REPORT_SENT_DATE")
    private Date reportSentDate;

    @Column(name = "PROBLEM")
    private String problemDescription;

    @Column(name = "NUMBER_OF_ORDERS")
    private int numberOfOrders;

    @Column(name = "NUMBER_OF_ORDERS_AML")
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
