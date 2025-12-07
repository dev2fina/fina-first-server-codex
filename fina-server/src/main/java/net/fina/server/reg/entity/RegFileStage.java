package net.fina.server.reg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "IN_REG_FILE_STAGE")
@Table(name = "IN_REG_FILE_STAGE")
public class RegFileStage {
    @Id
    @Column(name = "FILE_ID")
    private long fileId;

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }
}
