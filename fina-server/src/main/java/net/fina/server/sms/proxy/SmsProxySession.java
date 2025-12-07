package net.fina.server.sms.proxy;

import net.fina.common.shared.sms.SmsModel;
import net.fina.common.shared.sms.SmsStatusModel;
import net.fina.server.sms.api.SmsLocal;
import net.fina.server.sms.entity.Sms;
import net.fina.server.sms.entity.SmsStatus;
import net.fina.server.sms.util.SmsModelHelper;
import net.fina.server.sms.util.SmsStatusModelHelper;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class SmsProxySession {

    @EJB
    private SmsLocal smsLocal;

    public SmsModel saveSms(SmsModel smsModel) {
        Sms sms = SmsModelHelper.getSmsEntity(smsModel);
        return SmsModelHelper.getSmsModel(smsLocal.saveSms(sms));
    }

    public List<SmsStatusModel> loadLastStatuses(List<Long> smsIds) {
        return SmsStatusModelHelper.getSmsStatusModels(smsLocal.getLastStatuses(smsIds));
    }

    public List<SmsModel> loadSmsMessages(List<Long> ids) {
        return SmsModelHelper.getSmsModels(smsLocal.loadSmsMessages(ids));
    }

    public List<SmsModel> getPendingSmsMessages() {
        return SmsModelHelper.getSmsModels(smsLocal.getPendingSmsMessages());
    }

    public void sendSmsMessages(List<Long> smsIds) {
        for (Long id : smsIds) {
            Sms sms = smsLocal.getSmsById(id);
            sms.setProcessing(true);
            smsLocal.saveSms(sms);
        }
    }

    public void saveSmsMessageStatus(SmsStatusModel smsStatusModel) {
        SmsStatus smsStatus = SmsModelHelper.getSmsStatus(smsStatusModel);
        Sms parentSms = smsLocal.getSmsById(smsStatusModel.getSmsId());

        smsStatus.setSms(parentSms);
        smsLocal.saveSmsStatus(smsStatus);

        parentSms.setProcessing(false);
        smsLocal.saveSms(parentSms);
    }

}
