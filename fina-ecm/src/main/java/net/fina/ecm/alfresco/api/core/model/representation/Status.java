package net.fina.ecm.alfresco.api.core.model.representation;

public enum Status {
    PENDING("PENDING"),
    DONE("DONE"),
    IN_PROGRESS("IN_PROGRESS"),
    MAX_CONTENT_SIZE_EXCEEDED("MAX_CONTENT_SIZE_EXCEEDED "),
    CANCELLED("CANCELLED");

    private String value;

    Status(String value) {
        this.value = value;
    }
}
