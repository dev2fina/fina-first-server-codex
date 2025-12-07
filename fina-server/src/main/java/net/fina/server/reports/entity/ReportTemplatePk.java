package net.fina.server.reports.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReportTemplatePk implements Serializable {

    @Column
    private int reportId;

    @Column
    private long langId;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportTemplatePk that = (ReportTemplatePk) o;
        return Objects.equals(reportId, that.reportId) &&
                Objects.equals(langId, that.langId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId, langId);
    }
}
