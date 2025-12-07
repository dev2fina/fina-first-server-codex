package net.fina.server.dcs.mail.api;

import net.fina.common.client.constants.MessageReplySendStatus;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.MailLogFilter;
import net.fina.server.dcs.mail.entity.Message;
import net.fina.server.dcs.mail.entity.MessageReply;
import net.fina.server.dcs.uploadfile.entity.UploadFile;

import java.util.List;
import java.util.Map;

public interface MailMessageLocal {

    Message saveMessage(Message message, String languageCode);

    MessageReply saveMessageReplay(long messageId, MessageReply messageReply);

    MessageReply saveReplay(MessageReply messageReply);

    void updateMailReplayStatus(long messageReplyId, MessageReplySendStatus status);

    List<MessageReply> loadNotSentMessageReplays(String mailUser);

    List<String> loadActiveMessageIds(String mailUser);

    List<String> loadActiveMessageIds(String mailUser, int offset, int limit);

    int getActiveMessageIdCount(String mailUser);

    List<Object[]> loadMessages(Map<MailLogFilter, Object> filter);

    List<MessageReply> loadReplyMessages(Map<MailLogFilter, Object> filter);

    long messageCount(Map<MailLogFilter, Object> filter);

    long systemMessageReplyCount(Map<MailLogFilter, Object> filter);

    MessageReply loadMessageReplay(long messageId);

    MessageReply loadMessageReplayByReplyId(long replyId);

    List<UploadFile> loadUploadFiles(long messageId);

    MessageReply createMessageReplyForResponsibleUsers(UploadFile uploadFile);

    void resetPasswordReplyMessage(String mailTo, String password, String langCode) throws FinATypeException;

    String[] getMailResponseCC();

    void archiveMails(List<String> messageIds);

    void setMailServiceActiveStatus(String date);

    void setMailServiceLastJobStatus(String date);

    void setMailServiceNextFireTime(String date);

}
