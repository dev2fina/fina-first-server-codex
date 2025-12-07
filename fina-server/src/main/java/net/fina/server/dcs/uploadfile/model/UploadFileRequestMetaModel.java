package net.fina.server.dcs.uploadfile.model;

import net.fina.common.client.dcs.UploadType;

import java.util.Date;

public class UploadFileRequestMetaModel {
    private long id;
    private String bankCode;
    private String bankName;
    private String fileName;
    private String contentType;
    private byte[] content;
    private UploadType uploadType;
    private Date uploadTime;
    private String repositoryFileId;
    private String repositoryFileVersionId;

    public UploadFileRequestMetaModel() {
    }

    public UploadFileRequestMetaModel(String fileName, String contentType, byte[] content) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public UploadType getUploadType() {
        return uploadType;
    }

    public void setUploadType(UploadType uploadType) {
        this.uploadType = uploadType;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getRepositoryFileId() {
        return repositoryFileId;
    }

    public void setRepositoryFileId(String repositoryFileId) {
        this.repositoryFileId = repositoryFileId;
    }

    public String getRepositoryFileVersionId() {
        return repositoryFileVersionId;
    }

    public void setRepositoryFileVersionId(String repositoryFileVersionId) {
        this.repositoryFileVersionId = repositoryFileVersionId;
    }
}
