package net.fina.server.dcs.uploadfile.impl;

import jakarta.ejb.*;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import net.fina.common.client.constants.MessageReplySendStatus;
import net.fina.common.client.constants.MessageStatus;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.property.PropertyKeys;
import net.fina.server.dcs.mail.api.MailMessageLocal;
import net.fina.server.dcs.mail.entity.Message;
import net.fina.server.dcs.mail.entity.MessageReply;
import net.fina.server.dcs.mail.impl.MailResponseProcessor;
import net.fina.server.dcs.uploadfile.api.UploadFileMailMessageLocal;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.fsop.event.ProcessMailMessageEvent;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.notification.proxy.SysNotificationProxySession;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.entity.User;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(UploadFileMailMessageLocal.class)
@Interceptors(RecordingAuditor.class)
public class UploadFileMailMessageSession implements UploadFileMailMessageLocal {

    @Inject
    private Logger log;
    @Inject
    private EntityManager em;

    @EJB
    private MailMessageLocal mailMessageLocal;
    @EJB
    private PropertyLocal propertyLocal;

    @Inject
    private SysNotificationProxySession sysNotificationProxySession;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveProcessingMailMessage(@Observes(during = TransactionPhase.AFTER_SUCCESS) ProcessMailMessageEvent event) {
        try {
            if (checkProcessingMailEnable()) {

                MailResponseProcessor processor = new MailResponseProcessor(getDefaultLanguageCode());

                UploadFile uploadFile = em.find(UploadFile.class, event.getUploadFileQueue().getFileId());

                Map<Long, List<FsopImportedReturnMetaModel>> uploadFileImportedReturns = new HashMap<>();
                uploadFileImportedReturns.put(uploadFile.getId(), event.getImportedReturns());

                switch (uploadFile.getType()) {
                    case SUBMISSION_TOOL:
                    case MANUAL:
                        break;
                    case DCS:
                        saveMessageReplyForResponsibleUsers(uploadFile, processor, uploadFileImportedReturns);
                        sendNotification(uploadFile, processor, uploadFileImportedReturns);
                        break;
                    case EMAIL_ROBOT:
                        //Find upload file message
                        Message m = findMessageByFileId(event.getUploadFileQueue().getFileId());

                        //Load Message upload files imported returns
                        List<Long> fileIds = m.getUploadFiles().stream().filter(uf -> event.getUploadFileQueue().getFileId() != uf.getId()).map(UploadFile::getId).collect(Collectors.toList());
                        if (!fileIds.isEmpty()) {
                            loadUploadFileImportedReturnsReturnCodeAndMessage(uploadFileImportedReturns, fileIds);
                        }

                        //Check message status
                        if (m.getStatus() != MessageStatus.IGNORE_REPLAY) {
                            //Check finish process
                            if (checkConvertMessageUploadFiles(m, Arrays.asList(Integer.toString(UploadFileStatus.CONVERTED.ordinal()), Integer.toString(UploadFileStatus.UPLOADED.ordinal())))) {
                                mailMessageLocal.saveMessageReplay(m.getId(), createMessageReply(m, processor.process(m, uploadFileImportedReturns)));
                            }
                        }
                        break;
                }
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    private boolean checkProcessingMailEnable() {
        String enable = propertyLocal.getSystemProperty(PropertyKeys.PROCESSING_MAIL_ENABLE);
        return enable != null && (!enable.isEmpty()) && (Integer.parseInt(enable) > 0);
    }

    private String getDefaultLanguageCode() {
        String defaultLanguageCode = propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_LANGUAGE);

        if (defaultLanguageCode == null || defaultLanguageCode.trim().equals("")) {
            defaultLanguageCode = MailResponseProcessor.DEFAULT_LANGUAGE;
        }
        return defaultLanguageCode;
    }

    private boolean checkConvertMessageUploadFiles(Message m, List<String> uploadFileCheckStatuses) {
        return em.createQuery("select uf.id from SYS_UPLOADEDFILE uf where uf.status in(:statuses) and uf.id in(:messageUploadFiles)")
                .setParameter("statuses", uploadFileCheckStatuses)
                .setParameter("messageUploadFiles", m.getUploadFiles().stream().map(UploadFile::getId).collect(Collectors.toList()))
                .getResultList().isEmpty();
    }

    private Message findMessageByFileId(long fileId) {
        return em.createQuery("select mm from IN_MAIL_MESSAGE  mm, In(mm.uploadFiles) uf where uf.id=:fileId", Message.class)
                .setParameter("fileId", fileId)
                .getSingleResult();
    }

    private void loadUploadFileImportedReturnsReturnCodeAndMessage(Map<Long, List<FsopImportedReturnMetaModel>> uploadFileImportedReturns, List<Long> uploadFilesIds) {
        List<Object[]> queryResult = em.createQuery("select ir.uploadFile.id,ir.returnCode,ir.message from IN_IMPORTED_RETURNS ir where ir.uploadFile.id in(:uploadFileIds)", Object[].class)
                .setParameter("uploadFileIds", uploadFilesIds)
                .getResultList();

        for (Object[] objects : queryResult) {

            long fileId = (long) objects[0];

            FsopImportedReturnMetaModel ir = new FsopImportedReturnMetaModel();
            ir.setReturnCode(objects[1] != null ? objects[1].toString() : "");
            ir.setMessage(objects[2] != null ? objects[2].toString() : "");

            List<FsopImportedReturnMetaModel> importedReturns = uploadFileImportedReturns.get(fileId);
            if (importedReturns == null) {
                importedReturns = new ArrayList<>();
                uploadFileImportedReturns.put(fileId, importedReturns);
            }
            importedReturns.add(ir);
        }
    }

    private MessageReply createMessageReply(Message m, String content) {
        MessageReply messageReply = new MessageReply();
        messageReply.setFrom(m.getAddress());
        messageReply.setTo(m.getFrom());
        messageReply.setSender(m.getFrom());
        messageReply.setDate(new Date());
        messageReply.setSendStatus(MessageReplySendStatus.NOT_SENT);
        messageReply.setSubject(MailResponseProcessor.getSubject(m.getSubject()));
        messageReply.setContent(content);
        messageReply.setCc(mailMessageLocal.getMailResponseCC());
        return messageReply;
    }

    private void saveMessageReplyForResponsibleUsers(UploadFile uploadFile, MailResponseProcessor mailResponseProcessor, Map<Long, List<FsopImportedReturnMetaModel>> uploadFileImportedReturns) {
        MessageReply messageReply = mailMessageLocal.createMessageReplyForResponsibleUsers(uploadFile);
        if (messageReply != null) {
            messageReply.setContent(mailResponseProcessor.process(uploadFile, uploadFileImportedReturns));
            mailMessageLocal.saveReplay(messageReply);
        }
    }

    private void sendNotification(UploadFile uploadFile, MailResponseProcessor mailResponseProcessor, Map<Long, List<FsopImportedReturnMetaModel>> uploadFileImportedReturns) {

        List<User> responsibleUsers = em.createQuery("SELECT DISTINCT u FROM IN_BANKS b, IN(b.users) u WHERE b.code=:bankCode", User.class)
                .setParameter("bankCode", uploadFile.getBankCode())
                .getResultList();

        String content = mailResponseProcessor.process(uploadFile, uploadFileImportedReturns);

        sysNotificationProxySession.notifyUsers(responsibleUsers, content);

    }
}
