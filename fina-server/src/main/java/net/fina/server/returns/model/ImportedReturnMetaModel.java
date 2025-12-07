package net.fina.server.returns.model;

import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.returns.ImportedFileType;
import net.fina.server.returns.entity.ImportedReturn;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

public class ImportedReturnMetaModel implements Serializable {
    private int id;
    private String returnCode;
    private String versionCode;
    private Date importStart;
    private String importStartString;
    private Date importEnd;
    private String importEndString;
    private Date periodStart;
    private Date periodEnd;
    private String message;
    private ImportStatus status;
    private ImportedFileType type;

    public ImportedReturnMetaModel() {
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

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Date getImportStart() {
        return importStart;
    }

    public void setImportStart(Date importStart) {
        this.importStart = importStart;
    }

    public String getImportStartString() {
        return importStartString;
    }

    public void setImportStartString(String importStartString) {
        this.importStartString = importStartString;
    }

    public Date getImportEnd() {
        return importEnd;
    }

    public void setImportEnd(Date importEnd) {
        this.importEnd = importEnd;
    }

    public String getImportEndString() {
        return importEndString;
    }

    public void setImportEndString(String importEndString) {
        this.importEndString = importEndString;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public ImportedFileType getType() {
        return type;
    }

    public void setType(ImportedFileType type) {
        this.type = type;
    }

    public ImportedReturnMetaModel setImportedReturn(ImportedReturn ir, DateFormat df) {
        id = ir.getId();
        returnCode = ir.getReturnCode();
        versionCode = ir.getVersionCode();

        importStart = ir.getImportStart();
        if (importStart != null && df != null) {
            importStartString = df.format(importStart);
        }

        importEnd = ir.getImportEnd();
        if (importEnd != null && df != null) {
            importEndString = df.format(importEnd);
        }

        periodStart = ir.getPeriodStart();
        periodEnd = ir.getPeriodEnd();
        message = ir.getMessage();
        status = ir.getStatus();
        type = ir.getType();

        return this;
    }

    public ImportedReturn getImportedReturn() {
        ImportedReturn ir = new ImportedReturn();
        ir.setId(ir.getId());
        ir.setReturnCode(returnCode);
        ir.setVersionCode(versionCode);
        ir.setImportStart(importStart);
        ir.setImportEnd(importEnd);
        ir.setPeriodStart(periodStart);
        ir.setPeriodEnd(periodEnd);
        ir.setMessage(message);
        ir.setStatus(status);
        ir.setType(type);

        return ir;
    }
}
