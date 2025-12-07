package net.fina.server.reports.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@SuppressWarnings("serial")
public class StoredReportPk implements Serializable {
    @Column(name = "REPORTID")
    private int reportId;

    @Column(name = "LANGID")
    private int langId;

    @Column(name = "HASHCODE")
    private int hashCode;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getLangId() {
        return langId;
    }

    public void setLangId(int langId) {
        this.langId = langId;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId, langId, hashCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StoredReportPk other = (StoredReportPk) obj;
        if (hashCode != other.hashCode)
            return false;
        if (langId != other.langId)
            return false;
        if (reportId != other.reportId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "StoredReportPk [reportId=" + reportId + ", langId=" + langId + ", hashCode=" + hashCode + "]";
    }

}
