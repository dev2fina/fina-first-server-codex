package net.fina.common.client.returns;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.common.client.constants.ImportStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static net.fina.common.client.util.SafeValueUtil.getTrimmedValueSafe;
import static net.fina.common.client.util.SafeValueUtil.getValueSafe;

/**
 * User: Alexander
 * Date: 8/2/13
 * Time: 4:29 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportModel implements Serializable {
    private int id;
    private String returnCode;
    private String bankCode;
    private String versionCode;
    private Date periodStart;
    private Date periodEnd;
    private String userCode;
    private long userId;
    private long langId;
    private Date uploadTime;
    private Date importStart;
    private Date importEnd;
    private ImportStatus status;
    private String message;
    private String returnType;
    private ImportedFileType importType;
    private String fileName;
    private long fileId;
    private boolean group;
    private boolean isLatest;

    transient private int hash;


    public ImportModel() {
    }

    //FOR GROUPS
    public ImportModel(String bankCode, String versionCode, Date periodStart, Date periodEnd, String fileName, Long fileId, String login) {
        this();
        this.bankCode = bankCode;
        this.versionCode = versionCode;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.fileName = fileName;
        this.userCode = login;
        this.fileId = fileId == null ? 0 : fileId;
        this.id = fileId != null ? fileId.intValue() : 0;
        this.group = true;
    }

    public ImportModel(String bankCode, String versionCode, Date periodStart, Date periodEnd, String fileName, Long fileId, Date uploadTime, String login) {
        this(bankCode, versionCode, periodStart, periodEnd, fileName, fileId, login);
        this.uploadTime = uploadTime;
    }

    //FOR FILES
    public ImportModel(Integer id, String returnCode, String bankCode, String versionCode, Date periodStart, Date periodEnd, String userCode, long userId, Date uploadTime, Date importStart, Date importEnd, ImportStatus status, ImportedFileType importType, String message, String fileName, Long fileId) {
        this(bankCode, versionCode, periodStart, periodEnd, fileName, fileId, userCode);
        this.id = id == null ? 0 : id;
        this.uploadTime = uploadTime;
        this.returnCode = returnCode;
        this.userId = userId;
        this.importStart = importStart;
        this.importEnd = importEnd;
        this.status = status;
        this.message = message;
        this.importType = importType;
        this.group = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getReturnCodeSafe() {
        return getValueSafe(returnCode);
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = getTrimmedValueSafe(bankCode);
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getBankCodeSafe() {
        return getTrimmedValueSafe(bankCode);
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getVersionCodeSafe() {
        return getTrimmedValueSafe(versionCode);
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Date getImportStart() {
        return importStart;
    }

    public void setImportStart(Date importStart) {
        this.importStart = importStart;
    }

    public Date getImportEnd() {
        return importEnd;
    }

    public void setImportEnd(Date importEnd) {
        this.importEnd = importEnd;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getMessageSafe() {
        return getTrimmedValueSafe(message);
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getReturnTypeSafe() {
        return getTrimmedValueSafe(returnType);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getFileNameSafe() {
        return getTrimmedValueSafe(fileName);
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getUserCodeSafe() {
        return getTrimmedValueSafe(userCode);
    }

    public ImportedFileType getImportType() {
        return importType;
    }

    public void setImportType(ImportedFileType importType) {
        this.importType = importType;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean isLatest() {
        return isLatest;
    }

    public void setLatest(boolean latest) {
        isLatest = latest;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportModel model = (ImportModel) o;

        if (fileId != model.fileId) return false;
        if (id != model.id) return false;
        if (!Objects.equals(bankCode, model.bankCode)) return false;
        if (!Objects.equals(fileName, model.fileName)) return false;
        if (!Objects.equals(periodEnd, model.periodEnd)) return false;
        if (!Objects.equals(periodStart, model.periodStart)) return false;
        return Objects.equals(versionCode, model.versionCode);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (fileId ^ (fileId >>> 32));
        result = 31 * result + (bankCode != null ? bankCode.hashCode() : 0);
        result = 31 * result + (versionCode != null ? versionCode.hashCode() : 0);
        result = 31 * result + (periodStart != null ? periodStart.hashCode() : 0);
        result = 31 * result + (periodEnd != null ? periodEnd.hashCode() : 0);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImportModel{" +
                "fileName='" + fileName + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", returnCode='" + returnCode + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", periodEnd=" + periodEnd +
                ", userId=" + userId +
                ", uploadTime=" + uploadTime +
                ", isLatest=" + isLatest +
                '}';
    }

}
