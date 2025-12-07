package net.fina.server.dcs.uploadfile.impl.event;

import net.fina.common.client.dcs.UploadFileStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadFileStatusEvent {
    private long fileId;
    private String fileName;

    private UploadFileStatus status;

    private List<String> userLogins;

    private String fiCode;

    private Map<String, String> fiNameMap;

    public UploadFileStatusEvent() {
    }

    public UploadFileStatusEvent(long fileId, UploadFileStatus status, List<String> userLogins, String fiCode, Map<String,String> fiNameMap,String fileName) {
        this.fileId = fileId;
        this.userLogins = userLogins;
        this.status = status;
        this.fiCode = fiCode;
        this.fiNameMap = fiNameMap;
        this.fileName = fileName;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public List<String> getUserLogins() {
        return userLogins;
    }

    public void setUserLogins(List<String> userLogins) {
        this.userLogins = userLogins;
    }

    public UploadFileStatus getStatus() {
        return status;
    }

    public void setStatus(UploadFileStatus status) {
        this.status = status;
    }


    public String getFiCode() {
        return fiCode;
    }

    public void setFiCode(String fiCode) {
        this.fiCode = fiCode;
    }

    public Map<String, String> getFiNameMap() {
        return fiNameMap;
    }

    public void setFiNameMap(Map<String, String> fiNameMap) {
        this.fiNameMap = fiNameMap;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
