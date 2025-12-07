package net.fina.server.communicator.impl;

import jakarta.ejb.*;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import net.fina.common.client.constants.*;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.FileTypeCheckUtil;
import net.fina.common.shared.FileSignerException;
import net.fina.common.shared.SortField;
import net.fina.common.shared.WrongFileTypeException;
import net.fina.common.shared.comunicator.*;
import net.fina.messages.MessagesUtil;
import net.fina.server.communicator.api.CommunicatorLocal;
import net.fina.server.communicator.entity.*;
import net.fina.server.communicator.event.message.CommunicatorMessageEvent;
import net.fina.server.communicator.event.message.CommunicatorMessageReadEvent;
import net.fina.server.communicator.event.type.CommunicatorMessageEventType;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.legislative.entity.LegislativeDocument;
import net.fina.server.legislative.event.LegislativeDocumentNotificationEvent;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.crypto.signature.FileSigner;
import net.fina.server.security.crypto.signature.FileSignerFactorySession;
import net.fina.server.security.entity.User;
import net.fina.server.security.entity.User_;
import net.fina.server.util.SortUtil;
import org.jboss.logging.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
@Local(CommunicatorLocal.class)
@Interceptors(RecordingAuditor.class)
public class CommunicatorSession implements CommunicatorLocal {

    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @EJB
    private UserLocal userLocal;
    @EJB
    private FiLocal fiLocal;
    @EJB
    private FileSignerFactorySession fileSignerFactorySession;

    @Inject
    private Event<CommunicatorMessageEvent> communicatorMessageEvent;

    @Inject
    private Event<CommunicatorMessageReadEvent> communicatorMessageReadEventEvent;

    @Override
    public List<CommunicatorNotification> loadCommunicatorNotifications(Map<MessageFilter, String> filterMap, long offset, long limit) {

        reverseAuthorIdsIfExists(filterMap);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CommunicatorNotification> cq = cb.createQuery(CommunicatorNotification.class);
        Root<CommunicatorNotification> root = cq.from(CommunicatorNotification.class);

        cq.select(root);

        List<Predicate> predicates = getNotificationFilterPredicate(cb, cq, root, filterMap);
        predicates.add(cb.equal(root.get(CommunicatorNotification_.deleted), false));

        cq.where(predicates.toArray(new Predicate[0]));

        Expression<Object> isPending = cb.selectCase()
                .when(cb.equal(root.get(CommunicatorNotification_.status), CommunicatorNotificationStatus.PENDING), cb.literal(1))
                .otherwise(cb.literal(0));


        cq.orderBy(
                cb.desc(isPending),
                cb.desc(root.get(CommunicatorNotification_.creationDate))
        );

        TypedQuery<CommunicatorNotification> query = em.createQuery(cq);
        if (offset >= 0 && limit >= 0) {
            query.setFirstResult((int) offset).setMaxResults((int) limit);
        }

        return query.getResultList();
    }


    @Override
    public CommunicatorNotification saveNotification(CommunicatorNotification notification) throws FinATypeException {

        if (notification.getUser() == null) {
            notification.setUser(userLocal.getCurrentUser());
        }
        //check if notification is published and read
        validateNotificationReadStatus(notification.getId());

        if (notification.getId() > 0) {
            CommunicatorNotification changedNotification = em.find(CommunicatorNotification.class, notification.getId());

            if (notification.getNotificationUsers() == null) {
                notification.setNotificationUsers(changedNotification.getNotificationUsers());
            }

            if (notification.getCreationDate() == null) {
                notification.setCreationDate(new Date());
            }

            for (CommunicatorNotificationUser communicatorNotificationUser : changedNotification.getNotificationUsers()) {
                if (!notification.getNotificationUsers().contains(communicatorNotificationUser)) {
                    em.remove(communicatorNotificationUser);
                } else {
                    if (communicatorNotificationUser.getStatus() != CommunicatorReadStatus.READ) {
                        communicatorNotificationUser.setStatus(notification.getStatus() != null && notification.getStatus() == CommunicatorNotificationStatus.PUBLISHED ? CommunicatorReadStatus.SENT : CommunicatorReadStatus.PENDING);
                        em.merge(communicatorNotificationUser);
                    }
                }
            }

            for (CommunicatorNotificationUser communicatorNotificationUser : notification.getNotificationUsers()) {
                if (!changedNotification.getNotificationUsers().contains(communicatorNotificationUser)) {
                    em.persist(communicatorNotificationUser);
                }
            }

            notification.setUser(changedNotification.getUser());
            notification = em.merge(notification);
        } else {
            em.persist(notification);
        }

        return notification;
    }


    @Override
    public CommunicatorNotification saveNotificationWithAttachments(CommunicatorNotification notification, List<CommunicatorAttachment> attachments, List<Long> userIds) throws WrongFileTypeException, FileSignerException, FinATypeException {
        notification = saveNotification(notification);

        if (attachments != null) {
            for (CommunicatorAttachment attachment : attachments) {
                attachment.setMessageId(notification.getId());
                saveAttachment(attachment);
            }
            notification.setHasAttachments(!attachments.isEmpty());
        }
        return notification;
    }


    @Override
    public void deleteNotification(long notificationId) {
        CommunicatorNotification notification = em.find(CommunicatorNotification.class, notificationId);
        notification.setDeleted(true);
    }

    @Override
    public List<CommunicatorNotificationUser> loadCommunicationUsers(long notificationId) {
        return em.createQuery("SELECT nu FROM IN_COMMUNICATOR_NOTIFICATION_USERS nu WHERE nu.userNotificationId.notification.id=:notificationId", CommunicatorNotificationUser.class).setParameter("notificationId", notificationId).getResultList();
    }

    @Override
    public List<CommunicatorNotificationUser> loadCommunicationUsers(long notificationId, int offset, int limit, Map<MessageFilter, String> filterMap, SortField sortField) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CommunicatorNotificationUser> cq = cb.createQuery(CommunicatorNotificationUser.class);

        Root<CommunicatorNotificationUser> root = cq.from(CommunicatorNotificationUser.class);

        cq.select(root);

        List<Predicate> predicates = getNotificationUserFilterPredicate(cb, cq, root, filterMap);
        predicates.add(cb.equal(root.get(CommunicatorNotificationUser_.userNotificationId).get(UserNotificationId_.notification).get(CommunicatorNotification_.id), notificationId));

        cq.where(predicates.toArray(new Predicate[0]));

        if (sortField != null) {
            if (sortField.getProperty().equalsIgnoreCase("login")) {
                Join<UserNotificationId, User> userJoin = root.join(CommunicatorNotificationUser_.userNotificationId).join(UserNotificationId_.user);
                cq.orderBy(sortField.isAsc() ? cb.asc(userJoin.get(User_.login)) : cb.desc(userJoin.get(User_.login)));
            } else {
                if (SortUtil.isPropertyExistOnEntity(root, sortField.getProperty())) {
                    cq.orderBy(sortField.isAsc() ? cb.asc(root.get(sortField.getProperty())) : cb.desc(root.get(sortField.getProperty())));
                }
            }
        }

        TypedQuery<CommunicatorNotificationUser> query = em.createQuery(cq);

