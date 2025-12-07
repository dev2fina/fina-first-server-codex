package net.fina.server.cems.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "CEMS_RECOMMENDATIONS")
@Table(name = "CEMS_RECOMMENDATIONS")
public class CEMSRecommendation {
    @Id
    private String id;

    @Column(name = "TYPE")
    @Enumerated(EnumType.ORDINAL)
    private CEMSRecommendationType type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LETTER_DATE")
    private Date letterDate;

    @Column(name = "LETTER_INFO")
    private String letterInfo;

    @Column(name = "NUMBER")
    private int number;

    @Column(name = "ORDER_CONTENT")
    private String orderContent;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EXECUTION_PERIOD")
    private Date executionPeriod;

    @OneToMany(orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "RECOMMENDATION_ID")
    private List<CEMSResponsiblePerson> fiResponsiblePersons;

    @Embedded
    private CEMSRecommendationStatusInfo statusInfo;

    @Column(name = "RECORD_CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordCreateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSPECTION_ID")
    private CEMSInspection inspection;

    public CEMSRecommendation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CEMSRecommendationType getType() {
        return type;
    }

    public void setType(CEMSRecommendationType type) {
        this.type = type;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLetterDate() {
        return letterDate;
    }

    public void setLetterDate(Date letterDate) {
        this.letterDate = letterDate;
    }

    public String getLetterInfo() {
        return letterInfo==null?"":letterInfo;
    }

    public void setLetterInfo(String letterInfo) {
        this.letterInfo = letterInfo;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(String orderContent) {
        this.orderContent = orderContent;
    }

    public Date getExecutionPeriod() {
        return executionPeriod;
    }

    public void setExecutionPeriod(Date executionPeriod) {
        this.executionPeriod = executionPeriod;
    }

    public List<CEMSResponsiblePerson> getFiResponsiblePersons() {
        return fiResponsiblePersons == null ? new ArrayList<>() : fiResponsiblePersons;
    }

    public void setFiResponsiblePersons(List<CEMSResponsiblePerson> fiResponsiblePersons) {
        this.fiResponsiblePersons = fiResponsiblePersons;
    }

    public CEMSRecommendationStatusInfo getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(CEMSRecommendationStatusInfo statusInfo) {
        this.statusInfo = statusInfo;
    }

    public Date getRecordCreateDate() {
        return recordCreateDate;
    }

    public void setRecordCreateDate(Date recordCreateDate) {
        this.recordCreateDate = recordCreateDate;
    }

    public CEMSInspection getInspection() {
        return inspection;
    }

    public void setInspection(CEMSInspection inspection) {
        this.inspection = inspection;
    }

}
