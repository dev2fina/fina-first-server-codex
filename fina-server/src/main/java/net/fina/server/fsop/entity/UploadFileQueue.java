package net.fina.server.fsop.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.dcs.uploadfile.model.ProcessEngine;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity(name = "IN_UPLOADFILE_QUEUE")
@Table(name = "IN_UPLOADFILE_QUEUE")
public class UploadFileQueue implements Audited, Serializable {

    @Id
    @Column(name = "FILE_ID")
    private long fileId;

    @Column(name = "USER_ID")
    private long userId;

    @Column(name = "FILE_NAME")
    private String fileName;

    private UploadFileQueueStatus status;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "PROCESS_ENGINE")
    private ProcessEngine processEngine = ProcessEngine.FINA;

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public UploadFileQueueStatus getStatus() {
        return status;
    }

    public void setStatus(UploadFileQueueStatus status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Override
    public String toString() {
        return "[fileId=" + fileId +
                ", userId=" + userId +
                ", fileName='" + fileName + '\'' +
                ", status=" + status + ']';
    }
}
