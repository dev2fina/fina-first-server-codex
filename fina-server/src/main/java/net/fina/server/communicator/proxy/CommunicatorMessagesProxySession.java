package net.fina.server.communicator.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import net.fina.common.client.constants.*;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.*;
import net.fina.common.shared.comunicator.*;
import net.fina.common.shared.user.UserModelSimple;
import net.fina.messages.MessagesUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.communicator.api.CommunicatorLocal;
import net.fina.server.communicator.entity.CommunicatorAttachment;
import net.fina.server.communicator.entity.CommunicatorMessage;
import net.fina.server.communicator.entity.MessageUser;
import net.fina.server.communicator.entity.MessageUserId;
import net.fina.server.communicator.event.message.CommunicatorMessageEvent;
import net.fina.server.communicator.model.CommunicatorModelHelper;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.Permission;
import net.fina.server.security.entity.User;
import net.fina.server.util.SortUtil;
import net.fina.common.server.StatisticsLogger;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.sql.BatchUpdateException;
import java.util.*;
import java.util.stream.Collectors;

import static net.fina.server.communicator.model.CommunicatorModelHelper.getMessageModel;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_REVIEW)
public class CommunicatorMessagesProxySession {

    @Inject
    private CommunicatorLocal communicatorLocal;

    @Inject
    private UserLocal userLocal;

