package net.fina.server.reports.entity;

import java.io.Serializable;

public class ScheduleReportId implements Serializable {

    private int reportId;
    private int langId;
    private int hashcode;

    public ScheduleReportId() {}

    public ScheduleReportId(int reportId, int langId, int hashcode) {
        this.reportId = reportId;
        this.langId = langId;
        this.hashcode = hashcode;
    }

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

    public int getHashcode() {
        return hashcode;
    }

    public void setHashcode(int hashCode) {
        this.hashcode = hashCode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleReportId that = (ScheduleReportId) o;
        return (that.getReportId() == this.reportId) &&
                (that.getLangId() == this.langId) &&
                (that.getHashcode() == this.hashcode);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = reportId ^ (reportId >>> 16);
        result = prime * result + (langId ^ (langId >>> 16));
        result = prime * result + (hashcode ^ (hashcode >>> 16));
        return result;
    }

    @Override
    public String toString() {
        return "ScheduleReportId{" +
                "reportId=" + this.reportId +
                "langId=" + this.langId +
                "hashCode=" + this.hashcode +
                "}";
    }
}
