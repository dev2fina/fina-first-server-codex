package net.fina.server.communicator.api;

import net.fina.common.client.constants.CommunicatorAttachmentType;
import net.fina.common.client.constants.CommunicatorReadStatus;
import net.fina.common.client.constants.MessageFilter;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.FileSignerException;
import net.fina.common.shared.SortField;
import net.fina.common.shared.WrongFileTypeException;
import net.fina.common.shared.comunicator.CommunicatorAttachmentModel;
import net.fina.common.shared.comunicator.CommunicatorDashletModel;
import net.fina.common.shared.comunicator.CommunicatorMessageMarkType;
import net.fina.common.shared.comunicator.CommunicatorMessageModel;
import net.fina.server.communicator.entity.*;
import net.fina.server.legislative.event.LegislativeDocumentNotificationEvent;
import net.fina.server.security.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CommunicatorLocal {

    CommunicatorNotification saveNotification(CommunicatorNotification notification) throws FinATypeException;

    CommunicatorNotification saveNotificationWithAttachments(CommunicatorNotification notification, List<CommunicatorAttachment> attachments, List<Long> userIds) throws WrongFileTypeException, FileSignerException, FinATypeException;

    void deleteNotification(long notificationId);

    List<CommunicatorNotificationUser> loadCommunicationUsers(long notificationId);

    List<CommunicatorNotificationUser> loadCommunicationUsers(long notificationId, int offset, int limit, Map<MessageFilter, String> filterMap, SortField sortField);

    void publishCommunicatorNotification(long notificationId);

    List<CommunicatorNotification> loadUserNotReadNotifications();

    List<String> loadAllNotReadNotificationsUsers();

    List<CommunicatorNotification> loadAll(int start, int limit, SortField sortField);

    void submitReadNotification(long id);

    List<CommunicatorMessage> loadCommunicatorMessages(Map<MessageFilter, String> filterMap, long offset, long limit);

    List<CommunicatorNotification> loadCommunicatorNotifications(Map<MessageFilter, String> filterMap, long offset, long limit);

    List<MessageUser> loadMessageUsers(long messageId, Map<MessageFilter, String> filterMap, long offset, long limit, SortField sortField);

    List<CommunicatorMessage> loadConversation(long messageId, long userId, boolean pending, boolean bookmarked);

    List<CommunicatorMessage> loadConversation(long messageId, long userId, boolean pending, boolean bookmarked, int start, int limit, SortField sortField);

    CommunicatorMessage saveCommunicatorMessage(CommunicatorMessage message, CommunicatorMessageModel model, List<CommunicatorAttachment> attachments, long replyToid, long replyToUserId) throws FinATypeException, WrongFileTypeException, FileSignerException;

    CommunicatorMessage saveMessage(CommunicatorMessage message) throws FinATypeException;

    CommunicatorMessage saveMessage(CommunicatorMessage message, long rootMessageId, long replyToUserId) throws FinATypeException;

    void deleteMessage(long messageId);

    List<CommunicatorMessage> loadUserMessages(int offset, int limit, SortField sortField);

    List<String> loadSentMessagesUsers();

    CommunicatorMessage saveMessageFromDcs(long rootMessageId, CommunicatorMessage message, List<CommunicatorAttachment> attachments) throws WrongFileTypeException, FileSignerException;

    void markMessageAsRead(long rootMessageId);

    void saveAttachment(CommunicatorAttachment attachment) throws WrongFileTypeException, FileSignerException, FinATypeException;

    List<CommunicatorAttachment> getAttachmentsByMessageId(long messageId, CommunicatorAttachmentType type, boolean withContent);

    void validateUserAccessOnAttachment(long attachmentId, long userId, CommunicatorAttachmentType attachmentType) throws FinATypeException;

    void updateMessageUserStatus(long messageUserId, long messageId, long currentUserId, CommunicatorReadStatus status);

    void updateMessageUserStatus(long messageUserId, long messageId, CommunicatorReadStatus status);

    void acceptRejectMessage(long messageUserId, long messageUserMessageId, long messageId, boolean accept, String rejectionNote);

    void acceptRejectRootMessage(long messageId, boolean accept);

    void setLegislativeDocumentNotification(LegislativeDocumentNotificationEvent event) throws FinATypeException;

    void deleteNotifications(List<Long> notificationIds);

    void deleteMessages(List<Long> messageIds);

    long countNewMessages();

    CommunicatorAttachment getAttachmentById(long id);

    long countPendingMessageUsers(MessageUser mu);

    long countPendingMessage(CommunicatorMessage message);

    boolean isNotRepliedMessageUser(MessageUser mu);

    Map<CommunicatorDashletModel.MessagesStatistics, Object> loadMessageStatistics();

    long countNotifications(Map<MessageFilter, String> filterMap);

    long countAllMessages(Map<MessageFilter, String> filterMap);

    long countMessageUsers(long messageId, Map<MessageFilter, String> filterMap);

    long countUserNotifications();

    long countUserMessages();

    void markAllMessagesAsRead();

    void markAllMessageThreadsAsRead(long messageId);

    void updateAttachments(Collection<CommunicatorAttachmentModel> attachments, long messageId);

    CommunicatorMessage markMessage(long messageId, CommunicatorMessageMarkType markType);

    long countUnreadMessagesInConversation(long messageId, long userId);

    CommunicatorMessage getMessageById(long messageId);

    long countConversationMessages(long rootMessageId, long userId);

    CommunicatorMessageModel countNewAndCheckPending(CommunicatorMessage message, User currentUser, CommunicatorMessageModel messageModel);

    boolean checkUserHasReadMessage(MessageUser mu, User user);

    long countNotificationUsers(long notificationId, Map<MessageFilter, String> filterMap);

    CommunicatorMessage loadLastConversationMessage(long messageId, long userId);

    long countRecipientNewMessages();

    List<Long> loadRootMessageRecipientUserIds(long rootMessageId);

    List<Long> loadNotificationRecipientUserIds(long notificationId);

    List<User> loadRootMessageRecipients(long rootMessageId);

    List<User> loadNotificationRecipients(long notificationId);

    void saveNotificationUsers(List<CommunicatorNotificationUser> userList);

    void sendSavedMessage(long messageId) throws FinATypeException;
}