package net.fina.server.cems.entity;

public enum CEMSInspectionType {
    COMPLEX_CHECK("net.fina.cems.inspection.complexType"),
    TARGETED_VERIFICATION("net.fina.cems.inspection.targetedVerification"),
    TOPIC_CHECK("net.fina.cems.inspection.topicCheck");

    private final String code;

    CEMSInspectionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
