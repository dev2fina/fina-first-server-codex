package net.fina.common.shared.report;

import net.fina.common.client.reports.ReportType;

public class StoredReportModelSimple {
    private long reportId;
    private long parentId;
    private String code;
    private String description;
    private int hashcode;
    private ReportType reportType;

    public StoredReportModelSimple() {
    }

    public StoredReportModelSimple(long reportId, long parentId, String code, String description, int hashcode, ReportType reportType) {
        this.reportId = reportId;
        this.parentId = parentId;
        this.code = code;
        this.description = description;
        this.hashcode = hashcode;
        this.reportType = reportType;
    }

    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHashcode() {
        return hashcode;
    }

    public void setHashcode(int hashcode) {
        this.hashcode = hashcode;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
}
