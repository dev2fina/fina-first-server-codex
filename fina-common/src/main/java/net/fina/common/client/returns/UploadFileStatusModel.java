package net.fina.common.client.returns;

import net.fina.common.client.dcs.UploadFileStatus;

public class UploadFileStatusModel {
    private UploadFileStatus status;
    private String name;

    public UploadFileStatusModel() {}

    public UploadFileStatusModel(UploadFileStatus status, String name) {
        this.status = status;
        this.name = name;
    }

    public UploadFileStatus getStatus() {
        return status;
    }

    public void setStatus(UploadFileStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
