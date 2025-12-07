package net.fina.server.dcs.mail.entity;

import net.fina.common.client.constants.MessageStatus;
import net.fina.server.dcs.uploadfile.entity.UploadFile;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity(name = "IN_MAIL_MESSAGE")
@Table(name = "IN_MAIL_MESSAGE")
public class Message implements Serializable {

    @Id
    @SequenceGenerator(name = "in_mail_message_sequence", sequenceName = "in_mail_message_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_mail_message_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Column(name = "MESSAGE_ID", length = 512)
    private String messageId;

    @Column(name = "MAIL_USER")
    private String mailUser;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "FROM_ADDRESS")
    private String from;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS")
    private MessageStatus status;

    @Column(name = "RECIVE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date receivedDate;

    @Column(name = "READ_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date readDate;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "SUBJECT")
    private String subject;

    @OneToOne
    @JoinColumn(name = "MESSAGE_REPLAY_ID", nullable = true)
    private MessageReply messageReply;

    @OneToMany
    @JoinTable(name = "IN_MAIL_MESSAGE_UPLOADFILES")
    private Collection<UploadFile> uploadFiles;

    public Message() {
    }

    public Message(long id, String messageId, String mailUser, String address, String from, MessageStatus status, Date receivedDate, Date readDate, String note) {
        this.id = id;
        this.messageId = messageId;
        this.mailUser = mailUser;
        this.address = address;
        this.from = from;
        this.status = status;
        this.receivedDate = receivedDate;
        this.readDate = readDate;
        this.note = note;
    }

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

    public MessageReply getMessageReply() {
        return messageReply;
    }

    public void setMessageReply(MessageReply messageReply) {
        this.messageReply = messageReply;
    }

    public Collection<UploadFile> getUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(Collection<UploadFile> uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "messageId='" + messageId + '\'' + ", id=" + id;
    }
}
