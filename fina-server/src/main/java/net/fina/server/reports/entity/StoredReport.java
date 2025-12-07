package net.fina.server.reports.entity;

import net.fina.auditlog.api.Audited;
import net.fina.common.client.reports.ReportType;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "OUT_STORED_REPORTS")
@Table(name = "OUT_STORED_REPORTS")
@SuppressWarnings("serial")
public class StoredReport implements Serializable, Audited {

    @EmbeddedId
    private StoredReportPk reportPk;

    @Column(name = "INFO")
    private byte[] info;

    @Column(name = "REPORTRESULT")
    private byte[] reportResult;

    @Column(name = "USERID")
    private long userId;

    @Column(name = "STOREDATE")
    private Date storeDate;

    @Column(name = "REPORTHTMLRESULT")
    private byte[] reportHtmlResult;

    @Column(name = "REPORT_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private ReportType reportType;

    @Column(name = "REPOSITORY_FILE_ID")
    private String repositoryFileId;

    @Column(name = "REPOSITORY_FILE_VERSION_ID")
    private String repositoryFileVersionId;

    public StoredReport() {
    }

    public StoredReport(long userId, Date storeDate) {
        this.userId = userId;
        this.storeDate = storeDate;
    }

    public byte[] getInfo() {
        return info;
    }

    public void setInfo(byte[] info) {
        this.info = info;
    }

    public byte[] getReportResult() {
        return reportResult;
    }

    public void setReportResult(byte[] reportResult) {
        this.reportResult = reportResult;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getStoreDate() {
        return storeDate;
    }

    public void setStoreDate(Date storeDate) {
        this.storeDate = storeDate;
    }

    public StoredReportPk getReportPk() {
        return reportPk;
    }

    public void setReportPk(StoredReportPk reportPk) {
        this.reportPk = reportPk;
    }

    public byte[] getReportHtmlResult() {
        return reportHtmlResult;
    }

    public void setReportHtmlResult(byte[] reportHtmlResult) {
        this.reportHtmlResult = reportHtmlResult;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getRepositoryFileId() {
        return repositoryFileId;
    }

    public void setRepositoryFileId(String repositoryFileId) {
        this.repositoryFileId = repositoryFileId;
    }

    public String getRepositoryFileVersionId() {
        return repositoryFileVersionId;
    }

    public void setRepositoryFileVersionId(String repositoryFileVersionId) {
        this.repositoryFileVersionId = repositoryFileVersionId;
    }

    @Override
    public String toString() {
        return "StoredReport{" + "reportPk=" + reportPk + ", userId=" + userId + ", storeDate=" + storeDate + '}';
    }
}
