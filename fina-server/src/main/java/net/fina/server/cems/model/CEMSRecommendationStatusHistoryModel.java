package net.fina.server.cems.model;

import java.util.Date;

public class CEMSRecommendationStatusHistoryModel {
    private long id;
    private String note;
    private String fiActions;
    private String status;
    private Date recordDate;
    private int version;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFiActions() {
        return fiActions;
    }

    public void setFiActions(String fiActions) {
        this.fiActions = fiActions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
