package net.fina.common.shared.schedule;

public enum ScheduledTaskStatus {
    STATUS_SCHEDULED("Scheduled"),
    STATUS_PROCESSING("Processing"),
    STATUS_DONE("Done"),
    STATUS_ERROR("Error");

    private ScheduledTaskStatus(String code) {
        this.code = code;
    }

    private final String code;

    public String getCode() {
        return code;
    }
}
