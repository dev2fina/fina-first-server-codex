package net.fina.server.dcs.uploadfile.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.common.client.returns.ProcessStatus;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportStatusDetailModel implements Serializable {

    protected String code;
    protected String type;
    protected String version;
    protected Date importStart;
    protected String importStartString;
    protected Date importEnd;
    protected String importEndString;
    protected Date periodStart;
    protected Date periodEnd;
    protected String status;
    protected String message;
    protected int id;
    private String xmlProcessStatusCode;
    private String note;
    private ProcessStatus processStatus;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getImportStart() {
        return importStart;
    }

    public void setImportStart(Date importStart) {
        this.importStart = importStart;
    }

    public String getImportStartString(){
        return importStartString;
    }

    public void setImportStartString(String importStartString){
        this.importStartString = importStartString;
    }

    public Date getImportEnd() {
        return importEnd;
    }

    public void setImportEnd(Date importEnd) {
        this.importEnd = importEnd;
    }

    public String getImportEndString(){
        return importEndString;
    }

    public void setImportEndString(String importEndString){
        this.importEndString = importEndString;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXmlProcessStatusCode() {
        return xmlProcessStatusCode;
    }

    public void setXmlProcessStatusCode(String xmlProcessStatusCode) {
        this.xmlProcessStatusCode = xmlProcessStatusCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }
}
