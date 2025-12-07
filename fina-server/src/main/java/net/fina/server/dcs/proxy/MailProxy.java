package net.fina.server.dcs.proxy;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jws.WebParam;
import net.fina.common.client.constants.MessageReplySendStatus;
import net.fina.common.client.property.PropertyKeys;
import net.fina.server.dcs.mail.api.MailMessageLocal;
import net.fina.server.dcs.mail.entity.MessageReply;
import net.fina.server.dcs.mail.model.*;
import net.fina.server.security.api.PropertyLocal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Stateless
public class MailProxy {

    @Inject
    private MailMessageLocal mailMessageLocal;

    @EJB
    private PropertyLocal propertyLocal;

    public MailMessageIdFullListMetaModel loadMessageIdList(String mailUser, MailMessageIdFullListMetaModel model) {
        model.setList(mailMessageLocal.loadActiveMessageIds(mailUser, model.getCurrentPage() * model.getPageSize(), model.getPageSize()));
        model.setTotalResults(mailMessageLocal.getActiveMessageIdCount(mailUser));
        return model;
    }

    public String getPropertyValue(@WebParam(name = "key") String key) {
        return propertyLocal.getSystemProperty(key);
    }

    public List<MessageReply> loadNotSentMessageReplays(String mailUser) {
        return mailMessageLocal.loadNotSentMessageReplays(mailUser);
    }

    public void archiveMails(List<String> messageIds) {
        mailMessageLocal.archiveMails(messageIds);
    }

    public void updateMailReplayStatus(long messageReplyId, MessageReplySendStatus status) {
        mailMessageLocal.updateMailReplayStatus(messageReplyId, status);
    }

    public void updateRejecteMailProperty(String rejectedMail) {
        String rejectedMails = propertyLocal.getSystemProperty(PropertyKeys.MAIL_NOT_READ);
        StringBuilder sb = new StringBuilder();
        if (rejectedMails == null || rejectedMails.trim().isEmpty()) {
            sb.append(rejectedMail);
        } else {
            sb.append(rejectedMails)
                    .append(",")
                    .append(rejectedMail);
        }
        propertyLocal.setSystemProperty(PropertyKeys.MAIL_NOT_READ, sb.toString());
    }

    public void setMailServiceActiveStatus(String date) {
        mailMessageLocal.setMailServiceActiveStatus(date);
    }

    public void setMailServiceLastJobStatus(String date) {
        mailMessageLocal.setMailServiceLastJobStatus(date);
    }

    public void setMailServiceNextFireTime(long date, String timeZoneId) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        mailMessageLocal.setMailServiceNextFireTime(df.format(new Date(date)));
    }

    public MessageReplyMetaModel saveMessageReplay(long messageId, MessageReplyMetaModel messageReply) {
        return MessageReplyMetaModelHelper.toMetaModel(
                mailMessageLocal.saveMessageReplay(messageId, MessageReplyMetaModelHelper.toEntity(messageReply)));
    }

    public MessageMetaModel saveMessage(MessageMetaModel message, String languageCode) {
        return MessageMetaModelHelper.toMetaModel(
                mailMessageLocal.saveMessage(MessageMetaModelHelper.toEntity(message), languageCode));
    }
}
