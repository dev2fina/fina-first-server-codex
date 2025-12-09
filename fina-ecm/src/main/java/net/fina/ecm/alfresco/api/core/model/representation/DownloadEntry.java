package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadEntry implements Serializable, BaseRepresentation {
    private String id;
    private int filesAdded;
    private int bytesAdded;
    private int totalFiles;
    private long totalBytes;
    private Status status;

    public DownloadEntry() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFilesAdded() {
        return filesAdded;
    }

    public void setFilesAdded(int filesAdded) {
        this.filesAdded = filesAdded;
    }

    public int getBytesAdded() {
        return bytesAdded;
    }

    public void setBytesAdded(int bytesAdded) {
        this.bytesAdded = bytesAdded;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}



