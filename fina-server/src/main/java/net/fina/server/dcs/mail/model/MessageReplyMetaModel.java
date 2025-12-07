package net.fina.server.dcs.mail.model;

import net.fina.common.client.constants.MessageReplySendStatus;

import java.util.Date;
import java.util.List;

public class MessageReplyMetaModel {
    protected List<String> bcc;
    protected List<String> cc;
    protected String content;
    protected Date date;
    protected String from;
    protected long id;
    protected MessageReplySendStatus sendStatus;
    protected String sender;
    protected String subject;
    protected List<String> to;

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MessageReplySendStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(MessageReplySendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }
}
