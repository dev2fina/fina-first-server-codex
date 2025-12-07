package net.fina.common.client.returns;

import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.shared.SortField;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ReturnSubmissionPackageModel implements Serializable {
    protected long id;

    protected long userId;

    protected String login;

    protected String fileName;

    protected String bankCode;

    protected String bankName;

    protected String reason;

    protected UploadFileStatus status;

    protected Date uploadTime;

    private String versionCode;
    private long versionId;

    private Date fromDate;
    private Date toDate;

    private List<String> fiCodes;
    private SortField sortField;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public UploadFileStatus getStatus() {
        return status;
    }

    public void setStatus(UploadFileStatus status) {
        this.status = status;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public List<String> getFiCodes() {
        return fiCodes;
    }

    public void setFiCodes(List<String> fiCodes) {
        this.fiCodes = fiCodes;
    }

    public SortField getSortField() {
        return sortField;
    }

    public void setSortField(SortField sortField) {
        this.sortField = sortField;
    }
}
