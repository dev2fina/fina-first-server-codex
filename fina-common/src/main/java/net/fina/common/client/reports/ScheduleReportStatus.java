package net.fina.common.client.reports;

public enum ScheduleReportStatus {
    STATUS_SCHEDULED("Scheduled"),
    STATUS_PROCESSING("Processing"),
    STATUS_DONE("Done"),
    STATUS_ERROR("Error");

    private ScheduleReportStatus(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
}