        if (offset >= 0 && limit >= 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }
        return query.getResultList();

    }

    @Override
    public void publishCommunicatorNotification(long notificationId) {
        CommunicatorNotification notification = em.find(CommunicatorNotification.class, notificationId);
        if (notification != null) {
            notification.setStatus(CommunicatorNotificationStatus.PUBLISHED);
            notification.setPublishDate(new Date());

            for (CommunicatorNotificationUser communicatorNotificationUser : notification.getNotificationUsers()) {
                communicatorNotificationUser.setStatus(CommunicatorReadStatus.SENT);
                em.merge(communicatorNotificationUser);
            }
        }
    }

    @Override
    public List<CommunicatorNotification> loadUserNotReadNotifications() {
        return load(false, -1, -1, null);
    }

    @Override
    public List<String> loadAllNotReadNotificationsUsers() {
        TypedQuery<CommunicatorNotification> query = em.createQuery("select c from IN_COMMUNICATOR_NOTIFICATIONS c LEFT OUTER JOIN c.notificationUsers nu where c.deleted=false and c.status=:status and nu.status=:notificationStatus  order by c.publishDate desc", CommunicatorNotification.class).setParameter("status", CommunicatorNotificationStatus.PUBLISHED).setParameter("notificationStatus", CommunicatorReadStatus.SENT);

        List<CommunicatorNotification> notifications = query.getResultList();

        Set<String> result = new HashSet<>();
        for (CommunicatorNotification notification : notifications) {
            for (CommunicatorNotificationUser communicatorNotificationUser : notification.getNotificationUsers()) {
                result.add(communicatorNotificationUser.getUserNotificationId().getUser().getLogin());
            }
        }

        return new ArrayList<>(result);
    }

    @Override
    public List<CommunicatorNotification> loadAll(int start, int limit, SortField sortField) {
        return load(true, start, limit, sortField);
    }

    @Override
    public void submitReadNotification(long id) {
        UserNotificationId emId = new UserNotificationId();
        emId.setNotification(em.find(CommunicatorNotification.class, id));
        emId.setUser(userLocal.getCurrentUser());
        CommunicatorNotificationUser nu = em.find(CommunicatorNotificationUser.class, emId);
        nu.setStatus(CommunicatorReadStatus.READ);
        nu.setReadDate(new Date());
    }

    @Override
    public List<CommunicatorMessage> loadCommunicatorMessages(Map<MessageFilter, String> filterMap, long offset, long limit) {
        reverseAuthorIdsIfExists(filterMap);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CommunicatorMessage> cq = cb.createQuery(CommunicatorMessage.class);
        Root<CommunicatorMessage> root = cq.from(CommunicatorMessage.class);

        Expression<Object> statusOrder = cb.selectCase()
                .when(cb.equal(root.get(CommunicatorMessage_.status), CommunicatorMessageStatus.PENDING), 1)
                .otherwise(2);

        cq.multiselect(
                root.get(CommunicatorMessage_.id),
                root.get(CommunicatorMessage_.replyToId),
                root.get(CommunicatorMessage_.replyToUserId),
                root.get(CommunicatorMessage_.title),
                root.get(CommunicatorMessage_.content),
                root.get(CommunicatorMessage_.creationDate),
                root.get(CommunicatorMessage_.sendDate),
                root.get(CommunicatorMessage_.status),
                root.get(CommunicatorMessage_.sign),
                root.get(CommunicatorMessage_.user),
                root.get(CommunicatorMessage_.rejectionNote),
                root.get(CommunicatorMessage_.lastConversationMessageDate),
                root.get(CommunicatorMessage_.hasAttachments),
                root.get(CommunicatorMessage_.recipientReadDate),
                root.get(CommunicatorMessage_.markType),
                root.get(CommunicatorMessage_.recipientSize),
                statusOrder.alias("statusOrder")
        );

        List<Predicate> predicates = getFilterPredicate(cb, cq, root, filterMap);

        if (!filterMap.containsKey(MessageFilter.HAS_WORDS)) {
            predicates.add(cb.equal(root.get(CommunicatorMessage_.replyToId), 0));
        }
        predicates.add(cb.equal(root.get(CommunicatorMessage_.deleted), false));

        cq.where(predicates.toArray(new Predicate[0]));

        cq.orderBy(
                cb.asc(statusOrder),
                cb.desc(root.get(CommunicatorMessage_.lastConversationMessageDate))
        );

        TypedQuery<CommunicatorMessage> query = em.createQuery(cq);
        if (offset >= 0 && limit >= 0) {
            query.setFirstResult((int) offset).setMaxResults((int) limit);
        }

        return query.getResultList();
    }

    @Override
    public List<MessageUser> loadMessageUsers(long messageId, Map<MessageFilter, String> filterMap, long offset, long limit, SortField sortField) {
        reverseAuthorIdsIfExists(filterMap);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MessageUser> cq = cb.createQuery(MessageUser.class);
        Root<MessageUser> root = cq.from(MessageUser.class);

        cq.select(root);

        List<Predicate> predicates = getMessageUserFilterPredicate(cb, cq, root, filterMap, messageId);
        predicates.add(cb.equal(root.get(MessageUser_.messageUserId).get(MessageUserId_.message), messageId));

        Subquery<Long> statusSubquery = cq.subquery(Long.class);
        Root<UserMessageStatus> statusRoot = statusSubquery.from(UserMessageStatus.class);
        statusSubquery.select(statusRoot.get(UserMessageStatus_.messageId))
                .where(
                        cb.and(
                                cb.equal(statusRoot.get(UserMessageStatus_.messageId), root.get(MessageUser_.messageUserId).get(MessageUserId_.message)),
                                cb.equal(statusRoot.get(UserMessageStatus_.status), 2)
                        )
                );

        Expression<Object> statusOrder = cb.selectCase()
                .when(cb.exists(statusSubquery), 1)
                .otherwise(2);

        if (sortField != null && sortField.getProperty().equalsIgnoreCase("login")) {
            Subquery<String> subquery = cq.subquery(String.class);
            Root<User> userRoot = subquery.from(User.class);
            subquery.select(userRoot.get(User_.login));
            subquery.where(cb.equal(userRoot.get(User_.id), root.get(MessageUser_.messageUserId).get(MessageUserId_.user)));

            cq.orderBy(sortField.isAsc() ? cb.asc(subquery) : cb.desc(subquery));

        } else if (sortField != null) {
            if (SortUtil.isPropertyExistOnEntity(root, sortField.getProperty())) {
                cq.orderBy(sortField.isAsc() ? cb.asc(root.get(sortField.getProperty())) : cb.desc(root.get(sortField.getProperty())));
            }
        } else {
            cq.orderBy(cb.asc(statusOrder));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<MessageUser> query = em.createQuery(cq);
        if (offset >= 0 && limit >= 0) {
            query.setFirstResult((int) offset).setMaxResults((int) limit);
        }

        return query.getResultList();
    }


    @Override
    public List<CommunicatorMessage> loadConversation(long messageId, long userId, boolean pending, boolean bookmarked) {
        List<CommunicatorMessage> conversation = loadConversation(messageId, userId, pending, bookmarked, -1, -1, null);
        conversation.add(0, getMessageById(messageId));
        return conversation;
    }

    @Override
    public List<CommunicatorMessage> loadConversation(long messageId, long userId, boolean pending, boolean bookmarked, int start, int limit, SortField sortField) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CommunicatorMessage> cq = cb.createQuery(CommunicatorMessage.class);
        Root<CommunicatorMessage> root = cq.from(CommunicatorMessage.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(CommunicatorMessage_.replyToId), messageId));
        predicates.add(cb.equal(root.get(CommunicatorMessage_.replyToUserId), userId));

        cq.where(predicates.toArray(new Predicate[0]));
        if (sortField != null) {
            Path<Object> path = root.get(sortField.getProperty());
            cq.orderBy(sortField.isAsc() ? cb.asc(path) : cb.desc(path));
        } else {
            cq.orderBy(cb.asc(root.get(CommunicatorMessage_.sendDate)));
        }

        TypedQuery<CommunicatorMessage> query = em.createQuery(cq);
        if (start >= 0 && limit > 0) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        List<CommunicatorMessage> messageList = query.getResultList();

        if (pending) {
            return messageList.stream().filter(m -> m.getStatus() == CommunicatorMessageStatus.OUTBOX || m.getStatus() == CommunicatorMessageStatus.INBOX || m.getStatus() == CommunicatorMessageStatus.ACCEPTED).collect(Collectors.toList());
        }

        return messageList;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CommunicatorMessage saveCommunicatorMessage(CommunicatorMessage message, CommunicatorMessageModel model, List<CommunicatorAttachment> attachments, long replyToid, long replyToUserId) throws FinATypeException, WrongFileTypeException, FileSignerException {
        List<MessageUser> userList = new ArrayList<>();
        //check if message is already read
        validateMessageReadStatus(message.getId());

        for (long id : model.getUserIds()) {
            MessageUserId messageUserId = new MessageUserId();

            messageUserId.setUser(id);
            messageUserId.setMessage(message.getId());

            MessageUser messageUser = new MessageUser();
            messageUser.setMessageUserId(messageUserId);
            messageUser.setStatus(model.getStatus() == null || model.getStatus() == CommunicatorMessageStatus.CREATED ? CommunicatorReadStatus.PENDING : CommunicatorReadStatus.SENT);
            messageUser.setLastConversationMessageDate(new Date());
            userList.add(messageUser);
        }

        message.setRecipientSize(userList.size());

        message = replyToid == 0 ? saveMessage(message) : saveMessage(message, replyToid, replyToUserId);

        User current = userLocal.getCurrentUser();

        if (userList.isEmpty()) {
            if (replyToUserId > 0) {
                MessageUserId messageUserId = new MessageUserId();
                messageUserId.setMessage(message.getReplyToId());
                messageUserId.setUser(replyToUserId);

                MessageUser messageUser = em.find(MessageUser.class, messageUserId);
                messageUser.setLastConversationMessageDate(new Date());
                List statusIds = em.createQuery("select mus.id from IN_COMMUNICATOR_USERS_MESSAGE_STATUS mus where mus.messageId=:messageId and mus.messageUserId=:messageUserId ")
                        .setParameter("messageId", messageUser.getMessageUserId().getMessage())
                        .setParameter("messageUserId", messageUser.getMessageUserId().getUser())
                        .getResultList();

                UserMessageStatus lastMessageStatus = new UserMessageStatus();
                if (!statusIds.isEmpty()) {
                    lastMessageStatus = em.createQuery("select mus from IN_COMMUNICATOR_USERS_MESSAGE_STATUS mus where mus.id=:id", UserMessageStatus.class).setParameter("id", ((Number) statusIds.get(0)).longValue()).getSingleResult();
                }

                lastMessageStatus.setUser(current.getId());
                lastMessageStatus.setStatus(messageUser.getStatus());

                em.merge(lastMessageStatus);

                em.createNativeQuery("update IN_COMMUNICATOR_USERS_MESSAGE_STATUS set status=? where message_id=? and message_user_id=? and user_id<>?").setParameter(1, CommunicatorReadStatus.SENT.ordinal()).setParameter(2, replyToid).setParameter(3, replyToUserId).setParameter(4, current.getId()).executeUpdate();
                em.createNativeQuery("update IN_COMMUNICATOR_USERS_MESSAGE_STATUS set status=? where message_id=? and message_user_id=? and user_id=?").setParameter(1, CommunicatorReadStatus.READ.ordinal()).setParameter(2, replyToid).setParameter(3, replyToUserId).setParameter(4, current.getId()).executeUpdate();

                messageUser.getUserMessageStatus().add(lastMessageStatus);
                messageUser.setStatus(CommunicatorReadStatus.SENT);
                em.merge(messageUser);
            }
        } else {
            for (MessageUser messageUser : userList) {
                List<UserMessageStatus> userMessageStatuses = new ArrayList<>();
                List statusIds = em.createQuery("select mus.id from IN_COMMUNICATOR_USERS_MESSAGE_STATUS mus where mus.messageId=:messageId and mus.messageUserId=:messageUserId")
                        .setParameter("messageId", messageUser.getMessageUserId().getMessage())
                        .setParameter("messageUserId", messageUser.getMessageUserId().getUser())
                        .getResultList();

                UserMessageStatus ums = new UserMessageStatus();
                if (!statusIds.isEmpty()) {
                    ums = em.createQuery("select mus from IN_COMMUNICATOR_USERS_MESSAGE_STATUS mus where mus.id=:id", UserMessageStatus.class).setParameter("id", ((Number) statusIds.get(0)).longValue()).getSingleResult();
                }

                messageUser.getMessageUserId().setMessage(message.getId());
                messageUser.setLastConversationMessageDate(new Date());
                ums.setUser(current.getId());
                ums.setStatus(CommunicatorReadStatus.READ);
//                ums.setLastMessageId(message.getId());
                em.persist(ums);
                userMessageStatuses.add(ums);
                messageUser.setUserMessageStatus(userMessageStatuses);
                em.merge(messageUser);
            }
        }

        message.setUsers(userList);

        if (attachments != null) {
            for (CommunicatorAttachment attachment : attachments) {
                attachment.setMessageId(message.getId());
                saveAttachment(attachment);
            }

            message.setHasAttachments(!attachments.isEmpty());
            if (message.getReplyToId() != 0) {
                CommunicatorMessage rootMessage = em.find(CommunicatorMessage.class, message.getReplyToId());
                rootMessage.setHasAttachments(!attachments.isEmpty());
            }
        }

        return message;

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CommunicatorMessage saveMessage(CommunicatorMessage message) throws FinATypeException {

        if (message.getContent() == null || message.getContent().trim().equals("")) {
            throw new FinATypeException(MessagesUtil.getString("net.fina.communicator.contentEmptyMessage"));
        }

        if (message.getUser() == null) {
            message.setUser(userLocal.getCurrentUser());
        }

        if (message.getId() > 0) {
            em.createQuery("update IN_COMMUNICATOR_MESSAGES set title=:title, content=:content,status=:status,recipientSize=:size where id=:messageId")
                    .setParameter("messageId", message.getId())
                    .setParameter("title", message.getTitle())
                    .setParameter("content", message.getContent())
                    .setParameter("status", message.getStatus())
                    .setParameter("size", message.getRecipientSize())
                    .executeUpdate();
            em.createQuery("delete from IN_MESSAGE_USERS where id.message=:messageId").setParameter("messageId", message.getId());
            em.createQuery("delete from IN_COMMUNICATOR_USERS_MESSAGE_STATUS where  messageId=:messageId").setParameter("messageId", message.getId());
        } else {
            em.persist(message);
        }

        return message;
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public CommunicatorMessage saveMessage(CommunicatorMessage message, long rootMessageId, long replyToUserId) throws FinATypeException {
        if (replyToUserId != 0) {

            CommunicatorMessage root = em.find(CommunicatorMessage.class, rootMessageId);
            root.setLastConversationMessageDate(message.getLastConversationMessageDate());

            User current = userLocal.getCurrentUser();

            MessageUserId messageUserId = new MessageUserId();

            messageUserId.setMessage(rootMessageId);
            messageUserId.setUser(replyToUserId);

            MessageUser rootMessageUser = em.find(MessageUser.class, messageUserId);

            if (rootMessageUser != null) {
                rootMessageUser.setLastMessageUser(current);
            }
        }

        return saveMessage(message);
    }

    @Override
    public void deleteMessage(long messageId) {
        CommunicatorMessage message = em.find(CommunicatorMessage.class, messageId);
        message.setDeleted(true);
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<CommunicatorMessage> loadUserMessages(int offset, int limit, SortField sortField) {

        String path = (sortField != null ? "cu." + sortField.getProperty() + " " + sortField.getDirection() : " cu.lastConversationMessageDate desc");

        TypedQuery<CommunicatorMessage> query = em.createQuery("select new " + CommunicatorMessage.class.getName() + "(cm.id,cm.replyToId,cm.replyToUserId,cm.title,cm.content,cm.creationDate,cm.sendDate,cm.status,cm.sign,cm.rejectionNote,cm.user,cm.isRootBookmarked,cm.deleted,cm.hasAttachments,cm.recipientSize,cm.recipientReadDate,cu.lastConversationMessageDate) from IN_COMMUNICATOR_MESSAGES cm LEFT OUTER JOIN cm.users cu where  cm.status in :statuses and cm.replyToId=0 and cm.deleted=false and (cm.user.id=:currUser or cu.messageUserId.user=:currUser) order by " + path, CommunicatorMessage.class);
        query.setParameter("currUser", userLocal.getCurrentUserId()).setParameter("statuses", Arrays.asList(CommunicatorMessageStatus.OUTBOX, CommunicatorMessageStatus.INBOX));

        if (offset >= 0 && limit >= 0) {
            query.setFirstResult(offset).setMaxResults(limit);

        }

        return query.getResultList();
    }

    @Override
    public List<String> loadSentMessagesUsers() {
        TypedQuery<CommunicatorMessage> query = em.createQuery("select distinct cm from IN_COMMUNICATOR_MESSAGES cm LEFT OUTER JOIN cm.users cu where cm.status in :statuses and cm.replyToId=0 ", CommunicatorMessage.class).setParameter("statuses", Arrays.asList(CommunicatorMessageStatus.OUTBOX, CommunicatorMessageStatus.INBOX));

        Set<String> result = new HashSet<>();
        List<CommunicatorMessage> messages = query.getResultList();
        for (CommunicatorMessage message : messages) {
            for (MessageUser messageUser : message.getUsers()) {
                if (messageUser.getStatus() == CommunicatorReadStatus.SENT) {
                    result.add(userLocal.findUserbyId(messageUser.getMessageUserId().getUser()).getLogin());
                }
            }
        }

        return new ArrayList<>(result);
    }

    @Override
    public CommunicatorMessage saveMessageFromDcs(long rootMessageId, CommunicatorMessage message, List<CommunicatorAttachment> attachments) throws WrongFileTypeException, FileSignerException {
        User curUser = userLocal.getCurrentUser();

        if (rootMessageId == 0) {
            List<MessageUser> messageUsers = new ArrayList<>();

            MessageUser mu = new MessageUser();
            MessageUserId messageUserId = new MessageUserId();
            messageUserId.setUser(curUser.getId());

            messageUserId.setMessage(message.getId());

            mu.setMessageUserId(messageUserId);
            mu.setStatus(CommunicatorReadStatus.RECEIVED);
            List<UserMessageStatus> internalUsers = new ArrayList<>();

            mu.setUserMessageStatus(internalUsers);
            mu.setLastConversationMessageDate(new Date());
            messageUsers.add(mu);

            message.setUser(curUser);
            message.setStatus(CommunicatorMessageStatus.INBOX);
            message.setLastConversationMessageDate(new Date());
            mu.setLastMessageUser(curUser);

            em.persist(message);
            mu.getMessageUserId().setMessage(message.getId());
            em.persist(mu);

            message.setUsers(messageUsers);
            message.setRecipientSize(messageUsers.size());

            if (attachments != null) {
                for (CommunicatorAttachment attachment : attachments) {
                    attachment.setMessageId(message.getId());
                    try {
                        saveAttachment(attachment);
                    } catch (FinATypeException e) {
                        throw new FileSignerException(e);
                    }
                }
                message.setHasAttachments(!attachments.isEmpty());
                em.merge(message);
            }

        } else {
            CommunicatorMessage rootMessage = em.find(CommunicatorMessage.class, rootMessageId);
            MessageUser rootMessageUser = em.createQuery("select mu from IN_MESSAGE_USERS mu where mu.messageUserId.user=:userId and mu.messageUserId.message=:messageId", MessageUser.class).setParameter("userId", curUser.getId()).setParameter("messageId", rootMessageId).getSingleResult();
            rootMessageUser.setLastMessageUser(curUser);
            rootMessageUser.setLastConversationMessageDate(new Date());

            for (UserMessageStatus ums : rootMessageUser.getUserMessageStatus()) {
                ums.setStatus(CommunicatorReadStatus.RECEIVED);
                ums.setReadDate(null);
            }

            rootMessageUser.setStatus(CommunicatorReadStatus.RECEIVED);

            message.setUser(curUser);
            message.setReplyToId(rootMessageId);
            message.setReplyToUserId(curUser.getId());

            message.setUsers(new ArrayList<>());
            message.setStatus(CommunicatorMessageStatus.INBOX);

            rootMessage.setLastConversationMessageDate(new Date());

            em.persist(message);

            if (attachments != null) {
                for (CommunicatorAttachment attachment : attachments) {
                    attachment.setMessageId(message.getId());
                    try {
                        saveAttachment(attachment);
                    } catch (FinATypeException e) {
                        throw new FileSignerException(e);
                    }
                }
                message.setHasAttachments(!attachments.isEmpty());
                rootMessage.setHasAttachments(!attachments.isEmpty());
                em.merge(message);
            }

        }

        CommunicatorMessageEvent event = new CommunicatorMessageEvent(curUser.getLogin(), rootMessageId, CommunicatorMessageEventType.COMMUNICATOR_MESSAGE_RECEIVED);
        this.communicatorMessageEvent.fire(event);

        return message;
    }

    @Override
    public void markMessageAsRead(long rootMessageId) {
        MessageUserId muid = new MessageUserId();

        muid.setMessage(rootMessageId);
        muid.setUser(userLocal.getCurrentUserId());

        MessageUser messageUser = em.find(MessageUser.class, muid);

        em.createQuery("update IN_COMMUNICATOR_MESSAGES set recipientReadDate=:rDate where  recipientReadDate is null and replyToId=:rootMessageId and user.id<>:userId ")
                .setParameter("rootMessageId", rootMessageId)
                .setParameter("userId", muid.getUser())
                .setParameter("rDate", new Date())
                .executeUpdate();

        if (messageUser != null) {
            messageUser.setStatus(CommunicatorReadStatus.READ);
            if (messageUser.getReadDate() == null) {
                messageUser.setReadDate(new Date());
            }

            CommunicatorMessageReadEvent readEvent = new CommunicatorMessageReadEvent(rootMessageId, muid.getUser(), CommunicatorMessageEventType.MESSAGE_READ_STATUS_CHANGED);
            this.communicatorMessageReadEventEvent.fire(readEvent);

        }
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void saveAttachment(CommunicatorAttachment attachment) throws WrongFileTypeException, FinATypeException {
        if (attachment.getId() <= 0) {
            //Check file content
            FileTypeCheckUtil.checkDefault(attachment.getContent());
            attachment.setContentSize(attachment.getContent().length);

            //Sign File
            if (attachment.getSign() != null && attachment.getSign()) {
                try {
                    FileSigner fileSigner = fileSignerFactorySession.createFileSigner();
                    attachment.setContent(fileSigner.sign(attachment.getFileName(), attachment.getContent(), FileTypeCheckUtil.getFileType(attachment.getContent())));
                } catch (FileSignerException e) {
                    throw new FinATypeException(e.getMessage());
                }
            }

            em.persist(attachment);
        }
    }

    @Override
    public List<CommunicatorAttachment> getAttachmentsByMessageId(long messageId, CommunicatorAttachmentType type, boolean withContent) {
        if (withContent) {
            return em.createQuery("select a from CommunicatorAttachment a where a.messageId=:messageId AND a.type=:msgType", CommunicatorAttachment.class).setParameter("messageId", messageId).setParameter("msgType", type).getResultList();
        } else {
            return em.createQuery("select NEW net.fina.server.communicator.entity.CommunicatorAttachment(a.id,a.messageId,a.fileName,a.type,a.sign,a.contentSize) from CommunicatorAttachment a where a.messageId=:messageId AND a.type=:msgType", CommunicatorAttachment.class).setParameter("messageId", messageId).setParameter("msgType", type).getResultList();
        }
    }

    @Override
    public void validateUserAccessOnAttachment(long attachmentId, long userId, CommunicatorAttachmentType attachmentType) throws FinATypeException {
        if (attachmentId <= 0 || userId <= 0 || attachmentType == null) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        }
        switch (attachmentType) {
            case MESSAGE -> validateUserAccessOnMessage(attachmentId, userId);
            case NOTIFICATION -> validateUserAccessOnNotification(attachmentId, userId);
        }

    }


    @Override
    public void updateMessageUserStatus(long messageUserId, long messageId, long currentUserId, CommunicatorReadStatus status) {
        MessageUserId messageUserPk = new MessageUserId(messageId, messageUserId);

        MessageUser messageUser = em.find(MessageUser.class, messageUserPk);

        List<Long> statusIds = messageUser.getUserMessageStatus().stream()
                .filter(ums -> ums.getUser() == currentUserId)
                .map(UserMessageStatus::getId)
                .toList();
        if (!statusIds.isEmpty()) {
            em.createNativeQuery("update IN_COMMUNICATOR_USERS_MESSAGE_STATUS set status=?,READ_DATE=? where message_id=? and message_user_id=? and user_id=?")
                    .setParameter(1, CommunicatorReadStatus.READ.ordinal())
                    .setParameter(2, new Date())
                    .setParameter(3, messageId)
                    .setParameter(4, messageUserId)
                    .setParameter(5, currentUserId)
                    .executeUpdate();
        } else {
            UserMessageStatus ums = new UserMessageStatus();
            ums.setStatus(status);
            ums.setUser(currentUserId);
            ums.setReadDate(new Date());
            em.persist(ums);
            messageUser.getUserMessageStatus().add(ums);
            ums.setMessageId(messageId);
            ums.setMessageUserId(messageUserId);
            ums.setStatus(CommunicatorReadStatus.READ);
            ums.setReadDate(new Date());

        }
//
//        em.createQuery("UPDATE IN_MESSAGE_USERS mu set mu.status=:status WHERE mu.messageUserId.message=:messageId AND mu.messageUserId.user=:userId")
//                .setParameter("status", status)
//                .setParameter("messageId", messageId)
//                .setParameter("userId", messageUserId)
//                .executeUpdate();
    }

    @Override
    public void updateMessageUserStatus(long messageUserId, long messageId, CommunicatorReadStatus status) {
        updateMessageUserStatus(messageUserId, messageId, userLocal.getCurrentUserId(), status);
    }

    @Override
    public void acceptRejectMessage(long messageUserId, long messageUserMessageId, long messageId, boolean accept, String rejectionNote) {
        CommunicatorMessage message = em.find(CommunicatorMessage.class, messageId);
        if (message != null && message.getStatus().equals(CommunicatorMessageStatus.PENDING)) {
            message.setRejectionNote(rejectionNote);
            message.setAcceptor(userLocal.getCurrentUser());
            message.setStatus(accept ? CommunicatorMessageStatus.ACCEPTED : CommunicatorMessageStatus.REJECTED);
            updateMessageUserStatus(messageUserId, messageUserMessageId, accept ? CommunicatorReadStatus.SENT : CommunicatorReadStatus.REJECTED);
        }
    }

    @Override
    public void acceptRejectRootMessage(long messageId, boolean accept) {
        CommunicatorMessage root = em.find(CommunicatorMessage.class, messageId);
        if (root != null && (root.getStatus().equals(CommunicatorMessageStatus.CREATED) || root.getStatus().equals(CommunicatorMessageStatus.PENDING))) {
            root.setAcceptor(userLocal.getCurrentUser());
            root.setStatus(accept ? CommunicatorMessageStatus.OUTBOX : CommunicatorMessageStatus.REJECTED);
            root.getUsers().stream().filter(mu -> mu.getStatus().equals(CommunicatorReadStatus.PENDING)).forEach(mu -> {
                mu.setStatus(accept ? CommunicatorReadStatus.SENT : CommunicatorReadStatus.REJECTED);
            });
        }

    }

    private void validateMessageReadStatus(long messageId) throws FinATypeException {
        if (messageId > 0) {
            long countConversationMessages = em.createQuery("select count(id) from IN_COMMUNICATOR_MESSAGES  where replyToId=:rootMessageId", Long.class)
                    .setParameter("rootMessageId", messageId).
                    getSingleResult();

            long countReadMessages = em.createQuery("select count(mu.status) from IN_MESSAGE_USERS mu where mu.status=:status and mu.messageUserId.message = :messageId ", Long.class)
                    .setParameter("status", CommunicatorReadStatus.READ)
                    .setParameter("messageId", messageId)
                    .getSingleResult();

            if (countConversationMessages > 0) {
                throw new FinATypeException("Conversation has messages");
            } else if (countReadMessages > 0) {
                throw new FinATypeException("The message has already been read");
            }
        }
    }


    private void validateNotificationReadStatus(long notificationId) throws FinATypeException {
        if (notificationId > 0) {
            boolean isPublishedAndRead = em.createQuery("select count(nu.id) from IN_COMMUNICATOR_NOTIFICATION_USERS nu " +
                            "where nu.userNotificationId.notification.id=:notificationId and nu.userNotificationId.notification.status=:notificationStatus and nu.status=:readStatus ", Long.class)
                    .setParameter("notificationId", notificationId)
                    .setParameter("readStatus", CommunicatorReadStatus.READ)
                    .setParameter("notificationStatus", CommunicatorNotificationStatus.PUBLISHED)
                    .getSingleResult() > 0;

            if (isPublishedAndRead) {
                throw new FinATypeException("The notification has already been published and read");
            }
        }
    }

    private List<CommunicatorNotification> load(boolean read, int start, int limit, SortField sortField) {
        String path = (sortField != null ? "c." + sortField.getProperty() + " " + sortField.getDirection() : " c.publishDate desc");

        TypedQuery<CommunicatorNotification> query = em.createQuery("select distinct c from IN_COMMUNICATOR_NOTIFICATIONS c LEFT OUTER JOIN c.notificationUsers nu where c.deleted=false and c.status=:status and nu.userNotificationId.user.id=:userId and nu.status=:notificationStatus  order by " + path, CommunicatorNotification.class)
                .setParameter("userId", userLocal.getCurrentUserId()).setParameter("status", CommunicatorNotificationStatus.PUBLISHED)
                .setParameter("notificationStatus", read ? CommunicatorReadStatus.READ : CommunicatorReadStatus.SENT);

        if (start >= 0 && limit >= 0) {
            query.setFirstResult(start).setMaxResults(limit);
        }

        return query.getResultList();
    }

    private List<Predicate> getNotificationFilterPredicate(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<CommunicatorNotification> root, Map<MessageFilter, String> filterMap) {
        List<Predicate> predicates = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        if (filterMap != null) {
            for (Map.Entry<MessageFilter, String> entry : filterMap.entrySet()) {
                if (entry.getValue() != null) {
                    switch (entry.getKey()) {
                        case TITLE:
                            predicates.add(cb.like(cb.lower(cb.trim(root.get(CommunicatorNotification_.title))), "%" + entry.getValue().toLowerCase() + "%"));
                            break;
                        case CONTENT:
                            predicates.add(cb.like(cb.lower(cb.trim(root.get(CommunicatorNotification_.content))), "%" + entry.getValue().toLowerCase() + "%"));
                            break;
                        case AUTHORS:
                            List<Long> authorIds = getIdsAsList(entry.getValue());
                            if (!authorIds.isEmpty()) {
                                predicates.add(root.get(CommunicatorNotification_.user).get(User_.id).in(authorIds));
                            } else {
                                predicates.add(root.get(CommunicatorNotification_.user).get(User_.id).in(Collections.singletonList(-1L)));
                            }
                            break;
                        case RECIPIENTS:
                            List<Long> recipientIds = getIdsAsList(entry.getValue());
                            Join<CommunicatorNotification, CommunicatorNotificationUser> join = root.join(CommunicatorNotification_.notificationUsers, JoinType.LEFT);
                            if (!recipientIds.isEmpty()) {
                                predicates.add(join.get(CommunicatorNotificationUser_.userNotificationId).get(UserNotificationId_.user).get(User_.id).in(recipientIds));
                            } else {
                                predicates.add(join.get(CommunicatorNotificationUser_.userNotificationId).get(UserNotificationId_.user).get(User_.id).in(Collections.singletonList(-1L)));
                            }
                            break;
                        case STATUS:
                            predicates.add(cb.equal(root.get(CommunicatorNotification_.status), CommunicatorNotificationStatus.valueOf(entry.getValue())));
                            break;
                        case HAS_WORDS:
                            String val = "%" + entry.getValue().toLowerCase() + "%";
                            predicates.add(cb.or(cb.like(cb.lower(cb.trim(root.get(CommunicatorNotification_.title))), val), cb.like(cb.lower(cb.trim(root.get(CommunicatorNotification_.content))), val), cb.like(cb.lower(cb.trim(root.get(CommunicatorNotification_.user).get(User_.login))), val)));
                            break;
                        case AFTER:
                            try {
                                predicates.add(cb.or(cb.greaterThanOrEqualTo(root.get(CommunicatorNotification_.creationDate), df.parse(entry.getValue())), cb.greaterThanOrEqualTo(root.get(CommunicatorNotification_.publishDate), df.parse(entry.getValue()))));
                            } catch (ParseException e) {
                                log.error(e.getMessage(), e);
                            }
                            break;
                        case BEFORE:
                            try {
                                predicates.add(cb.or(cb.lessThanOrEqualTo(root.get(CommunicatorNotification_.creationDate), df.parse(entry.getValue())), cb.lessThanOrEqualTo(root.get(CommunicatorNotification_.publishDate), df.parse(entry.getValue()))));
                            } catch (ParseException e) {
                                log.error(e.getMessage(), e);
                            }
                            break;
                        case HIDE_AUTOMATIC:
                            if (Boolean.parseBoolean(entry.getValue())) {
                                predicates.add(cb.equal(root.get(CommunicatorNotification_.AUTOMATIC), false));
                            }
                            break;
                        case NOTIFICATION_TYPE: {
                            CommunicatorNotificationTypeFilter type = CommunicatorNotificationTypeFilter.valueOf(entry.getValue());
                            if (type == CommunicatorNotificationTypeFilter.ALL) {
                                break;
                            }
                            predicates.add(cb.equal(root.get(CommunicatorNotification_.AUTOMATIC), type == CommunicatorNotificationTypeFilter.SYSTEM));
                            break;
                        }
                        case ATTACHMENT:
                            predicates.add(cb.equal(root.get(CommunicatorNotification_.hasAttachments), Boolean.parseBoolean(entry.getValue())));
                            break;
                    }
                }
            }
        }
        return predicates;
    }


    private List<Predicate> getFilterPredicate(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<CommunicatorMessage> root, Map<MessageFilter, String> filterMap) {
        List<Predicate> predicates = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Join<CommunicatorMessage, MessageUser> join;
        if (filterMap != null) {
            for (Map.Entry<MessageFilter, String> entry : filterMap.entrySet()) {
                if (entry.getValue() != null) {
                    switch (entry.getKey()) {
                        case TITLE:
                            predicates.add(cb.or(cb.like(cb.lower(cb.trim(root.get(CommunicatorMessage_.title))), "%" + entry.getValue().toLowerCase() + "%"), cb.like(cb.lower(cb.trim(root.get(CommunicatorMessage_.content))), "%" + entry.getValue().toLowerCase() + "%")));
                            predicates.add(cb.equal(root.get(CommunicatorMessage_.replyToId), 0));
                            break;
                        case CONTENT:
                            predicates.add(cb.like(cb.lower(cb.trim(root.get(CommunicatorMessage_.content))), "%" + entry.getValue().toLowerCase() + "%"));
                            break;
                        case AUTHORS:
                            List<Long> authorIds = getIdsAsList(entry.getValue());

                            if (!authorIds.isEmpty()) {
                                predicates.add((root.get(CommunicatorMessage_.user).get(User_.id).in(authorIds)));
                            }
                            break;
                        case RECIPIENTS:
                            List<Long> recipientIds = getIdsAsList(entry.getValue());
                            join = root.join(CommunicatorMessage_.users, JoinType.LEFT);

                            if (!recipientIds.isEmpty()) {
                                predicates.add(join.get(MessageUser_.messageUserId).get(MessageUserId_.user).in(recipientIds));

                                //indicator that message is sent by external user (recipient and author ids are same)
                                predicates.add(cb.notEqual(join.get(MessageUser_.messageUserId).get(MessageUserId_.user), root.get(CommunicatorMessage_.user).get(User_.id)));
                            }
                            break;
                        case FIS:
                            List<Long> fiIds = getIdsAsList(entry.getValue());
                            List<Long> fiUserIds = fiLocal.loadAllUsersInFis(fiIds);

                            join = root.join(CommunicatorMessage_.users, JoinType.LEFT);
                            if (!fiUserIds.isEmpty()) {
                                predicates.add(join.get(MessageUser_.messageUserId).get(MessageUserId_.user).in(fiUserIds));
                            } else {
                                predicates.add(cb.equal(root.get(CommunicatorMessage_.id), -1L));
                            }
                            break;
                        case STATUS:
                            predicates.add(cb.equal(root.get(CommunicatorMessage_.status), CommunicatorMessageStatus.valueOf(entry.getValue())));
                            break;
                        case HAS_WORDS:
                            String val = "%" + entry.getValue().toLowerCase() + "%";

                            Subquery<Long> rootMessageIds = cq.subquery(Long.class);
                            Root<CommunicatorMessage> subQueryRoot = rootMessageIds.from(CommunicatorMessage.class);
                            rootMessageIds.select(subQueryRoot.get(CommunicatorMessage_.replyToId));
                            rootMessageIds.where(cb.like(cb.lower(cb.trim(subQueryRoot.get(CommunicatorMessage_.content))), val));

                            predicates.add(cb.or(root.get(CommunicatorMessage_.id).in(rootMessageIds)));
                            break;
                        case AFTER:
                            try {
                                predicates.add(cb.or(cb.greaterThanOrEqualTo(root.get(CommunicatorMessage_.creationDate), df.parse(entry.getValue())), cb.greaterThanOrEqualTo(root.get(CommunicatorMessage_.sendDate), df.parse(entry.getValue()))));
                            } catch (ParseException e) {
                                log.error(e.getMessage(), e);
                            }
                            break;
                        case BEFORE:
                            try {
                                predicates.add(cb.or(cb.lessThanOrEqualTo(root.get(CommunicatorMessage_.creationDate), df.parse(entry.getValue())), cb.lessThanOrEqualTo(root.get(CommunicatorMessage_.sendDate), df.parse(entry.getValue()))));
                            } catch (ParseException e) {
                                log.error(e.getMessage(), e);
                            }
                            break;
                        case ATTACHMENT:
                            predicates.add(cb.equal(root.get(CommunicatorMessage_.hasAttachments), entry.getValue() != null && Boolean.parseBoolean(entry.getValue())));
                            break;
                        case NOREPLIES:
                            join = root.join(CommunicatorMessage_.users, JoinType.LEFT);
                            predicates.add(cb.equal(join.get(MessageUser_.lastMessageUser).get(User_.id), join.get(MessageUser_.messageUserId).get(MessageUserId_.user)));
//                            cq.distinct(true);
                            break;
                        case BOOKMARKED:
                            if (Boolean.parseBoolean(entry.getValue())) {
                                predicates.add(cb.isTrue(root.get(CommunicatorMessage_.isRootBookmarked)));
                                break;
                            }
                        case IGNORED_REPLIES:
                            if (Boolean.parseBoolean(entry.getValue())) {
                                cq.distinct(true);
                                join = root.join(CommunicatorMessage_.users, JoinType.INNER);
                                Join<MessageUser, UserMessageStatus> umsJoin = join.join(MessageUser_.userMessageStatus, JoinType.INNER);

                                predicates.add(cb.notEqual(umsJoin.get(UserMessageStatus_.user), userLocal.getCurrentUserId()));

                                Subquery<Long> subquery = cq.subquery(Long.class);
                                Root<UserMessageStatus> subRoot = subquery.from(UserMessageStatus.class);
                                subquery.select(subRoot.get(UserMessageStatus_.id))
                                        .where(cb.and(
                                                cb.equal(subRoot.get(UserMessageStatus_.messageId), root.get(CommunicatorMessage_.id)),
                                                cb.equal(subRoot.get(UserMessageStatus_.messageUserId), join.get(MessageUser_.messageUserId).get(MessageUserId_.user)),
                                                cb.notEqual(subRoot.get(UserMessageStatus_.user), umsJoin.get(UserMessageStatus_.user)),
                                                cb.isNull(subRoot.get(UserMessageStatus_.readDate))
                                        ));
                                predicates.add(cb.exists(subquery));
                                break;

                            }


                        case UNREAD:
                            if (Boolean.parseBoolean(entry.getValue())) {
                                join = root.join(CommunicatorMessage_.users, JoinType.LEFT);
                                long currentUserId = userLocal.getCurrentUserId();

                                predicates.add(cb.or(
                                        cb.and(
                                                cb.notEqual(join.get(MessageUser_.lastMessageUser).get(User_.id), currentUserId),
                                                cb.not(join.get(MessageUser_.status).in(Arrays.asList(CommunicatorReadStatus.READ.ordinal(), CommunicatorReadStatus.REJECTED.ordinal())))
                                        ),
                                        cb.and(
                                                cb.notEqual(root.get(CommunicatorMessage_.user).get(User_.id), currentUserId),
                                                cb.equal(join.get(MessageUser_.messageUserId).get(MessageUserId_.user), currentUserId),
                                                cb.or(cb.isNull(join.get(MessageUser_.lastMessageUser).get(User_.id)), cb.notEqual(join.get(MessageUser_.lastMessageUser).get(User_.id), currentUserId))
                                        )));

                                predicates.add(cb.not(join.get(MessageUser_.status).in(Arrays.asList(CommunicatorReadStatus.READ.ordinal(), CommunicatorReadStatus.REJECTED.ordinal()))));
                                break;
                            }
                            break;

                        case MARK_TYPE:
                            List<CommunicatorMessageMarkType> markTypeList = getEnumListFromString(entry.getValue(), CommunicatorMessageMarkType.class);
                            if (markTypeList.isEmpty()) {
                                predicates.add(cb.equal(root.get(CommunicatorMessage_.id), -1L));
                                break;
                            }
                            Subquery<Long> subquery = cq.subquery(Long.class);
                            Root<CommunicatorMessage> subRoot = subquery.from(CommunicatorMessage.class);
                            subquery.select(subRoot.get(CommunicatorMessage_.replyToId))
                                    .where(subRoot.get(CommunicatorMessage_.markType).in(markTypeList));

                            predicates.add(cb.or(
                                    cb.and(
                                            root.get(CommunicatorMessage_.markType).in(markTypeList),
                                            cb.isTrue(root.get(CommunicatorMessage_.isRootBookmarked))
                                    ),
                                    root.get(CommunicatorMessage_.id).in(subquery)
                            ));
                            break;

                    }
                }
            }
        }
        return predicates;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setLegislativeDocumentNotification(@Observes(during = TransactionPhase.AFTER_SUCCESS) LegislativeDocumentNotificationEvent event) throws FinATypeException {
        LegislativeDocument document = event.getDocument();

        CommunicatorNotification notification = new CommunicatorNotification();

        String descriptionHtml = document.getDescription() != null && !document.getDescription().getDescriptions().isEmpty() ? "</br><p>" + document.getDescription().getDescriptions().values().toArray()[0].toString() + "</p>" : "";

        notification.setTitle(document.getFileName());

        String downloadFunction = "onClick=\"function download(){" +
                "event.preventDefault();" +
                "var docId = event.target.getAttribute('docId');\n" +
                "window.onLegislativeDocumentDownloadLinkClick(docId);}; download();\"";
        String contentString = "<a docId=\"" + document.getId() + "\" href=\"#\" " + downloadFunction + "\">" + document.getFileName() + "</a>" + descriptionHtml;
        notification.setContent(contentString);

        notification.setCreationDate(new Date());
        notification.setStatus(CommunicatorNotificationStatus.PUBLISHED);
        notification.setPublishDate(new Date());
        notification.setNotificationUsers(null);

        notification = saveNotification(notification);

        List<CommunicatorNotificationUser> userList = new ArrayList<>();

        Set<User> users = new HashSet<>(fiLocal.loadFiTypeUsers(document.getFiType().getId()));

        for (User user : users) {
            UserNotificationId userNotificationId = new UserNotificationId();

            userNotificationId.setUser(user);
            userNotificationId.setNotification(notification);

            CommunicatorNotificationUser communicatorNotificationUser = new CommunicatorNotificationUser();
            communicatorNotificationUser.setUserNotificationId(userNotificationId);
            communicatorNotificationUser.setStatus(CommunicatorReadStatus.SENT);

            userList.add(communicatorNotificationUser);
        }

        notification.setNotificationUsers(userList);

        saveNotification(notification);

    }

    @Override
    public void deleteNotifications(List<Long> notificationIds) {
        em.createQuery("update IN_COMMUNICATOR_NOTIFICATIONS c set c.deleted=true  WHERE c.id in :notificationIds").setParameter("notificationIds", notificationIds).executeUpdate();
    }

    @Override
    public void deleteMessages(List<Long> messageIds) {
        for (Long id : messageIds) {
            deleteMessage(id);
        }
    }

    @Override
    public long countNewMessages() {
        try {
            long userId = userLocal.getCurrentUserId();
            Map<Long, Integer> messageRecipientSizeMap = new HashMap<>();
            Map<Long, Integer> messageReadCountMap = new HashMap<>();
            List<Object[]> messageRecipients = em.createQuery("select id,recipientSize from IN_COMMUNICATOR_MESSAGES where deleted=false and replyToId=0 and recipientSize>0", Object[].class).getResultList();
            for (Object[] obj : messageRecipients) {
                messageRecipientSizeMap.put((Long) obj[0], (Integer) obj[1]);
            }
            List<Object[]> messageReadCount = em.createNativeQuery("select message_id,count(message_user_id) from IN_COMMUNICATOR_USERS_MESSAGE_STATUS where USER_ID=:userId and status=:status and message_id is not null group by USER_ID,message_id", Object[].class)
                    .setParameter("userId", userId)
                    .setParameter("status", CommunicatorReadStatus.READ.ordinal())
                    .getResultList();

            for (Object[] obj : messageReadCount) {
                messageReadCountMap.put(((Number) obj[0]).longValue(), (Integer) obj[1]);
            }

            int counter = 0;

            for (Map.Entry<Long, Integer> entry : messageRecipientSizeMap.entrySet()) {
                long difference = entry.getValue() - messageReadCountMap.getOrDefault(entry.getKey(), 0);
                if (difference > 0) {
                    counter++;
                }
            }

            return counter;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return 0;


    }

    @Override
    public CommunicatorAttachment getAttachmentById(long id) {
        return em.find(CommunicatorAttachment.class, id);
    }

    @Override
    public long countPendingMessageUsers(MessageUser mu) {
        return em.createQuery("select count(distinct m.id) from IN_COMMUNICATOR_MESSAGES m where m.status=:status and m.deleted=false and m.replyToId=:replyToId and m.replyToUserId=:replyToUserId", Long.class).setParameter("status", CommunicatorMessageStatus.PENDING).setParameter("replyToId", mu.getMessageUserId().getMessage()).setParameter("replyToUserId", mu.getMessageUserId().getUser()).getSingleResult();
    }

    @Override
    public long countPendingMessage(CommunicatorMessage message) {
        return em.createQuery("select count(distinct m.id) from IN_COMMUNICATOR_MESSAGES m where m.status=:status and m.deleted=false and m.replyToId=:replyToId and m.replyToUserId in(select messageUserId.user from IN_MESSAGE_USERS where messageUserId.message=:replyToId)", Long.class).setParameter("status", CommunicatorMessageStatus.PENDING).setParameter("replyToId", message.getId()).getSingleResult();
    }

    @Override
    public boolean isNotRepliedMessageUser(MessageUser mu) {

        return !em.createQuery("select m.id from IN_COMMUNICATOR_MESSAGES m where m.replyToId=:replyToId and m.deleted=false and m.replyToUserId=:replyToUserId and m.user.id=:userId order by m.sendDate  desc ", Long.class).setParameter("replyToId", mu.getMessageUserId().getMessage()).setParameter("replyToUserId", mu.getMessageUserId().getUser()).setParameter("userId", mu.getMessageUserId().getUser()).setMaxResults(1).getResultList().isEmpty();
    }

    @Override
    public Map<CommunicatorDashletModel.MessagesStatistics, Object> loadMessageStatistics() {
        Map<CommunicatorDashletModel.MessagesStatistics, Object> result = new HashMap<>();

        int rootMessageCount = em.createQuery("select count (m.id) from IN_COMMUNICATOR_MESSAGES m where m.replyToId=0 and m.deleted=false", Long.class).getSingleResult().intValue();
        int threadCount = em.createQuery("select count (m.messageUserId.user) from IN_MESSAGE_USERS m ", Long.class).getSingleResult().intValue();
        int sentMessages = em.createQuery("select count(m.id) from IN_COMMUNICATOR_MESSAGES m where m.user in (:userIds) and m.deleted=false ", Long.class).setParameter("userIds", userLocal.loadUserByPermission(PermissionIdNames.FINA_WEB_INTERNAL_USER)).getSingleResult().intValue();

        result.put(CommunicatorDashletModel.MessagesStatistics.INITIATIVE_MESSAGES_COUNT, rootMessageCount);
        result.put(CommunicatorDashletModel.MessagesStatistics.THREAD_COUNT, threadCount);
        result.put(CommunicatorDashletModel.MessagesStatistics.SENTBOX_COUNT, sentMessages + (threadCount - rootMessageCount));
        result.put(CommunicatorDashletModel.MessagesStatistics.INBOX_COUNT, em.createQuery("select count(m.id) from IN_COMMUNICATOR_MESSAGES m where m.user in (:userIds) and m.deleted=false ", Long.class).setParameter("userIds", userLocal.loadUserByPermission(PermissionIdNames.FINA_WEB_EXTERNAL_USER)).getSingleResult().intValue());


        return result;
    }

    @Override
    public long countNotifications(Map<MessageFilter, String> filterMap) {
        reverseAuthorIdsIfExists(filterMap);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<CommunicatorNotification> root = cq.from(CommunicatorNotification.class);

        cq.select(cb.countDistinct(root.get(CommunicatorNotification_.id)));

        List<Predicate> predicates = getNotificationFilterPredicate(cb, cq, root, filterMap);
        predicates.add(cb.equal(root.get(CommunicatorNotification_.deleted), false));

        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getSingleResult();
    }

    @Override
    public long countAllMessages(Map<MessageFilter, String> filterMap) {
        reverseAuthorIdsIfExists(filterMap);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<CommunicatorMessage> root = cq.from(CommunicatorMessage.class);

        cq.select(cb.countDistinct(root.get(CommunicatorMessage_.ID)));

        List<Predicate> predicates = getFilterPredicate(cb, cq, root, filterMap);
        if (!filterMap.containsKey(MessageFilter.HAS_WORDS)) {
            predicates.add(cb.equal(root.get(CommunicatorMessage_.replyToId), 0));
        }
        predicates.add(cb.equal(root.get(CommunicatorMessage_.deleted), false));

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> query = em.createQuery(cq);

        return query.getSingleResult();
    }

    @Override
    public long countMessageUsers(long messageId, Map<MessageFilter, String> filterMap) {
        reverseAuthorIdsIfExists(filterMap);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<MessageUser> root = cq.from(MessageUser.class);

        cq.select(cb.countDistinct(root.get(MessageUser_.messageUserId).get(MessageUserId_.user)));

        List<Predicate> predicates = getMessageUserFilterPredicate(cb, cq, root, filterMap, messageId);
        predicates.add(cb.equal(root.get(MessageUser_.messageUserId).get(MessageUserId_.message), messageId));

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> query = em.createQuery(cq);

        return query.getSingleResult();
    }

    @Override
    public long countUserNotifications() {
        return em.createQuery("select count( distinct c) from IN_COMMUNICATOR_NOTIFICATIONS c LEFT OUTER JOIN c.notificationUsers nu where c.deleted=false and c.status=:status and nu.userNotificationId.user.id=:userId and nu.status=:notificationStatus", Long.class).setParameter("userId", userLocal.getCurrentUserId()).setParameter("status", CommunicatorNotificationStatus.PUBLISHED).setParameter("notificationStatus", CommunicatorReadStatus.READ).getSingleResult();
    }

    @Override
    public long countUserMessages() {
        return em.createQuery("select count( distinct cm) from IN_COMMUNICATOR_MESSAGES cm LEFT OUTER JOIN cm.users cu where cm.deleted=false and cm.status in :statuses and cm.replyToId=0 and (cm.user.id=:userId or cu.messageUserId.user=:userId)", Long.class).setParameter("userId", userLocal.getCurrentUserId()).setParameter("statuses", Arrays.asList(CommunicatorMessageStatus.OUTBOX, CommunicatorMessageStatus.INBOX)).getSingleResult();
    }

    @Override
    public void markAllMessagesAsRead() {
        long currentUserId = userLocal.getCurrentUserId();
        em.createNativeQuery("insert into IN_COMMUNICATOR_USERS_MESSAGE_STATUS (id,USER_ID,message_id,message_user_id,READ_DATE,status) select next value for com_users_messages_sequence," + currentUserId + ",mu.MESSAGE_ID,mu.USER_ID, GETDATE(),0 from IN_MESSAGE_USERS mu where mu.USER_ID not in(select message_user_id from IN_COMMUNICATOR_USERS_MESSAGE_STATUS where IN_COMMUNICATOR_USERS_MESSAGE_STATUS.message_id=mu.MESSAGE_ID and user_id=" + currentUserId + ")").executeUpdate();
        em.createQuery("update IN_COMMUNICATOR_USERS_MESSAGE_STATUS set status=:readStatus where user=:currUserId and status<>:readStatus")
                .setParameter("readStatus", CommunicatorReadStatus.READ)
                .setParameter("currUserId", currentUserId)
                .executeUpdate();

    }

    @Override
    public void markAllMessageThreadsAsRead(long messageId) {
        long currentUserId = userLocal.getCurrentUserId();
        em.createNativeQuery(" insert into IN_COMMUNICATOR_USERS_MESSAGE_STATUS (id,USER_ID,message_id,message_user_id,READ_DATE,status) " +
                        "select next value for com_users_messages_sequence," + currentUserId + ",mu.MESSAGE_ID,mu.USER_ID, GETDATE(),0 " +
                        "from IN_MESSAGE_USERS mu where mu.MESSAGE_ID=:messageId " +
                        "and mu.USER_ID not in(select message_user_id from IN_COMMUNICATOR_USERS_MESSAGE_STATUS where IN_COMMUNICATOR_USERS_MESSAGE_STATUS.message_id=mu.MESSAGE_ID " +
                        "and user_id=" + currentUserId + ")")
                .setParameter("messageId", messageId)
                .executeUpdate();
        em.createQuery("update IN_COMMUNICATOR_USERS_MESSAGE_STATUS set status=:readStatus where messageId=:messageId and user=:currUserId and status<>:readStatus")
                .setParameter("messageId", messageId)
                .setParameter("readStatus", CommunicatorReadStatus.READ)
                .setParameter("currUserId", currentUserId)
                .executeUpdate();
    }

    @Override
    public void updateAttachments(Collection<CommunicatorAttachmentModel> attachments, long messageId) {
        if (attachments.isEmpty()) {
            em.createQuery("delete from CommunicatorAttachment at where at.messageId=:messageId").setParameter("messageId", messageId).executeUpdate();
        } else {
            List<Long> fileIds = attachments.stream().map(CommunicatorAttachmentModel::getId).collect(Collectors.toList());
            if (!fileIds.isEmpty()) {
                em.createQuery("delete from CommunicatorAttachment at where at.messageId=:messageId and at.id not in(:fileIds)").setParameter("messageId", messageId).setParameter("fileIds", fileIds).executeUpdate();
            }
        }
    }

    public CommunicatorMessage markMessage(long messageId, CommunicatorMessageMarkType markType) {
        CommunicatorMessage m = em.find(CommunicatorMessage.class, messageId);
        m.setMarkType(markType);

        long replyToId = m.getReplyToId();
        CommunicatorMessage rootMessage = replyToId == 0 ? m : em.find(CommunicatorMessage.class, replyToId);

        if (markType != null) {
            rootMessage.setRootBookmarked(true);
        } else {
            int numOfMarkedReplies = getMarkedRepliesCount(replyToId);
            rootMessage.setRootBookmarked(numOfMarkedReplies > 0);
        }
        return m;
    }


    @Override
    public long countUnreadMessagesInConversation(long messageId, long userId) {
        //TODO improve query
        long conversationCount = em.createQuery("select count(m.id) from IN_COMMUNICATOR_MESSAGES m where m.replyToId=:rootMessageId and m.replyToUserId=:replyToUserId  and m.user.id<>:userId and m.recipientReadDate is null ", Long.class).setParameter("rootMessageId", messageId).setParameter("replyToUserId", userId).setParameter("userId", userId).getSingleResult();
        if (conversationCount == 0) {
            return em.createQuery("select count(m.id) from IN_COMMUNICATOR_MESSAGES m left join m.users imu where m.replyToId=0 and m.id=:messageId and imu.status=:status and m.id=:messageId and imu.messageUserId.user=:userId", Long.class)
                    .setParameter("messageId", messageId)
                    .setParameter("userId", userId)
                    .setParameter("status", CommunicatorReadStatus.SENT)
                    .getSingleResult();

        }

        return conversationCount;
    }

    @Override
    public CommunicatorMessage getMessageById(long messageId) {
        return em.find(CommunicatorMessage.class, messageId);
    }

    @Override
    public long countConversationMessages(long rootMessageId, long userId) {
        return em.createQuery("select count (m.id) from IN_COMMUNICATOR_MESSAGES m where m.replyToId=:messageId and m.replyToUserId=:userId", Long.class).setParameter("messageId", rootMessageId).setParameter("userId", userId).getSingleResult();
    }

    @Override
    public CommunicatorMessageModel countNewAndCheckPending(CommunicatorMessage message, User currentUser, CommunicatorMessageModel messageModel) {

        int messageCounter = 0;
        boolean hasPendingConversation = countPendingMessage(message) > 0;


        messageCounter = countRootMessageUnreadThreads(message, currentUser.getId());

        messageModel.setNew(messageCounter > 0);
        messageModel.setNewMessages(messageCounter);
        messageModel.setHasPendingMessage(hasPendingConversation);
        messageModel.setRecipientCount(message.getRecipientSize());

        return messageModel;
    }

    @Override
    public boolean checkUserHasReadMessage(MessageUser mu, User user) {
        List status = em.createQuery("select mus.status from IN_COMMUNICATOR_USERS_MESSAGE_STATUS mus where mus.messageId=:messageId and mus.messageUserId=:messageuserId and mus.user=:userId")
                .setParameter("messageId", mu.getMessageUserId().getMessage())
                .setParameter("messageuserId", mu.getMessageUserId().getUser())
                .setParameter("userId", user.getId())
                .getResultList();

        if (status.isEmpty()) {
            return true;
        } else {
            return !status.get(0).equals(CommunicatorReadStatus.READ);
        }

    }

    private int countRootMessageUnreadThreads(CommunicatorMessage message, long userId) {
        List statusses = em.createQuery("select mus.status from IN_COMMUNICATOR_USERS_MESSAGE_STATUS mus where mus.messageId=:messageId and mus.user=:userId and mus.status=:status")
                .setParameter("messageId", message.getId())
                .setParameter("userId", userId)
                .setParameter("status", CommunicatorReadStatus.READ)
                .getResultList();

        if (statusses.isEmpty()) {
            return message.getRecipientSize();
        }

        return message.getRecipientSize() - statusses.size();
    }

    @Override
    public long countNotificationUsers(long notificationId, Map<MessageFilter, String> filterMap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<CommunicatorNotificationUser> root = cq.from(CommunicatorNotificationUser.class);

        cq.select(cb.count(root.get(CommunicatorNotificationUser_.userNotificationId).get(UserNotificationId_.notification).get(CommunicatorNotification_.id)));

        List<Predicate> predicates = getNotificationUserFilterPredicate(cb, cq, root, filterMap);
        predicates.add(cb.equal(root.get(CommunicatorNotificationUser_.userNotificationId).get(UserNotificationId_.notification).get(CommunicatorNotification_.id), notificationId));

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> query = em.createQuery(cq);
        return query.getSingleResult();
    }

    @Override
    public CommunicatorMessage loadLastConversationMessage(long messageId, long userId) {
        List<CommunicatorMessage> result = em.createQuery("select  m from IN_COMMUNICATOR_MESSAGES m where m.replyToId=:replyToId and m.replyToUserId=:replyToUserId and m.id in (select max(mm.id) from IN_COMMUNICATOR_MESSAGES mm group by mm.replyToId,mm.replyToUserId)", CommunicatorMessage.class).setParameter("replyToId", messageId).setParameter("replyToUserId", userId).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public long countRecipientNewMessages() {
        return em.createQuery("select count(mu) from IN_MESSAGE_USERS mu left join IN_COMMUNICATOR_MESSAGES cm on cm.id = mu.messageUserId.message where mu.status=:status and mu.messageUserId.user=:userId and cm.deleted=false", Long.class).setParameter("userId", userLocal.getCurrentUserId()).setParameter("status", CommunicatorReadStatus.SENT).getSingleResult();
    }

    @Override
    public List<Long> loadRootMessageRecipientUserIds(long rootMessageId) {
        return em.createQuery("select messageUserId.user from IN_MESSAGE_USERS where messageUserId.message=:messageId", Long.class)
                .setParameter("messageId", rootMessageId)
                .getResultList();
    }

    @Override
    public List<Long> loadNotificationRecipientUserIds(long notificationId) {
        return em.createQuery("select userNotificationId.user.id from IN_COMMUNICATOR_NOTIFICATION_USERS where userNotificationId.notification.id=:notificationId", Long.class)
                .setParameter("notificationId", notificationId)
                .getResultList();
    }

    @Override
    public List<User> loadRootMessageRecipients(long rootMessageId) {
        List<Long> userIds = loadRootMessageRecipientUserIds(rootMessageId);
        return em.createQuery("select new net.fina.server.security.entity.User(u.id, u.login, u.description) from SYS_USERS u where u.id in (:userIds)", User.class).setParameter("userIds", !userIds.isEmpty() ? userIds : Collections.singletonList(-1L)).getResultList();
    }

    @Override
    public List<User> loadNotificationRecipients(long notificationId) {
        List<Long> userIds = loadNotificationRecipientUserIds(notificationId);
        return em.createQuery("select new net.fina.server.security.entity.User(u.id, u.login, u.description) from SYS_USERS u where u.id in (:userIds)", User.class).setParameter("userIds", !userIds.isEmpty() ? userIds : Collections.singletonList(-1L)).getResultList();
    }

    @Override
    public void saveNotificationUsers(List<CommunicatorNotificationUser> userList) {
        for (CommunicatorNotificationUser u : userList) {
            em.persist(u);
        }
    }


    @Override
    public void sendSavedMessage(long messageId) throws FinATypeException {
        CommunicatorMessage message = em.find(CommunicatorMessage.class, messageId);
        if (!message.getStatus().equals(CommunicatorMessageStatus.CREATED) &&
                !message.getStatus().equals(CommunicatorMessageStatus.PENDING)) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Message status is not valid");
        }

        message.setStatus(CommunicatorMessageStatus.OUTBOX);
        message.setSendDate(new Date());
        message.getUsers().forEach(mu -> {
            mu.setStatus(CommunicatorReadStatus.SENT);
            updateMessageUserStatus(mu.getMessageUserId().getUser(), messageId, CommunicatorReadStatus.READ); //internal user read status
        });

    }

    private int getMarkedRepliesCount(long rootMessageId) {
        TypedQuery<Long> q = em.createQuery("select count (cm.id) from IN_COMMUNICATOR_MESSAGES cm " + " where cm.replyToId =: rootMessageId and cm.markType is not null and cm.deleted = false", Long.class)
                .setParameter("rootMessageId", rootMessageId);

        return q.getSingleResult().intValue();
    }

    private void validateUserAccessOnNotification(long attachmentId, long userId) throws FinATypeException {
        CommunicatorAttachment attachment = Optional.ofNullable(em.find(CommunicatorAttachment.class, attachmentId))
                .orElseThrow(() -> new FinATypeException(FinATypeException.Type.INVALID_VALUE));

        CommunicatorNotification notification = Optional.ofNullable(em.find(CommunicatorNotification.class, attachment.getMessageId()))
                .orElseThrow(() -> new FinATypeException(FinATypeException.Type.INVALID_VALUE));


        boolean invalid = em.createQuery("select count(a.id) from CommunicatorAttachment a left join IN_COMMUNICATOR_NOTIFICATIONS cn on cn.id = :notificationId " +
                        ",in(cn.notificationUsers) cnu " +
                        " where a.id = :attachmentId and (cn.user.id = :userId or cnu.userNotificationId.user.id = :userId)", Long.class)
                .setParameter("attachmentId", attachmentId)
                .setParameter("userId", userId)
                .setParameter("notificationId", notification.getId())
                .getSingleResult() <= 0;

        if (invalid) {
            throw new FinATypeException("User does not has access to this attachment");
        }
    }

    private void validateUserAccessOnMessage(long attachmentId, long userId) throws FinATypeException {
        CommunicatorAttachment attachment = Optional.ofNullable(em.find(CommunicatorAttachment.class, attachmentId))
                .orElseThrow(() -> new FinATypeException(FinATypeException.Type.INVALID_VALUE));

        CommunicatorMessage message = Optional.ofNullable(em.find(CommunicatorMessage.class, attachment.getMessageId()))
                .orElseThrow(() -> new FinATypeException(FinATypeException.Type.INVALID_VALUE));

        //find root message for correct join on IN_MESSAGE_USERS
        if (message.getReplyToId() > 0) {
            message = em.find(CommunicatorMessage.class, message.getReplyToId());
        }

        boolean invalid = em.createQuery("select count(a.id) from CommunicatorAttachment a left join IN_COMMUNICATOR_MESSAGES cm on cm.id=:messageId" +
                        " inner join IN_MESSAGE_USERS mu on mu.messageUserId.message = :messageId " +
                        "  where a.id = :attachmentId and (cm.user.id=:userId or mu.messageUserId.user=:userId)", Long.class)
                .setParameter("attachmentId", attachmentId)
                .setParameter("userId", userId)
                .setParameter("messageId", message.getId())
                .getSingleResult() <= 0;

        if (invalid) {
            throw new FinATypeException("User does not has access to this attachment");
        }
    }


    private List<Predicate> getMessageUserFilterPredicate(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<MessageUser> root, Map<MessageFilter, String> filterMap, long rootMessageId) {
        List<Predicate> predicates = new ArrayList<>();
        if (filterMap != null) {
            for (Map.Entry<MessageFilter, String> entry : filterMap.entrySet()) {
                if (entry.getValue() != null) {
                    switch (entry.getKey()) {
                        case NOREPLIES:
                            predicates.add(cb.equal(root.get(MessageUser_.lastMessageUser).get(User_.id), root.get(MessageUser_.messageUserId).get(MessageUserId_.user)));
                            break;
                        case FIS:
                            List<Long> fiIds = getIdsAsList(entry.getValue());
                            List<Long> fiUserIds = fiLocal.loadAllUsersInFis(fiIds);
                            if (!fiUserIds.isEmpty()) {
                                predicates.add(root.get(MessageUser_.messageUserId).get(MessageUserId_.user).in(fiUserIds));
                            }
                            break;
                        case HAS_WORDS:
                            Subquery<Long> subquery = cq.subquery(Long.class);
                            Root<CommunicatorMessage> rootMessage = subquery.from(CommunicatorMessage.class);
                            subquery.select(rootMessage.get(CommunicatorMessage_.replyToUserId));
                            subquery.where(cb.and(cb.like(cb.lower(cb.trim(rootMessage.get(CommunicatorMessage_.content))), "%" + entry.getValue().toLowerCase() + "%"),
                                    cb.equal(rootMessage.get(CommunicatorMessage_.replyToId), rootMessageId)));

                            predicates.add(root.get(MessageUser_.messageUserId).get(MessageUserId_.user).in(subquery));
                            break;
                        case RECIPIENTS:
                            List<Long> recipientIds = getIdsAsList(entry.getValue());

                            if (!recipientIds.isEmpty()) {
                                predicates.add(root.get(MessageUser_.messageUserId).get(MessageUserId_.user).in(recipientIds));
                            }
                            break;

                        case STATUS:
                            List<CommunicatorReadStatus> statusEnums = getEnumListFromString(entry.getValue(), CommunicatorReadStatus.class);
                            if (!statusEnums.isEmpty()) {
                                predicates.add(root.get(MessageUser_.status).in(statusEnums));
                            } else {
                                predicates.add(cb.equal(root.get(MessageUser_.messageUserId).get(MessageUserId_.user), -1L));
                            }
                            break;
                        case MARK_TYPE:
                            List<CommunicatorMessageMarkType> markTypeList = getEnumListFromString(entry.getValue(), CommunicatorMessageMarkType.class);
                            if (markTypeList.isEmpty()) {
                                predicates.add(cb.equal(root.get(MessageUser_.messageUserId).get(MessageUserId_.user), -1L));
                                break;
                            }
                            List<Long> distinctUserIds = getMarkedConversationsUserIds(rootMessageId, markTypeList);

                            if (!distinctUserIds.isEmpty()) {
                                predicates.add(root.get(MessageUser_.messageUserId).get(MessageUserId_.user).in(distinctUserIds));
                            } else {
                                predicates.add(cb.equal(root.get(MessageUser_.messageUserId).get(MessageUserId_.user), -1L));
                            }
                            break;

                        case ATTACHMENT:
                            if (!Boolean.parseBoolean(entry.getValue())) break;
                            List<Long> attachmentUserIds = getMessageUserIdsWithAttachment(rootMessageId);

                            if (attachmentUserIds.isEmpty()) {
                                predicates.add(cb.equal(root.get(MessageUser_.messageUserId).get(MessageUserId_.user), -1L));
                            } else {
                                predicates.add(root.get(MessageUser_.messageUserId).get(MessageUserId_.user).in(attachmentUserIds));

                            }
                            break;

                    }
                }
            }
        }

        return predicates;
    }

    private List<Predicate> getNotificationUserFilterPredicate(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<CommunicatorNotificationUser> root, Map<MessageFilter, String> filterMap) {
        List<Predicate> predicates = new ArrayList<>();
        if (filterMap != null) {
            for (Map.Entry<MessageFilter, String> entry : filterMap.entrySet()) {
                if (entry.getValue() != null) {
                    switch (entry.getKey()) {
                        case RECIPIENTS:
                            List<Long> ids = getIdsAsList(entry.getValue());
                            if (!ids.isEmpty()) {
                                predicates.add(root.get(CommunicatorNotificationUser_.userNotificationId).get(UserNotificationId_.user).get(User_.id).in(ids));
                            }
                            break;

                        case STATUS:
                            List<CommunicatorReadStatus> statusEnums = getEnumListFromString(entry.getValue(), CommunicatorReadStatus.class);
                            if (!statusEnums.isEmpty()) {
                                predicates.add(root.get(CommunicatorNotificationUser_.status).in(statusEnums));
                            } else {
                                predicates.add(cb.equal(root.get(CommunicatorNotificationUser_.userNotificationId).get(UserNotificationId_.user).get(User_.id), -1L));
                            }
                            break;
                    }
                }
            }
        }

        return predicates;

    }

    private List<Long> getMessageUserIdsWithAttachment(long rootMessageId) {
        boolean hasRootMessageAttachment = !em.createQuery("select a.id from CommunicatorAttachment a where a.messageId=:rootMessageId and a.type=1", Long.class)
                .setParameter("rootMessageId", rootMessageId)
                .getResultList().isEmpty();

        if (hasRootMessageAttachment) {
            return em.createQuery("select mu.messageUserId.user from IN_MESSAGE_USERS mu where mu.messageUserId.message=:rootMessageId", Long.class)
                    .setParameter("rootMessageId", rootMessageId)
                    .getResultList();
        }

        List<Object[]> result = em.createQuery(
                        "select cm.user.id, cm.replyToUserId from IN_COMMUNICATOR_MESSAGES cm " +
                                "where (cm.id = :rootMessageId or cm.replyToId = :rootMessageId) and cm.hasAttachments=true", Object[].class)
                .setParameter("rootMessageId", rootMessageId)
                .getResultList();

        return result.stream()
                .flatMap(o -> Stream.of((Long) o[0], (Long) o[1])).distinct().collect(Collectors.toList());
    }

    private List<Long> getMarkedConversationsUserIds(long rootMessageId, List<CommunicatorMessageMarkType> markTypeList) {

        List<Object[]> result = em.createQuery(
                        "select cm.user.id, cm.replyToUserId from IN_COMMUNICATOR_MESSAGES cm " +
                                "where (cm.id = :rootMessageId or cm.replyToId = :rootMessageId) and cm.markType in(:markType)", Object[].class)
                .setParameter("rootMessageId", rootMessageId)
                .setParameter("markType", markTypeList)
                .getResultList();

        Set<Long> distinctIds = result.stream()
                .flatMap(o -> Stream.of((Long) o[0], (Long) o[1]))
                .collect(Collectors.toSet());

        boolean isRootMessageMarked = !em.createQuery("select cm.id from IN_COMMUNICATOR_MESSAGES cm where cm.id=:id and cm.markType in(:markType)", Long.class)
                .setParameter("id", rootMessageId)
                .setParameter("markType", markTypeList)
                .getResultList().isEmpty();
        if (isRootMessageMarked) {
            List<Long> rootMessageUserIds = em.createQuery("select mu.messageUserId.user from IN_MESSAGE_USERS mu " + " where mu.messageUserId.message=:mId", Long.class)
                    .setParameter("mId", rootMessageId)
                    .getResultList();
            distinctIds.addAll(rootMessageUserIds);
        }

        return new ArrayList<>(distinctIds);
    }

    private String getAuthorIdsAsString(List<Long> authorIds) {
        String result = null;
        if (authorIds != null && !authorIds.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Long authorId : authorIds) {
                sb.append(authorId).append(",");
            }
            result = sb.substring(0, sb.toString().length() - 1);
        }
        return result;
    }

    private List<Long> getIdsAsList(String authorIds) {
        try {
            return Stream.of(authorIds.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(Long::parseLong).collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return Collections.singletonList(-1L);
        }
    }

    private void reverseAuthorIdsIfExists(Map<MessageFilter, String> filterMap) {
        String authorIdsString = filterMap != null ? filterMap.get(MessageFilter.AUTHORS) : null;
        if (authorIdsString != null && !authorIdsString.trim().isEmpty() && filterMap.containsKey(MessageFilter.REVERSE_AUTHORS)) {
            boolean reverseAuthors = Boolean.parseBoolean(filterMap.get(MessageFilter.REVERSE_AUTHORS));
            if (reverseAuthors) {
                List<Long> authorIds = getIdsAsList(authorIdsString);
                authorIds = userLocal.loadUserIdsNotIn(authorIds);
                filterMap.put(MessageFilter.AUTHORS, getAuthorIdsAsString(authorIds));
            }
        }
    }

    private <T extends Enum<T>> List<T> getEnumListFromString(String statusListString, Class<T> enumClass) {
        statusListString = statusListString.substring(1, statusListString.length() - 1);
        String[] elements = statusListString.split(",\\s*");

        List<T> enumList = new ArrayList<>();

        for (String element : elements) {
            try {
                T status = Enum.valueOf(enumClass, element);
                enumList.add(status);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return enumList;
    }


}
