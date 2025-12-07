package net.fina.server.dcs.uploadfile.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.common.client.dcs.UploadType;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.security.entity.User;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadFileMetaModel implements Serializable {

    protected long id;
    protected String fileName;
    protected UploadType type;
    protected Date uploadedTime;
    protected String uploadedTimeString;
    protected String status;
    protected String reason;
    protected String protectioninfo;
    protected String bankCode;
    protected List<String> bankCodes;
    protected String bankName;
    protected long userId;
    protected String userLogin;
    protected Boolean hasUserBank;
    protected Boolean nameValid;
    protected Boolean versionValid;
    protected Boolean matrixValid;
    protected boolean convert;
    protected String languageCode;
    protected List<Long> scheduleIds;
    private Date fromDate;
    private Date toDate;
    private ProcessEngine processEngine = ProcessEngine.FINA;
    private Long matrixId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UploadType getType() {
        return type;
    }

    public void setType(UploadType type) {
        this.type = type;
    }

    public Date getUploadedTime() {
        return uploadedTime;
    }

    public void setUploadedTime(Date uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    public String getUploadedTimeString() {
        return uploadedTimeString;
    }

    public void setUploadedTimeString(String uploadedTimeString) {
        this.uploadedTimeString = uploadedTimeString;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getProtectioninfo() {
        return protectioninfo;
    }

    public void setProtectioninfo(String protectioninfo) {
        this.protectioninfo = protectioninfo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public Boolean getHasUserBank() {
        return hasUserBank;
    }

    public void setHasUserBank(Boolean hasUserBank) {
        this.hasUserBank = hasUserBank;
    }

    public Boolean getNameValid() {
        return nameValid;
    }

    public void setNameValid(Boolean nameValid) {
        this.nameValid = nameValid;
    }

    public Boolean getVersionValid() {
        return versionValid;
    }

    public void setVersionValid(Boolean versionValid) {
        this.versionValid = versionValid;
    }

    public Boolean getMatrixValid() {
        return matrixValid;
    }

    public void setMatrixValid(Boolean matrixValid) {
        this.matrixValid = matrixValid;
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

    public boolean isConvert() {
        return convert;
    }

    public void setConvert(boolean convert) {
        this.convert = convert;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public List<String> getBankCodes() {
        return bankCodes;
    }

    public void setBankCodes(List<String> bankCodes) {
        this.bankCodes = bankCodes;
    }

    public List<Long> getScheduleIds() {
        return scheduleIds == null ? new ArrayList<>() : scheduleIds;
    }

    public void setScheduleIds(List<Long> scheduleIds) {
        this.scheduleIds = scheduleIds;
    }

    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public Long getMatrixId() {
        return matrixId;
    }

    @JsonIgnore
    public UploadFileMetaModel setUploadFile(UploadFile uf, DateFormat df) {
        this.id = uf.getId();
        this.fileName = uf.getFileName();
        this.status = uf.getStatus();
        this.bankCode = uf.getBankCode();

        if (uf.getUser() != null) {
            this.userId = uf.getUser().getId();
            this.userLogin = uf.getUser().getLogin();
        }

        this.hasUserBank = uf.getHasUserBank();
        this.nameValid = uf.isNameValid();
        this.versionValid = uf.getVersionValid();
        this.matrixValid = uf.getMatrixValid();

        this.uploadedTime = uf.getUploadedTime();
        if (this.uploadedTime != null && df != null) {
            this.uploadedTimeString = df.format(this.uploadedTime);
        }

        this.type = uf.getType();
        this.reason = uf.getReason();
        this.protectioninfo = uf.getProtectioninfo();

        this.setFromDate(uf.getFromDate());
        this.setToDate(uf.getToDate());
        this.processEngine = uf.getProcessEngine();
        if (uf.getMatrixId() != null) {
            this.matrixId = uf.getMatrixId();
        }
        return this;
    }

    @JsonIgnore
    public UploadFile getUploadFile() {
        UploadFile uf = new UploadFile();
        uf.setId(id);
        uf.setFileName(fileName);
        uf.setStatus(status);
        uf.setBankCode(bankCode);
        uf.setBankCodes(bankCodes);

        User user = new User();
        user.setId(userId);
        user.setLogin(userLogin);

        uf.setUser(user);

        uf.setHasUserBank(hasUserBank);
        uf.setNameValid(nameValid);
        uf.setVersionValid(versionValid);
        uf.setMatrixValid(matrixValid);
        uf.setUploadedTime(uploadedTime);
        uf.setType(type);

        uf.setReason(reason);
        uf.setProtectioninfo(protectioninfo);

        uf.setFromDate(fromDate);
        uf.setToDate(toDate);
        uf.setMatrixId(this.matrixId);
        return uf;
    }

    @JsonIgnore
    public UploadFileMetaModel clone() {
        UploadFileMetaModel result = new UploadFileMetaModel();
        result.id = this.getId();
        result.fileName = this.getFileName();
        result.status = this.getStatus();
        result.bankCode = this.getBankCode();

        result.userId = this.getUserId();
        result.userLogin = this.getUserLogin();

        result.hasUserBank = this.getHasUserBank();
        result.nameValid = this.getNameValid();
        result.versionValid = this.getVersionValid();
        result.matrixValid = this.getMatrixValid();

        result.uploadedTime = this.getUploadedTime();
        result.uploadedTimeString = this.getUploadedTimeString();

        result.type = this.getType();
        result.reason = this.getReason();
        result.protectioninfo = this.getProtectioninfo();

        result.setFromDate(this.getFromDate());
        result.setToDate(this.getToDate());

        return result;
    }

    @Override
    public String toString() {
        return "UploadFileMetaModel{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", convert=" + convert +
                ", languageCode='" + languageCode + '\'' +
                '}';
    }
}
