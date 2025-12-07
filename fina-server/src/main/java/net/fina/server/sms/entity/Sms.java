package net.fina.server.sms.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "OUT_SMS_MESSAGE")
public class Sms {

    @Id
    @SequenceGenerator(name = "sms_messages_sequence", sequenceName = "sms_messages_sequence", allocationSize = 1)
    @GeneratedValue(generator = "sms_messages_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    private String recipient;
    private String address;
    private String title;
    private String content;
    private Date creationDate;
    private boolean isProcessing;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public void setProcessing(boolean processing) {
        isProcessing = processing;
    }
}
