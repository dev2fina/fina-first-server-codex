package net.fina.server.sms.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.sms.entity.Sms;
import net.fina.server.sms.entity.SmsStatus;

import java.util.List;

public interface SmsLocal {

    Sms saveSms(Sms sms);

    Sms getSmsById(Long id);

    void deleteSms(Sms sms);

    SmsStatus saveSmsStatus(SmsStatus smsStatus);

    List<SmsStatus> getLastStatuses(List<Long> smsIds);

    void deleteSmsStatus(SmsStatus smsStatus);

    List<Sms> getPendingSmsMessages();

    List<Sms> loadSmsMessages(List<Long> ids);

    Sms createResetPasswordReplyMessage(String smsTo, String password) throws FinATypeException;

}
