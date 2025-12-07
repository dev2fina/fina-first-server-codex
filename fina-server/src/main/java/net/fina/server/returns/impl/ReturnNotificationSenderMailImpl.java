package net.fina.server.returns.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.fina.common.client.constants.MessageReplySendStatus;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.CommonUtil;
import net.fina.messages.MessagesUtil;
import net.fina.server.dcs.mail.api.MailMessageLocal;
import net.fina.server.dcs.mail.entity.MessageReply;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.returns.api.ReturnNotificationSender;
import net.fina.server.returns.qualifier.ReturnNotificationQualifier;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.logging.Logger;

import java.util.Date;
import java.util.List;

@Stateless
@Local(ReturnNotificationSender.class)
@ReturnNotificationQualifier(value = "email")
@Default
public class ReturnNotificationSenderMailImpl implements ReturnNotificationSender {
    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private PropertyLocal propertyLocal;
    @Inject
    private MailMessageLocal mailMessageLocal;


    @Override
    public void send(List<UploadFile> uploadFiles, String returnStatusBundleText, String note, String langCode) {
        if (uploadFiles == null || uploadFiles.isEmpty()) {
            return;
        }
        try {

            for (UploadFile uploadFile : uploadFiles) {
                String userMail = uploadFile.getUser().getEmail();
                String userLogin = uploadFile.getUser().getLogin();

                if (userMail == null || userMail.trim().isBlank()) {
                    log.warn("user [" + userLogin + "] mail is not provided");
                    continue;
                }

                log.info("sending email to user [" + userLogin + "]");
                mailMessageLocal.saveReplay(createMessageReply(uploadFile, userMail, returnStatusBundleText, note, langCode));

            }
        } catch (FinATypeException e) {
            log.error(e.getMessage(), e);
        }

    }


    private MessageReply createMessageReply(UploadFile uploadFile, String mailTo, String returnStatusBundleText, String note, String langCode) throws FinATypeException {

        String mailUser = getMailUser();

        if (mailUser == null) {
            throw new FinATypeException("Mail Properties Are Not Correctly Set!");
        }


        MessageReply messageReply = new MessageReply();

        String subject = MessagesUtil.getString("net.fina.return.status.email.title", langCode);
        String returnStatus = MessagesUtil.getString(returnStatusBundleText, langCode);
        String content = MessagesUtil.getString("net.fina.return.status.email.content", langCode);

        if (note != null && !note.isEmpty()) {
            String noteI18n = MessagesUtil.getString("net.fina.dcs.importStatus.note", langCode);
            content = String.format("%s<br><br>%s: %s", content, noteI18n, note);
        }

        String compiledContent = CommonUtil.compileMessageWithParams(
                content,
                uploadFile.getFileName(),
                uploadFile.getUploadedTime().toString(),
                returnStatus
        );


        String mailAddress = getMailAddress();
        if (mailAddress == null) {
            mailAddress = mailUser;
        }

        messageReply.setSubject(subject);
        messageReply.setSendStatus(MessageReplySendStatus.NOT_SENT);
        messageReply.setSender(mailAddress);
        messageReply.setTo(mailTo);
        messageReply.setContent(compiledContent);
        messageReply.setFrom(mailUser);
        messageReply.setDate(new Date());

        return messageReply;

    }

    private String getMailUser() {
        String mailUser = propertyLocal.getSystemProperty(PropertyKeys.MAIL_ADDRESS);
        if (mailUser != null && !mailUser.trim().isBlank()) {
            return mailUser;
        }

        return null;
    }

    private String getMailAddress() {
        String mailAddress = propertyLocal.getSystemProperty(PropertyKeys.MAIL_ADDRESS);
        if (mailAddress != null && !mailAddress.trim().isBlank()) {
            return mailAddress;
        }
        return null;
    }

}
