package net.fina.server.cems.model;

import net.fina.server.cems.entity.CEMSRecommendationType;

import java.util.Date;
import java.util.List;

public class CEMSRecommendationModel {
    private String id;
    private CEMSRecommendationType type;
    private Date creationDate;
    private Date letterDate;
    private String letterInfo;
    private int number;
    private String orderContent;
    private Date executionPeriod;
    private List<CEMSResponsiblePersonModel> fiResponsiblePersons;
    private String fiActions;
    private String note;
    private String status;

    private CEMSInspectionModel inspection;
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
        return letterInfo;
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

    public List<CEMSResponsiblePersonModel> getFiResponsiblePersons() {
        return fiResponsiblePersons;
    }

    public void setFiResponsiblePersons(List<CEMSResponsiblePersonModel> fiResponsiblePersons) {
        this.fiResponsiblePersons = fiResponsiblePersons;
    }

    public String getFiActions() {
        return fiActions;
    }

    public void setFiActions(String fiActions) {
        this.fiActions = fiActions;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CEMSInspectionModel getInspection() {
        return inspection;
    }

    public void setInspection(CEMSInspectionModel inspection) {
        this.inspection = inspection;
    }
}
