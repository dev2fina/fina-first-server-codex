package net.fina.server.reports.model;

import net.fina.common.client.reports.ScheduleReportStatus;
import java.io.Serializable;

public class ReportScheduleWrapper implements Serializable {
    private int reportId;

    private ScheduleReportStatus status;

    public ReportScheduleWrapper(int reportId, ScheduleReportStatus status) {
        this.reportId = reportId;
        this.status = status;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public ScheduleReportStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleReportStatus status) {
        this.status = status;
    }
}

