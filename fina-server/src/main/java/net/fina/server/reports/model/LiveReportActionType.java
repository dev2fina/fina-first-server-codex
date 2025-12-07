package net.fina.server.reports.model;

public enum LiveReportActionType {
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    AVG("AVERAGE");

    String code;

    LiveReportActionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}