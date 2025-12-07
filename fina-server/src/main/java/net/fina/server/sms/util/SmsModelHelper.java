package net.fina.server.sms.util;

import net.fina.common.shared.sms.SmsModel;
import net.fina.common.shared.sms.SmsStatusModel;
import net.fina.server.sms.entity.Sms;
import net.fina.server.sms.entity.SmsStatus;
import net.fina.server.sms.entity.SmsStatusEnum;

import java.util.ArrayList;
import java.util.List;

public class SmsModelHelper {

    public static Sms getSmsEntity(SmsModel smsModel) {
        Sms sms = new Sms();

        sms.setId(smsModel.getId());
        sms.setProcessing(smsModel.isProcessing());
        sms.setRecipient(smsModel.getRecipient());
        sms.setCreationDate(smsModel.getCreationDate());
        sms.setContent(smsModel.getContent());
        sms.setTitle(smsModel.getTitle());
        sms.setAddress(smsModel.getAddress());

        return sms;
    }

    public static SmsModel getSmsModel(Sms sms) {
        SmsModel smsModel = new SmsModel();

        smsModel.setId(sms.getId());
        smsModel.setProcessing(sms.isProcessing());
        smsModel.setRecipient(sms.getRecipient());
        smsModel.setCreationDate(sms.getCreationDate());
        smsModel.setContent(sms.getContent());
        smsModel.setTitle(sms.getTitle());
        smsModel.setAddress(sms.getAddress());

        return smsModel;
    }

    public static List<SmsModel> getSmsModels(List<Sms> smsList) {
        List<SmsModel> smsModels = new ArrayList<>();
        for (Sms sms : smsList) {
            smsModels.add(getSmsModel(sms));
        }
        return smsModels;
    }

    public static SmsStatusModel getSmsStatusModel(SmsStatus smsStatus) {
        SmsStatusModel smsStatusModel = new SmsStatusModel();

        smsStatusModel.setId(smsStatus.getId());
        smsStatusModel.setProviderName(smsStatus.getProviderName());
        smsStatusModel.setProvidedMessageId(smsStatus.getProvidedMessageId());
        smsStatusModel.setDeliveryDate(smsStatus.getDeliveryDate());
        smsStatusModel.setErrorCode(smsStatus.getErrorCode());
        smsStatusModel.setSendDate(smsStatus.getSendDate());
        if (smsStatus.getSms() != null) {
            smsStatusModel.setSmsId(smsStatus.getSms().getId());
        }
        if (smsStatus.getStatus() != null) {
            smsStatusModel.setStatus(SmsStatusModel.SmsStatusEnum.valueOf(smsStatus.getStatus().name()));
        }

        return smsStatusModel;
    }

    public static SmsStatus getSmsStatus(SmsStatusModel smsStatusModel) {
        SmsStatus smsStatus = new SmsStatus();

        smsStatus.setId(smsStatusModel.getId());
        smsStatus.setProviderName(smsStatusModel.getProviderName());
        smsStatus.setProvidedMessageId(smsStatusModel.getProvidedMessageId());
        smsStatus.setDeliveryDate(smsStatusModel.getDeliveryDate());
        smsStatus.setErrorCode(smsStatusModel.getErrorCode());
        smsStatus.setSendDate(smsStatusModel.getSendDate());
        if (smsStatusModel.getStatus() != null) {
            smsStatus.setStatus(SmsStatusEnum.valueOf(smsStatusModel.getStatus().name()));
        }

        return smsStatus;
    }

}
