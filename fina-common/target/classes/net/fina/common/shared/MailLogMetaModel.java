package net.fina.common.shared;

import net.fina.common.client.constants.MailType;
import net.fina.common.client.constants.MessageReplySendStatus;
import net.fina.common.client.constants.MessageStatus;

import java.util.Date;

public class MailLogMetaModel {
    private long id;
    private MailType mailType;
    private String mailId;
    private String address;
    private String fromAddress;
    private String toAddress;
    private String mailUser;
    private Date receiveDate;
    private Date readDate;
    private MessageStatus status;
    private MessageReplySendStatus replyStatus;
    private String note;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MailType getMailType() {
        return mailType;
    }

    public void setMailType(MailType mailType) {
        this.mailType = mailType;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getMailUser() {
        return mailUser;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public MessageReplySendStatus getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(MessageReplySendStatus replyStatus) {
        this.replyStatus = replyStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
