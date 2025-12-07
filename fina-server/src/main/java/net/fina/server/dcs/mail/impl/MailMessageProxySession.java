package net.fina.server.dcs.mail.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.MailType;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.MailLogFilter;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.CommonUtil;
import net.fina.common.shared.MailLogMessageReplyMetaModel;
import net.fina.common.shared.MailLogMetaModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.messages.MessagesUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.dcs.mail.api.MailMessageLocal;
import net.fina.server.dcs.mail.entity.MessageReply;
import net.fina.server.dcs.mail.helper.MailLogModelHelper;
import net.fina.server.dcs.uploadfile.api.UploadFileLocal;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.model.UploadFileMetaModel;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class MailMessageProxySession {

    @Inject
    private MailMessageLocal mailMessageLocal;
    @Inject
    private UploadFileLocal uploadFileLocal;
    @Inject
    private PropertyLocal propertyLocal;
    @Inject
    private UserLocal current;

    public PaginatedListWrapper<MailLogMetaModel> loadMailLog(Map<MailLogFilter, Object> filter) {

        int count = 0;
        List<MailLogMetaModel> models = new ArrayList<>();

        if (filter.get(MailLogFilter.TYPE) == MailType.MAIL_ROBOT) {
            models = MailLogModelHelper.toMailMogModels(mailMessageLocal.loadMessages(filter));
            count = (int) mailMessageLocal.messageCount(filter);

        } else if (filter.get(MailLogFilter.TYPE) == MailType.SYSTEM) {
            models = MailLogModelHelper.toSystemMailModels(mailMessageLocal.loadReplyMessages(filter));
            count = (int) mailMessageLocal.systemMessageReplyCount(filter);
        }

        PaginatedListWrapper<MailLogMetaModel> result = new PaginatedListWrapper<>();
        result.setList(models);
        result.setTotalResults(count);
        result.setPageSize((Integer) filter.getOrDefault(MailLogFilter.LIMIT, 0));

        return result;
    }

    public MailLogMessageReplyMetaModel loadMessageReply(long messageId, MailType type) {
        MailLogMessageReplyMetaModel model = new MailLogMessageReplyMetaModel();

        MessageReply mr = (type == MailType.MAIL_ROBOT ? mailMessageLocal.loadMessageReplay(messageId) : mailMessageLocal.loadMessageReplayByReplyId(messageId));
        model.setId(mr.getId());
        model.setFrom(mr.getFrom());
        model.setCc("");
        if (mr.getCc() != null) {
            model.setCc(mr.getCcString());
        }
        model.setBcc("");
        if (mr.getBcc() != null) {
            model.setBcc(mr.getBccString());
        }
        model.setDate(mr.getDate());
        model.setContent(mr.getContent() != null ? mr.getContent() : "");
        model.setSender(mr.getSender());
        model.setSubject(mr.getSubject() != null ? mr.getSubject() : "");
        model.setTo(mr.getToString());

        return model;
    }

    public List<UploadFileMetaModel> loadAttachmentList(long messageId) {
        List<UploadFileMetaModel> models = new ArrayList<>();
        for (UploadFile uploadFile : mailMessageLocal.loadUploadFiles(messageId)) {
            UploadFileMetaModel ufmodel = new UploadFileMetaModel();
            ufmodel.setId(uploadFile.getId());
            ufmodel.setFileName(uploadFile.getFileName());
            models.add(ufmodel);
        }
        return models;
    }

    public UploadFile downloadAttachmentFile(long fileId) {
        return uploadFileLocal.loadUploadFileContent(fileId);
    }

    public String setSynchronizeFlag() throws FinATypeException {
        String mailSyncFlag = propertyLocal.getSystemProperty(PropertyKeys.MAIL_SYNC);

        if (mailSyncFlag != null) {
            String langCode = ThreadLocalHolder.getLanguage().getCode();
            if (Integer.parseInt(mailSyncFlag) <= 0) {
                propertyLocal.setSystemProperty(PropertyKeys.MAIL_SYNC, "1");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String status = current.getCurrentUserLogin() + ";" + sdf.format(new Date());
                propertyLocal.setSystemProperty(PropertyKeys.MAIL_LAST_SYNC_STATUS, status);
                return status;
            } else {
                String checkStatus = checkSynchronizeFlag(langCode);

                String mailSyncServiceNextFireTime = propertyLocal.getSystemProperty(PropertyKeys.SERVICE_MONITOR_MAIL_SERVICE_NEXT_FIRE_TIME);

                if (mailSyncServiceNextFireTime == null || mailSyncServiceNextFireTime.isEmpty()) {
                    throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, checkStatus);
                } else {
                    throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, checkStatus + "." + MessagesUtil.getString("net.fina.serviceMonitor.mailService.nextFireTime", langCode) +
                            mailSyncServiceNextFireTime);
                }
            }

        }
        return "Mail sync property not found!";
    }


    public String checkSynchronizeFlag(String langCode) {
        String message = "";
        String mailSyncFlag = propertyLocal.getSystemProperty(PropertyKeys.MAIL_SYNC);

        if (mailSyncFlag != null) {
            if (Integer.parseInt(mailSyncFlag) > 0) {
                String status = propertyLocal.getSystemProperty(PropertyKeys.MAIL_LAST_SYNC_STATUS);
                if (status != null && !status.isEmpty()) {
                    String[] statusInfo = status.split(";");
                    message = CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.mail.sync.message", langCode), statusInfo[0], statusInfo[1]);
                }
            }
        }

        return message;
    }

}
