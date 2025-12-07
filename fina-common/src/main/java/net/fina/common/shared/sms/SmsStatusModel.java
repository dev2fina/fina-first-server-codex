package net.fina.common.shared.sms;

import java.io.Serializable;
import java.util.Date;

public class SmsStatusModel implements Serializable {

    public enum SmsStatusEnum {
        SENT,
        SEND_FAILED,
        DELIVERED,
        DELIVERY_FAILED
    }

    private long id;
    private String providedMessageId;
    private String providerName;
    private Date sendDate;
    private SmsStatusEnum status;
    private int errorCode;
    private Date deliveryDate;
    private long smsId;
    private SmsModel smsModel;

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

    public long getSmsId() {
        return smsId;
    }

    public void setSmsId(long smsId) {
        this.smsId = smsId;
    }

    public SmsModel getSmsModel() {
        return smsModel;
    }

    public void setSmsModel(SmsModel smsModel) {
        this.smsModel = smsModel;
    }
}
