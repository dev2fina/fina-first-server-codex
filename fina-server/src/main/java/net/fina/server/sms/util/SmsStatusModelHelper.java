package net.fina.server.sms.util;

import net.fina.common.shared.sms.SmsStatusModel;
import net.fina.server.sms.entity.SmsStatus;

import java.util.ArrayList;
import java.util.List;

public class SmsStatusModelHelper {

    public static List<SmsStatusModel> getSmsStatusModels(List<SmsStatus> smsStatuses) {
        List<SmsStatusModel> result = new ArrayList<>();
        if (smsStatuses != null && !smsStatuses.isEmpty()) {
            for (SmsStatus smsStatus : smsStatuses) {
                result.add(getSmsStatusModel(smsStatus));
            }
        }
        return result;
    }

    public static SmsStatusModel getSmsStatusModel(SmsStatus smsStatus) {
        SmsStatusModel result = new SmsStatusModel();
        result.setId(smsStatus.getId());
        result.setDeliveryDate(smsStatus.getDeliveryDate());
        result.setErrorCode(smsStatus.getErrorCode());
        result.setProvidedMessageId(smsStatus.getProvidedMessageId());
        result.setProviderName(smsStatus.getProviderName());
        result.setSendDate(smsStatus.getSendDate());
        result.setStatus(SmsStatusModel.SmsStatusEnum.values()[smsStatus.getStatus().ordinal()]);

        if (smsStatus.getSms() != null) {
            result.setSmsId(smsStatus.getSms().getId());
            result.setSmsModel(SmsModelHelper.getSmsModel(smsStatus.getSms()));
        }

        return result;
    }

}
