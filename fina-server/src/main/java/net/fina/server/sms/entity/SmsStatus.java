package net.fina.server.sms.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "OUT_SMS_MESSAGE_STATUS")
public class SmsStatus {

    @Id
    @SequenceGenerator(name = "sms_messages_status_sequence", sequenceName = "sms_messages_status_sequence", allocationSize = 1)
    @GeneratedValue(generator = "sms_messages_status_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    private String providedMessageId;
    private String providerName;
    private Date sendDate;
    private SmsStatusEnum status;
    private int errorCode;
    private Date deliveryDate;

    @ManyToOne
    private Sms sms;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProvidedMessageId() {
        return providedMessageId;
    }

    public void setProvidedMessageId(String providedMessageId) {
        this.providedMessageId = providedMessageId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public SmsStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SmsStatusEnum status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Sms getSms() {
        return sms;
    }

    public void setSms(Sms sms) {
        this.sms = sms;
    }
}
