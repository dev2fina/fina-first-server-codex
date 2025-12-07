package net.fina.server.dcs.mail.model;

import java.io.Serializable;
import java.util.List;

public class MailResponseUploadFileMetaModel implements Serializable{
    private String fileName;
    private String status;
    private String reason;

    private List<MailResponseImportedXmlMetaModel> xmls;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MailResponseImportedXmlMetaModel> getXmls() {
        return xmls;
    }

    public void setXmls(List<MailResponseImportedXmlMetaModel> xmls) {
        this.xmls = xmls;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
