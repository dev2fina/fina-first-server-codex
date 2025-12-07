package net.fina.server.dcs.mail.model;

import net.fina.common.client.constants.MessageStatus;
import net.fina.server.dcs.uploadfile.model.UploadFileMetaModel;

import java.util.Collection;
import java.util.Date;

public class MessageMetaModel {
    private long id;
    private String messageId;
    private String mailUser;
    private String address;
    private String from;
    private MessageStatus status;
    private Date receivedDate;
    private Date readDate;
    private String note;
    private String subject;
    private MessageReplyMetaModel messageReply;
    private Collection<UploadFileMetaModel> uploadFiles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMailUser() {
        return mailUser;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public MessageReplyMetaModel getMessageReply() {
        return messageReply;
    }

    public void setMessageReply(MessageReplyMetaModel messageReply) {
        this.messageReply = messageReply;
    }

    public Collection<UploadFileMetaModel> getUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(Collection<UploadFileMetaModel> uploadFiles) {
        this.uploadFiles = uploadFiles;
    }
}
