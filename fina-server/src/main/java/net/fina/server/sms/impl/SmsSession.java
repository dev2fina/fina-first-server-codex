package net.fina.server.sms.impl;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.sms.api.SmsLocal;
import net.fina.server.sms.entity.Sms;
import net.fina.server.sms.entity.SmsStatus;
import net.fina.server.sms.entity.SmsStatusEnum;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless
@Local(SmsLocal.class)
@Interceptors(RecordingAuditor.class)
public class SmsSession implements SmsLocal {

    @Inject
    private EntityManager em;

    @Override
    public Sms saveSms(Sms sms) {
        if (sms.getId() > 0) {
            return em.merge(sms);
        }

        sms.setId(0);
        em.persist(sms);
        return sms;
    }

    @Override
    public Sms getSmsById(Long id) {
        return em.find(Sms.class, id);
    }

    @Override
    public void deleteSms(Sms sms) {
        em.remove(sms);
    }

    @Override
    public SmsStatus saveSmsStatus(SmsStatus smsStatus) {
        if (smsStatus.getId() > 0) {
            return em.merge(smsStatus);
        }

        smsStatus.setId(0);
        em.persist(smsStatus);
        return smsStatus;
    }

    @Override
    public List<SmsStatus> getLastStatuses(List<Long> smsIds) {
        TypedQuery<SmsStatus> query = em.createQuery("select ss from SmsStatus ss where ss.id in (select max(s.id) from SmsStatus s group by s.sms.id) and ss.sms.id in :smsIds", SmsStatus.class);
        query.setParameter("smsIds", smsIds);

        return query.getResultList();
    }

    @Override
    public void deleteSmsStatus(SmsStatus smsStatus) {
        em.remove(smsStatus);
    }

    @Override
    public List<Sms> getPendingSmsMessages() {
        return em.createQuery("select s from Sms s " +
                "where s.isProcessing = false and " +
                "s.id not in (select st.sms.id from SmsStatus st " +
                "where st.sms is not null and st.status in :statuses)", Sms.class)
                .setParameter("statuses", Arrays.asList(SmsStatusEnum.SENT, SmsStatusEnum.DELIVERED))
                .getResultList();
    }

    @Override
    public List<Sms> loadSmsMessages(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            return em.createQuery("select s from Sms s where s.id in :ids", Sms.class).setParameter("ids", ids).getResultList();
        }
        return new ArrayList<>();
    }

    @Override
    public Sms createResetPasswordReplyMessage(String smsTo, String password) throws FinATypeException {
        if (smsTo == null) {
            throw new FinATypeException("Phone number is mandatory!");
        }

        Sms sms = new Sms();
        sms.setTitle(ConfigurationUtil.get().get("SMS.passwordResetTitle"));
        sms.setContent(password);
        sms.setCreationDate(new Date());
        sms.setAddress(smsTo);
        sms.setRecipient("");

        return saveSms(sms);
    }

}
