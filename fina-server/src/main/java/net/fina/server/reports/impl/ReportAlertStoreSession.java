package net.fina.server.reports.impl;

import net.fina.common.client.constants.MessageReplySendStatus;
import net.fina.common.client.property.PropertyKeys;
import net.fina.report.model.ReportAlertMetaModel;
import net.fina.server.dcs.mail.api.MailMessageLocal;
import net.fina.server.dcs.mail.entity.MessageReply;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.reports.event.ReportAlertsEvent;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.logging.Logger;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.interceptor.Interceptors;
import java.util.Date;

@Stateless
@Interceptors(RecordingAuditor.class)
public class ReportAlertStoreSession {

    private Logger log = Logger.getLogger(getClass());

    @EJB
    private MailMessageLocal mailMessageLocal;
    @EJB
    private PropertyLocal propertyLocal;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storeReportAlerts(@Observes(during = TransactionPhase.AFTER_SUCCESS) ReportAlertsEvent event) {

        if (event.getAlerts() != null) {

            String fromEmail = propertyLocal.getSystemProperty(PropertyKeys.MAIL_ADDRESS);

            for (ReportAlertMetaModel alert : event.getAlerts()) {

                try {
                    MessageReply messageReply = new MessageReply();
                    messageReply.setSubject(alert.getDescription());
                    messageReply.setContent(alert.getMessage());
                    messageReply.setTo(alert.getEmails().toArray(new String[0]));
                    messageReply.setDate(new Date());
                    messageReply.setSendStatus(MessageReplySendStatus.NOT_SENT);

                    messageReply.setFrom(fromEmail);
                    messageReply.setSender(fromEmail);

                    mailMessageLocal.saveReplay(messageReply);

                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }

            }
        }
    }
}
