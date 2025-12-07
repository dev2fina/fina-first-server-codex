package net.fina.server.fsop.model;

import net.fina.common.client.constants.ImportStatus;
import net.fina.common.server.util.CommonUtil;
import net.fina.server.returns.entity.ImportedReturn;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class FsopImportedReturnMetaModel implements Serializable {

    private int id;

    private ImportStatus status;
    private String message;
    private String fullMessage;

    private Date importStart;
    private Date importEnd;

    private byte[] content;

    private String returnCode;

    private Date periodStart;

    private Date periodEnd;

    private long langId;

    private String languageNumberFormat;

    private String languageDateFormat;

    private String languageDateTimeFormat;
    private String versionCode;

    public FsopImportedReturnMetaModel() {
    }

    public FsopImportedReturnMetaModel(int id, byte[] content, String returnCode, Date periodStart, Date periodEnd,
                                       long langId, String languageNumberFormat,
                                       String languageDateFormat, String languageDateTimeFormat,
                                       String versionCode) {
        this.id = id;
        this.content = content;
        this.returnCode = returnCode;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.langId = langId;
        this.languageNumberFormat = languageNumberFormat;
        this.languageDateFormat = languageDateFormat;
        this.languageDateTimeFormat = languageDateTimeFormat;
        this.versionCode = versionCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        this.message = CommonUtil.left(message, 2000);
        this.fullMessage = message;
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
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

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    public String getLanguageNumberFormat() {
        return languageNumberFormat;
    }

    public void setLanguageNumberFormat(String languageNumberFormat) {
        this.languageNumberFormat = languageNumberFormat;
    }

    public String getLanguageDateFormat() {
        return languageDateFormat;
    }

    public void setLanguageDateFormat(String languageDateFormat) {
        this.languageDateFormat = languageDateFormat;
    }

    public String getLanguageDateTimeFormat() {
        return languageDateTimeFormat;
    }

    public void setLanguageDateTimeFormat(String languageDateTimeFormat) {
        this.languageDateTimeFormat = languageDateTimeFormat;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    public void setFullMessage(String fullMessage) {
        this.fullMessage = fullMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FsopImportedReturnMetaModel that = (FsopImportedReturnMetaModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public FsopImportedReturnMetaModel setImportedReturn(ImportedReturn ir) {
        id = ir.getId();
        returnCode = ir.getReturnCode();
        importStart = ir.getImportStart();
        importEnd = ir.getImportEnd();
        periodStart = ir.getPeriodStart();
        periodEnd = ir.getPeriodEnd();
        message = ir.getMessage();
        status = ir.getStatus();
        languageNumberFormat = ir.getLanguage().getNumberFormat();
        languageDateFormat = ir.getLanguage().getDateFormat();
        languageDateTimeFormat = ir.getLanguage().getDateTimeFormat();
        langId = ir.getLanguage().getId();
        versionCode = ir.getVersionCode();
        fullMessage = ir.getFullMessage();
        return this;
    }
}
