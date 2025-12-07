package net.fina.server.rvc.event;

public abstract class ReturnVersionControlEvent {
    private final String processId;
    private final String type;

    public ReturnVersionControlEvent(String processId, String type) {
        this.processId = processId;
        this.type = type;
    }

    public String getProcessId() {
        return processId;
    }

    public String getType() {
        return type;
    }
}
