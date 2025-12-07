package net.fina.server.inputs.model;

import net.fina.common.client.dcs.UploadType;

import java.util.Date;
import java.util.List;

public class InputManagerUploadFileMetaModel {

    protected long id;
    protected String fileName;
    protected UploadType typeCode;
    protected String typeName;
    protected Date uploadedTime;
    protected String statusCode;
    protected String statusName;
    protected String reason;
    protected String protectioninfo;
    protected String bankCode;
    protected List<String> bankCodes;
    protected String bankName;
    protected long userId;
    protected String userLogin;
    protected String userName;
    protected Boolean hasUserBank;
    protected Boolean nameValid;
    protected Boolean versionValid;
    protected Boolean matrixValid;
    private Date fromDate;
    private Date toDate;
    protected boolean convert;
    protected String languageCode;

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

    public UploadType getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(UploadType typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Date getUploadedTime() {
        return uploadedTime;
    }

    public void setUploadedTime(Date uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
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

    public List<String> getBankCodes() {
        return bankCodes;
    }

    public void setBankCodes(List<String> bankCodes) {
        this.bankCodes = bankCodes;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
