package net.fina.server.dcs.mail.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MailResponseMetaModel implements Serializable {

    private String header;
    private String footer;

    private String errorMail;

    private String sent;
    private Date sentDate;

    private String received;
    private Date receivedDate;

    private List<MailResponseUploadFileMetaModel> files;
    private List<MailResponseUploadFileMetaModel> errorFiles;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public List<MailResponseUploadFileMetaModel> getFiles() {
        return files;
    }

    public void setFiles(List<MailResponseUploadFileMetaModel> files) {
        this.files = files;
    }

    public List<MailResponseUploadFileMetaModel> getErrorFiles() {
        return errorFiles;
    }

    public void setErrorFiles(List<MailResponseUploadFileMetaModel> errorFiles) {
        this.errorFiles = errorFiles;
    }

    public String getErrorMail() {
        return errorMail;
    }

    public void setErrorMail(String errorMail) {
        this.errorMail = errorMail;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }
}
