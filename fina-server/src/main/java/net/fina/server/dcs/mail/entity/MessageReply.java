package net.fina.server.dcs.mail.entity;

import net.fina.common.client.constants.MessageReplySendStatus;
import org.apache.commons.lang.StringUtils;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
@Entity(name = "OUT_MAIL_MESSAGE_REPLY")
@Table(name = "OUT_MAIL_MESSAGE_REPLY")
public class MessageReply implements Serializable {

    @Id
    @SequenceGenerator(name = "message_replay_sequence", sequenceName = "message_replay_sequence", allocationSize = 1)
    @GeneratedValue(generator = "message_replay_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Column(name = "MAIL_FROM", nullable = false)
    private String from;

    @Column(name = "MAIL_TO", nullable = false, length = 512)
    private String to;

    @Column(name = "MAIL_CC")
    private String cc;

    @Column(name = "MAIL_BCC")
    private String bcc;

    @Column(name = "MAIL_SENDER", nullable = false)
    private String sender;

    @Column(name = "MAIL_CONTENT")
    private String content;

    @Column(name = "MAIL_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MAIL_SEND_STATUS", nullable = false)
    private MessageReplySendStatus sendStatus;

    @Column(name = "MAIL_SUBJECT")
    private String subject;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String[] getTo() {
        if (to != null && (!to.trim().equals(""))) {
            return to.split("[,|;]");
        }
        return null;
    }

    public String getToString() {
        return to;
    }

    public void setTo(String... to) {
        this.to = arrayToString(to);
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = StringUtils.left(content, 4000);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MessageReplySendStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(MessageReplySendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String[] getCc() {
        if (cc != null && (!cc.trim().equals(""))) {
            return cc.split("[,|;]");
        }
        return null;
    }

    public String getCcString() {
        return cc;
    }

    public void setCc(String[] cc) {
        this.cc = arrayToString(cc);
    }

    public String[] getBcc() {
        if (bcc != null && (!cc.trim().equals(""))) {
            return bcc.split("[,|;]");
        }
        return null;
    }

    public String getBccString() {
        return bcc;
    }

    public void setBcc(String[] bcc) {
        this.bcc = arrayToString(bcc);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    private String arrayToString(String[] array) {
        if (array == null) {
            return null;
        }
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                buff.append(",");
            }
            buff.append(array[i]);
        }
        return buff.toString();
    }

    @Override
    public String toString() {
        return "[id=" + id + ", from=" + from + ", sender=" + sender + ", date=" + date + ", sendStatus=" + sendStatus + "]";
    }

}
