package net.fina.common.client.returns;

public class ProcessStatusModel {
    private ProcessStatus status;
    private String name;

    public ProcessStatusModel() {}

    public ProcessStatusModel(ProcessStatus status, String name) {
        this.status = status;
        this.name = name;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
