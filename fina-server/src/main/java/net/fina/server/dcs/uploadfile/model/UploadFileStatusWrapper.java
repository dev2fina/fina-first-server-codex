package net.fina.server.dcs.uploadfile.model;

import java.util.Map;

public class UploadFileStatusWrapper {
    private long id;
    private String fileName;
    private int status;
    private String fiCode;
    private Map<String, String> fiNameMap;


    public UploadFileStatusWrapper(long id, int status, String fiCode, Map<String, String> fiNameMap, String fileName) {
        this.id = id;
        this.status = status;
        this.fiCode = fiCode;
        this.fiNameMap = fiNameMap;
        this.fileName = fileName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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