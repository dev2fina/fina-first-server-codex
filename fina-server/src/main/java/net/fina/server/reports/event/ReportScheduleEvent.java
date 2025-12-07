package net.fina.server.reports.event;

import net.fina.common.client.reports.ScheduleReportStatus;

public class ReportScheduleEvent {
    private int reportId;
    private String userLogin;
    private ScheduleReportStatus status;

    public ReportScheduleEvent(int reportId, String userLogin, ScheduleReportStatus status) {
        this.reportId = reportId;
        this.userLogin = userLogin;
        this.status = status;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public ScheduleReportStatus getStatus() {
        return status;
    }
    public void setStatus(ScheduleReportStatus status) {
        this.status = status;
    }
}