    @Inject
    private Event<CommunicatorMessageEvent> communicatorMessageEvent;

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_REVIEW)
    public PaginatedListWrapper<CommunicatorMessageModel> loadUserMessages(int start, int limit, SortField sortField) {
        User current = userLocal.getCurrentUser();
        List<CommunicatorMessage> messages = communicatorLocal.loadUserMessages(start, limit, sortField);
        List<CommunicatorMessageModel> models = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();

        for (CommunicatorMessage message : messages) {
            CommunicatorMessageModel model = getMessageModel(message, langId);
            int messageCount = (int) communicatorLocal.countUnreadMessagesInConversation(message.getId(), current.getId());
            model.setNewMessages(messageCount);
            models.add(model);
        }
        return new PaginatedListWrapper<>(models, limit, communicatorLocal.countUserMessages());
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_REVIEW)
    public List<CommunicatorMessageModel> loadConversation(long messageId, long userId, boolean bookmarked) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<CommunicatorMessage> conversationMessages = communicatorLocal.loadConversation(messageId, userId, false, bookmarked);
        List<CommunicatorMessageModel> modelList = conversationMessages.stream().map(m -> getMessageModel(m, langId)).collect(Collectors.toList());
        String userLogin = userLocal.findUserbyId(userId).getLogin();

        boolean hasUserAcceptPermission = ThreadLocalHolder.getThreadLocalRequest().isUserInRole(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_ACCEPT);

        for (CommunicatorMessageModel model : modelList) {
            model.setAttachments(getMessageAttachments(model.getId(), CommunicatorAttachmentType.MESSAGE));
            model.setSelf(!model.getUserLogin().equals(userLogin));
            model.setHasAcceptPermission(hasUserAcceptPermission);
            model.setSign(model.getSign());
        }

        return modelList;
    }


    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_REVIEW)
    public PaginatedListWrapper<CommunicatorMessageModel> loadConversation(long rootMessageId, long userId, int start, int limit, boolean bookmarked, SortField sortField) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        boolean isExternalUser = userId == 0;
        userId = userId > 0 ? userId : userLocal.getCurrentUserId();

        long totalMessageCount = communicatorLocal.countConversationMessages(rootMessageId, userId);
        totalMessageCount = totalMessageCount > 0 ? totalMessageCount + 1 : totalMessageCount;

        List<CommunicatorMessage> messages = communicatorLocal.loadConversation(rootMessageId, userId, isExternalUser, bookmarked, start, limit, sortField);

        CommunicatorMessage startMessage = communicatorLocal.getMessageById(rootMessageId);
        if (sortField == null || sortField.getDirection().equalsIgnoreCase("asc")) {
            if (start <= 0 && (!bookmarked || startMessage.getMarkType() != null)) {
                messages.add(0, startMessage);
            }
        } else {
            if (start + limit >= totalMessageCount) {
                messages.add(startMessage);
            }
        }


        PaginatedListWrapper<CommunicatorMessageModel> result = new PaginatedListWrapper<>();

        List<CommunicatorMessageModel> resultList = new ArrayList<>();

        for (CommunicatorMessage entity : messages) {
            CommunicatorMessageModel model = getMessageModel(entity, langId);
            model.setUser(entity.getUser().getLogin().toLowerCase());
            model.setAttachments(getMessageAttachments(model.getId(), CommunicatorAttachmentType.MESSAGE));
            resultList.add(model);
        }

        result.setList(resultList);
        result.setTotalResults(totalMessageCount);

        return result;
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_REVIEW)
    public CommunicatorAttachmentModel getAttachmentWithContent(long attachmentId, CommunicatorAttachmentType attachmentType) throws FinATypeException {
        validateUserAccessOnAttachment(attachmentId, attachmentType);

        CommunicatorAttachment attachment = communicatorLocal.getAttachmentById(attachmentId);
        if (attachment == null) {
            throw new FinATypeException("Attachment not found");
        }
        return CommunicatorModelHelper.getAttachmentModel(attachment, true);
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_REVIEW)
    public List<CommunicatorAttachmentModel> getAttachmentsWithContent(List<Long> attachmentIds, CommunicatorAttachmentType attachmentType) throws FinATypeException {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        }
        List<CommunicatorAttachmentModel> result = new ArrayList<>();
        for (Long attachmentId : attachmentIds) {
            result.add(getAttachmentWithContent(attachmentId, attachmentType));
        }
        return result;
    }


    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_REVIEW)
    public Collection<CommunicatorAttachmentModel> getMessageAttachments(long messageId, CommunicatorAttachmentType type) {
        //TODO check user has access to messageId
        return communicatorLocal.getAttachmentsByMessageId(messageId, type, false)
                .stream().map(item -> CommunicatorModelHelper.getAttachmentModel(item, false)).collect(Collectors.toList());
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_REVIEW)
    public void markMessageAsRead(long rootMessageId) {
        communicatorLocal.markMessageAsRead(rootMessageId);
    }


    @RolesAllowed({PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_AMEND, PermissionIdNames.FAQ_QUESTION_ASK})
    public CommunicatorMessageModel saveMessageFromClient(CommunicatorMessageModel messageModel)
            throws WrongFileTypeException, FileSignerException {
        long langId = ThreadLocalHolder.getLanguage().getId();

        if (messageModel != null) {
            CommunicatorMessage message = CommunicatorModelHelper.getMessageEntity(messageModel);
            if (message.getTitle() == null) message.setTitle("");
            List<CommunicatorAttachment> attachmentList = new ArrayList<>();

            if (messageModel.getAttachments() != null) {
                messageModel.getAttachments()
                        .forEach(item -> attachmentList.add(CommunicatorModelHelper.getAttachmentEntity(item, CommunicatorAttachmentType.MESSAGE)));
            }

            return getMessageModel(communicatorLocal.saveMessageFromDcs(messageModel.getReplyToId(), message, attachmentList), langId);
        }

        return null;
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_AMEND)
    public CommunicatorMessageModel saveCommunicatorMessage(CommunicatorMessageModel model, long replyToId, long replyToUserId) throws Exception {

        HttpServletRequest request = ThreadLocalHolder.getThreadLocalRequest();
        LanguageSampleModel language = ThreadLocalHolder.getLanguage();
        boolean hasAcceptPermission = request.isUserInRole(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_ACCEPT);

        checkAcceptRejectPermissions(model, language, request);

        if (model.getUserIds().isEmpty() && model.getStatus() != CommunicatorMessageStatus.OUTBOX && model.getId() > 0) {
            model.setUserIds(communicatorLocal.loadMessageUsers(model.getId(), null, -1, -1, null)
                    .stream().map(m -> m.getMessageUserId().getUser()).collect(Collectors.toList()));
        }

        if (model.getReplyToId() == 0 && model.getStatus() == CommunicatorMessageStatus.OUTBOX && model.getUserIds().isEmpty()) {
            throw new FinATypeException("Recipients is empty!");
        }

        CommunicatorMessage message = new CommunicatorMessage();
        message.setId(model.getId());
        message.setReplyToId(model.getId() != replyToId ? replyToId : 0);
        message.setReplyToUserId(replyToUserId);
        message.setTitle(model.getTitle());
        message.setContent(model.getContent());
        message.setCreationDate(model.getId() == 0 ? new Date() : model.getCreationDate());
        message.setSendDate(new Date());
        message.setLastConversationMessageDate(message.getSendDate());
        if (model.getStatus() == null) {
            if (hasAcceptPermission) {
                message.setStatus(CommunicatorMessageStatus.CREATED);
            } else {
                message.setStatus(CommunicatorMessageStatus.PENDING);
            }
        } else {
            message.setStatus(model.getStatus());
        }
        message.setSign(model.getSign());

        message.setUser(userLocal.getCurrentUser());

        List<CommunicatorAttachment> attachments = new ArrayList<>();
        if (model.getId() > 0) {
            communicatorLocal.updateAttachments(model.getAttachments(), model.getId());
        }

        if (model.getAttachments() != null) {
            Collection<CommunicatorAttachmentModel> communicatorMessageModels = model.getAttachments();
            for (CommunicatorAttachmentModel attachmentModel : communicatorMessageModels) {

                CommunicatorAttachment attachment = new CommunicatorAttachment();
                attachment.setId(attachmentModel.getId());
                attachment.setMessageId(message.getId());

                attachment.setFileName(attachmentModel.getName());
                attachment.setContent(attachmentModel.getContent());
                attachment.setType(CommunicatorAttachmentType.MESSAGE);
                attachment.setSign(model.getSign());

                attachments.add(attachment);

            }
        }

        try {
            message = communicatorLocal.saveCommunicatorMessage(message, model, attachments, replyToId, replyToUserId);
        } catch (Throwable t) {
            String exceptionMessage = ((ExceptionUtils.getRootCause(t) instanceof BatchUpdateException) ? "Content/title has too many characters." : t.getMessage());
            throw new FinATypeException(exceptionMessage);
        }

        model = CommunicatorModelHelper.getMessageModel(message, language.getId());
        model.setAttachments(attachments.stream().map(a -> CommunicatorModelHelper.getAttachmentModel(a, false)).collect(Collectors.toList()));

        List<String> destinationUsers = new ArrayList<>();
        if (message.getStatus().equals(CommunicatorMessageStatus.OUTBOX)) {
            if (message.getReplyToId() == 0) {
                message.getUsers().forEach(mu -> destinationUsers.add(userLocal.findUserbyId(mu.getMessageUserId().getUser()).getLogin()));
            } else {
                destinationUsers.add(userLocal.findUserbyId(message.getReplyToUserId()).getLogin());
            }
        }

        boolean isNewMessage = message.getReplyToId() == 0;
        updateDcsCommunicatorMessageClient(destinationUsers, isNewMessage ? message.getId() : message.getReplyToId(), isNewMessage);
        return model;
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_AMEND)
    public CommunicatorMessageModel saveConversationMessage(MessageUserModel messageUserModel, CommunicatorMessageModel model) throws Exception {
        HttpServletRequest threadLocalRequest = ThreadLocalHolder.getThreadLocalRequest();
        if (!threadLocalRequest.isUserInRole(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_ACCEPT)) {

            CommunicatorMessage message = new CommunicatorMessage();
            message.setId(model.getId());
            message.setReplyToId(model.getReplyToId());
            message.setReplyToUserId(messageUserModel.getId());
            message.setTitle(model.getTitle());
            message.setContent(model.getContent());
            message.setCreationDate(new Date());
            message.setSendDate(new Date());
            message.setLastConversationMessageDate(message.getSendDate());
            message.setStatus(CommunicatorMessageStatus.PENDING);
            message.setSign(model.getSign());

            if (model.getUser() != null) {
                message.setUser(userLocal.findUserbyLogin(model.getUser()));
            }

            try {
                message = communicatorLocal.saveMessage(message, messageUserModel.getMessageId(), messageUserModel.getId());
                List<MessageUser> userList = new ArrayList<>();
                for (long id : model.getUserIds()) {
                    MessageUserId messageUserId = new MessageUserId();

                    messageUserId.setUser(id);
                    messageUserId.setMessage(message.getId());

                    MessageUser messageUser = new MessageUser();
                    messageUser.setMessageUserId(messageUserId);
                    messageUser.setStatus(model.getStatus() == null || model.getStatus() == CommunicatorMessageStatus.CREATED ? CommunicatorReadStatus.PENDING : CommunicatorReadStatus.SENT);

                    userList.add(messageUser);
                }

                message.setUsers(userList);

                if (model.getAttachments() != null) {

                    for (CommunicatorAttachmentModel attachmentModel : model.getAttachments()) {
                        CommunicatorAttachment attachment = new CommunicatorAttachment();
                        attachment.setMessageId(message.getId());

                        attachment.setFileName(attachmentModel.getName());
                        attachment.setContent(attachmentModel.getContent());
                        attachment.setType(CommunicatorAttachmentType.MESSAGE);
                        attachment.setSign(model.getSign());

                        communicatorLocal.saveAttachment(attachment);
                        attachmentModel.setId(attachmentModel.getId());

                    }
                }

                Collection<CommunicatorAttachmentModel> atachemnts = model.getAttachments();
                model = CommunicatorModelHelper.getMessageModel(communicatorLocal.saveCommunicatorMessage(message, model, null, model.getReplyToId(), model.getReplyToUserId()), ThreadLocalHolder.getLanguage().getId());
                if (atachemnts != null) {
                    atachemnts.forEach(a -> a.setContent(null));
                    model.setAttachments(atachemnts);
                    model.setHasAttachments(!atachemnts.isEmpty());
                }


                return model;

            } catch (Throwable t) {
                String exceptionMessage = ((ExceptionUtils.getRootCause(t) instanceof BatchUpdateException) ? "Content has too many characters." : t.getMessage());
                throw new FinATypeException(exceptionMessage);
            }
        } else {
            model.setStatus(CommunicatorMessageStatus.OUTBOX);
            return saveCommunicatorMessage(model, messageUserModel.getMessageId(), messageUserModel.getId());
        }
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_AMEND)
    public void sendSavedMessage(long messageId) throws FinATypeException {
        if (communicatorLocal.getMessageById(messageId) == null) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        }
        if (!messageHasRecipients(messageId)) {
            throw new FinATypeException(FinATypeException.Type.NO_RECIPIENT);
        }
        communicatorLocal.sendSavedMessage(messageId);
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_REVIEW)
    public long getCurrentUserNewMessagesCount() {
        return communicatorLocal.countRecipientNewMessages();
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_REVIEW)
    public PaginatedListWrapper<CommunicatorMessageModel> loadMessages(Map<MessageFilter, String> filterMap, int offset, int limit) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        try (StatisticsLogger logger = new StatisticsLogger("COMMUNICATOR MESSAGE LOAD")) {

            logger.logMessage("start");
            logger.logStage("load current user");
            User current = userLocal.getCurrentUser();
            logger.logStage("load Messages");
            List<CommunicatorMessage> messages = communicatorLocal.loadCommunicatorMessages(filterMap, offset, limit);
            List<CommunicatorMessageModel> result = new ArrayList<>();
            logger.logStage("count message statistics");

            for (CommunicatorMessage cm : messages) {
                CommunicatorMessageModel model = getMessageModel(cm, langId);
                result.add(communicatorLocal.countNewAndCheckPending(cm, current, model));
            }
            logger.logStage("count messages");
            long messageCount = communicatorLocal.countAllMessages(filterMap);

            return new PaginatedListWrapper<>(result, limit, messageCount);
        }


    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_REVIEW)
    public List<MessageUserModel> loadMessageUsers(long messageId, Map<MessageFilter, String> filterMap, int offset, int limit, String sortField, String sortDir) {

        List<MessageUserModel> modelList = new ArrayList<>();
        User current = userLocal.getCurrentUser();
        MessageUserModel model;
        long langId = ThreadLocalHolder.getLanguage().getId();
        CommunicatorMessage rootMessage = communicatorLocal.getMessageById(messageId);

        SortField sortFieldModel = SortUtil.constructSortField(sortField, sortDir);

        List<MessageUser> messageUsersList = communicatorLocal.loadMessageUsers(messageId, filterMap, offset, limit, sortFieldModel);

        for (MessageUser messageUser : messageUsersList) {
            model = new MessageUserModel();
            User user = userLocal.findUserbyId(messageUser.getMessageUserId().getUser());
            model.setId(user.getId());
            model.setName(user.getDescription().getDescription(langId));
            model.setLogin(user.getLogin());
            model.setStatus(messageUser.getStatus());
            model.setMessageId(messageId);
            boolean isReadByUser = communicatorLocal.checkUserHasReadMessage(messageUser, current);
            model.setNew(isReadByUser);
            model.setPendingMessageCount(communicatorLocal.countPendingMessageUsers(messageUser));
            model.setNotReliedMessage(filterMap.containsKey(MessageFilter.NOREPLIES));
            CommunicatorMessage lastConversationMessage = communicatorLocal.loadLastConversationMessage(messageId, messageUser.getMessageUserId().getUser());
            model.setLastConversationMessage(lastConversationMessage != null ? lastConversationMessage.getContent() : rootMessage.getContent());
            model.setReadDate(messageUser.getReadDate());
            modelList.add(model);
        }


        return modelList;
    }


    public long countMessages() {
        return communicatorLocal.countNewMessages();
    }


    public void acceptRejectMessage(MessageUserModel messageUserModel, CommunicatorMessageModel model,
                                    boolean accept) throws FinATypeException {
        checkAcceptRejectPermissions(model, null, null);
        communicatorLocal.acceptRejectMessage(messageUserModel.getId(), messageUserModel.getMessageId(), model.getId(), accept, model.getRejectionNote());
        updateCommunicatorMessageClient();
    }

    public void acceptRejectRootMessage(CommunicatorMessageModel model, boolean accept) throws FinATypeException {
        if (accept && !messagesHaveRecipient(Collections.singletonList(model.getId()))) {
            throw new FinATypeException(FinATypeException.Type.NO_RECIPIENT);
        }
        checkAcceptRejectPermissions(model, null, null);
        communicatorLocal.acceptRejectRootMessage(model.getId(), accept);
        updateCommunicatorMessageClient();
    }


    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_ACCEPT)
    public void acceptRejectRootMessages(List<CommunicatorMessageModel> models, boolean accept) throws FinATypeException {
        if (accept && !messagesHaveRecipient(models.stream().map(CommunicatorMessageModel::getId).collect(Collectors.toList()))) {
            throw new FinATypeException(FinATypeException.Type.NO_RECIPIENT);
        }
        for (CommunicatorMessageModel model : models) {
            communicatorLocal.acceptRejectRootMessage(model.getId(), accept);
        }
        updateCommunicatorMessageClient();
    }

    public void acceptRejectRootMessagesIds(List<Long> ids, boolean accept) throws FinATypeException {
        if (accept && !messagesHaveRecipient(ids)) {
            throw new FinATypeException(FinATypeException.Type.NO_RECIPIENT);
        }
        for (Long id : ids) {
            communicatorLocal.acceptRejectRootMessage(id, accept);
        }
        updateCommunicatorMessageClient();
    }

    public void markAllMessagesAsRead() {
        communicatorLocal.markAllMessagesAsRead();
    }

    public void updateMessageUserStatus(MessageUserModel userModel, CommunicatorReadStatus status) {
        communicatorLocal.updateMessageUserStatus(userModel.getId(), userModel.getMessageId(), status);
    }

    public void markMessageThreads(long messageId) {
        communicatorLocal.markAllMessageThreadsAsRead(messageId);
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_BOOKMARKS_AMEND)
    public CommunicatorMessageModel markMessage(long messageId, CommunicatorMessageMarkType markType) {
        CommunicatorMessage message = communicatorLocal.markMessage(messageId, markType);
        long langId = ThreadLocalHolder.getLanguage().getId();
        return CommunicatorModelHelper.getMessageModel(message, langId);
    }

    private void updateCommunicatorMessageClient() {
        CommunicatorMessageEvent event = new CommunicatorMessageEvent(userLocal.getCurrentUserLogin());
        this.communicatorMessageEvent.fire(event);
    }

    private boolean messagesHaveRecipient(List<Long> messageIds) {
        for (Long messageId : messageIds) {
            if (communicatorLocal.loadMessageUsers(messageId, null, -1, -1, null).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void updateDcsCommunicatorMessageClient(List<String> destinationUsers, long rootMessageId, boolean isNew) {
        CommunicatorMessageEvent event = new CommunicatorMessageEvent(userLocal.getCurrentUserLogin(), destinationUsers, rootMessageId, isNew);
        this.communicatorMessageEvent.fire(event);
    }

    public Map<CommunicatorDashletModel.MessagesStatistics, Object> loadMessageStatistics() {
        return communicatorLocal.loadMessageStatistics();
    }

    public long countMessageUsers(long messageId, Map<MessageFilter, String> filterMap) {
        return communicatorLocal.countMessageUsers(messageId, filterMap);
    }

    public void deleteMessage(long messageId) {
        communicatorLocal.deleteMessage(messageId);
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_DELETE)
    public void deleteMessages(List<Long> messageIds) {
        communicatorLocal.deleteMessages(messageIds);
    }

    public List<CommunicatorAttachment> getAttachmentsByMessageId(long messageId, CommunicatorAttachmentType type, boolean withContent) {
        return communicatorLocal.getAttachmentsByMessageId(messageId, type, withContent);
    }

    public List<Long> loadRootMessageRecipientUserIds(long rootMessageId) {
        return communicatorLocal.loadRootMessageRecipientUserIds(rootMessageId);
    }

    public List<UserModelSimple> loadRootMessageRecipientUsers(long rootMessageId) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return communicatorLocal.loadRootMessageRecipients(rootMessageId).stream().map(u -> new UserModelSimple(u.getId(), u.getLogin(), u.getDescription().getDescription(langId))).collect(Collectors.toList());
    }

    private void validateUserAccessOnAttachment(long attachmentId, CommunicatorAttachmentType attachmentType) throws FinATypeException {
        User user = userLocal.getCurrentUser();
        if (user.getPermissions().contains(new Permission(PermissionIdNames.FINA_WEB_EXTERNAL_USER))) {
            communicatorLocal.validateUserAccessOnAttachment(attachmentId, user.getId(), attachmentType);
        }
    }

    private void checkAcceptRejectPermissions(CommunicatorMessageModel model, LanguageSampleModel langModel, HttpServletRequest servletRequest) throws FinATypeException {

        LanguageSampleModel language = Optional.ofNullable(langModel).orElse(ThreadLocalHolder.getLanguage());
        HttpServletRequest request = Optional.ofNullable(servletRequest).orElse(ThreadLocalHolder.getThreadLocalRequest());

        boolean hasAcceptPermission = request.isUserInRole(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_ACCEPT);

        if (model != null && model.getStatus() == CommunicatorMessageStatus.OUTBOX
                && !hasAcceptPermission
                && !(request.isUserInRole(PermissionIdNames.FINA_COMMUNICATOR_MESSAGES_ACCEPT_WITHOUT_ATTACHMENT)
                && model.getAttachments().isEmpty())) {
            throw new FinATypeException(MessagesUtil.getString("net.fina.web.communicator.messages.sendPermissionError", language.getCode()));
        }
    }

    private boolean messageHasRecipients(long messageId) {
        return communicatorLocal.countMessageUsers(messageId, new HashMap<>()) > 0;
    }

}
