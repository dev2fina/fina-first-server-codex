package net.fina.server.reports.entity;

import net.fina.auditlog.api.Audited;
import net.fina.common.client.reports.ScheduleReportStatus;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "OUT_REPORTS_SCHEDULE")
@Table(name = "OUT_REPORTS_SCHEDULE")
@IdClass(ScheduleReportId.class)
@SuppressWarnings("serial")
public class ScheduleReport implements Serializable, Audited {

    @Id
    @Column(name = "REPORTID")
    private int reportId;

    @Id
    @Column(name = "LANGID")
    private int langId;

    @Id
    @Column(name = "HASHCODE")
    private int hashcode;

    @Column(name = "SCHEDULETIME")
    private Date scheduleTime;

    @Column(name = "INFO")
    private byte[] info;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS")
    private ScheduleReportStatus status;

    @Column(name = "ONDEMAND")
    private Integer onDemand;

    @Column(name = "USERID")
    private int userId;

    @Column(name = "STATE")
    private String state;

    @Column(name = "FILESTORAGELOCATION")
    private String fileStorageLocation;

    @Column(name = "REPOSITORYNODEID")
    private String repositoryNodeId;

    @Column(name = "REPOSITORYFOLDERNAME")
    private String repositoryFolderName;

    @Column(name = "NOTIFICATIONMAILS")
    private String notificationMails;

    public int getLangId() {
        return langId;
    }

    public void setLangId(int langId) {
        this.langId = langId;
    }

    public byte[] getInfo() {
        return info;
    }

    public void setInfo(byte[] info) {
        this.info = info;
    }

    public ScheduleReportStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleReportStatus status) {
        this.status = status;
    }

    public Integer getOnDemand() {
        return onDemand;
    }

    public void setOnDemand(Integer onDemand) {
        this.onDemand = onDemand;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getHashcode() {
        return hashcode;
    }

    public void setHashcode(int hashcode) {
        this.hashcode = hashcode;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFileStorageLocation() {
        return fileStorageLocation;
    }

    public void setFileStorageLocation(String fileStorageLocation) {
        this.fileStorageLocation = fileStorageLocation;
    }

    public String getRepositoryNodeId() {
        return repositoryNodeId;
    }

    public void setRepositoryNodeId(String repositoryNodeId) {
        this.repositoryNodeId = repositoryNodeId;
    }

    public String getNotificationMails() {
        return notificationMails;
    }

    public void setNotificationMails(String notificationMails) {
        this.notificationMails = notificationMails;
    }

    public String getRepositoryFolderName() {
        return repositoryFolderName;
    }

    public void setRepositoryFolderName(String repositoryFolderName) {
        this.repositoryFolderName = repositoryFolderName;
    }

    @Override
    public String toString() {
        return "ScheduleReport{" +
                "reportId=" + reportId +
                ", langId=" + langId +
                ", hashcode=" + hashcode +
                ", scheduleTime=" + scheduleTime +
                ", info:" + (info == null ? "NULL" : "NOT NULL") +
                ", status=" + status +
                ", onDemand=" + onDemand +
                ", userId=" + userId +
                '}';
    }
}
