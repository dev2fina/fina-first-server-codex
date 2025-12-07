package net.fina.server.reports.entity;

import java.io.Serializable;
import java.util.Objects;

public class ReportPropertyId implements Serializable {
    private int reportId;
    private String key;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportPropertyId that = (ReportPropertyId) o;
        return reportId == that.reportId &&
                Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId, key);
    }
}
