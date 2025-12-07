package net.fina.server.communicator.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import net.fina.common.client.constants.*;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.SortField;
import net.fina.common.shared.comunicator.CommunicatorAttachmentModel;
import net.fina.common.shared.comunicator.CommunicatorNotificationFullModel;
import net.fina.common.shared.comunicator.NotificationToUserModel;
import net.fina.common.shared.comunicator.NotificationUserModel;
import net.fina.common.shared.user.UserModelSimple;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.communicator.api.CommunicatorLocal;
import net.fina.server.communicator.entity.CommunicatorAttachment;
import net.fina.server.communicator.entity.CommunicatorNotification;
import net.fina.server.communicator.entity.CommunicatorNotificationUser;
import net.fina.server.communicator.entity.UserNotificationId;
import net.fina.server.communicator.event.notification.CommunicatorNotificationEvent;
import net.fina.server.communicator.model.CommunicatorModelHelper;
import net.fina.server.util.SortUtil;
import net.fina.server.security.api.UserLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.*;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class CommunicatorNotificationProxySession {
    @Inject
    private CommunicatorLocal communicatorLocal;

    @Inject
    private UserLocal userLocal;

    @Inject
    private Event<CommunicatorNotificationEvent> communicatorNotificationEvent;


    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_REVIEW)
    public PaginatedListWrapper<CommunicatorNotificationFullModel> loadAll(int start, int limit, Map<MessageFilter, String> filterMap) {
        List<CommunicatorNotificationFullModel> result = CommunicatorModelHelper.toNotificationsModels(communicatorLocal.loadCommunicatorNotifications(filterMap, start, limit), ThreadLocalHolder.getLanguage().getId());
        return new PaginatedListWrapper<>(result, limit, communicatorLocal.countNotifications(filterMap));
    }

    public int countNotifications(Map<MessageFilter, String> filterMap) {
        return (int) communicatorLocal.countNotifications(filterMap);
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_REVIEW)
    public PaginatedListWrapper<NotificationUserModel> loadCommunicatorNotificationUsers(long notificationId, int offset, int limit, Map<MessageFilter, String> filterMap, String sortField, String sortDir) {
        List<NotificationUserModel> modelList = new ArrayList<>();
        NotificationUserModel model;
        long langId = ThreadLocalHolder.getLanguage().getId();

        SortField sortFieldModel = SortUtil.constructSortField(sortField, sortDir);

        List<CommunicatorNotificationUser> notificationUsers = communicatorLocal.loadCommunicationUsers(notificationId, offset, limit, filterMap, sortFieldModel);
        for (CommunicatorNotificationUser communicatorNotificationUsermessageUser : notificationUsers) {
            model = new NotificationUserModel();
            model.setId(communicatorNotificationUsermessageUser.getUserNotificationId().getUser().getId());
            model.setName(communicatorNotificationUsermessageUser.getUserNotificationId().getUser().getDescription().getDescription(langId));
            model.setLogin(communicatorNotificationUsermessageUser.getUserNotificationId().getUser().getLogin());
            model.setStatus(communicatorNotificationUsermessageUser.getStatus());
            model.setMessageId(notificationId);
            model.setReadDate(communicatorNotificationUsermessageUser.getReadDate());
            modelList.add(model);
        }

        if (sortFieldModel == null) {
            Collections.sort(modelList, Comparator.comparing(o -> o.getLogin().toLowerCase()));
        }

        return new PaginatedListWrapper<>(modelList, limit, communicatorLocal.countNotificationUsers(notificationId, filterMap));
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_DELETE)
    public void deleteCommunicatorNotification(long notificationId) {
        communicatorLocal.deleteNotification(notificationId);
        updateCommunicatorNotificationClient(new ArrayList<>());
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_DELETE)
    public void deleteCommunicatorNotifications(List<Long> notificationIds) {
        communicatorLocal.deleteNotifications(notificationIds);
        updateCommunicatorNotificationClient(new ArrayList<>());
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_AMEND)
    public void publishCommunicatorNotification(long notificationId) throws FinATypeException {
        if (!notificationsHaveRecipient(Collections.singletonList(notificationId))) {
            throw new FinATypeException(FinATypeException.Type.NO_RECIPIENT);
        }
        communicatorLocal.publishCommunicatorNotification(notificationId);
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_AMEND)
    public void publishCommunicatorNotifications(List<Long> notificationIds) throws FinATypeException {
        if (!notificationsHaveRecipient(notificationIds)) {
            throw new FinATypeException(FinATypeException.Type.NO_RECIPIENT);
        }
        for (Long notificationId : notificationIds) {
            if (notificationId != null) {
                communicatorLocal.publishCommunicatorNotification(notificationId);
            }
        }
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_AMEND)
    public CommunicatorNotificationFullModel saveCommunicatorNotification(CommunicatorNotificationFullModel model, List<CommunicatorAttachment> attachments) throws Exception {
        HttpServletRequest localRequest = ThreadLocalHolder.getThreadLocalRequest();
        boolean hasAcceptPermission = localRequest.isUserInRole(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_ACCEPT);

        if (model.getStatus() == CommunicatorNotificationStatus.PUBLISHED && !hasAcceptPermission) {
            throw new FinATypeException("User Does not have publish permission");
        }
        if (model.getStatus() == CommunicatorNotificationStatus.PUBLISHED && (model.getUserIds() == null || model.getUserIds().isEmpty())) {
            throw new FinATypeException("Recipients is empty!");
        }

        CommunicatorNotification notification = new CommunicatorNotification();
        notification.setId(model.getId());
        notification.setTitle(model.getTitle());
        notification.setContent(model.getContent());
        notification.setCreationDate(model.getId() > 0 ? model.getCreationDate() : new Date());
        if (model.getStatus() == null) {
            if (hasAcceptPermission) {
                notification.setStatus(CommunicatorNotificationStatus.CREATED);
            } else {
                notification.setStatus(CommunicatorNotificationStatus.PENDING);
            }
        } else {
            notification.setStatus(model.getStatus());
        }
        notification.setPublishDate(model.getStatus() == CommunicatorNotificationStatus.PUBLISHED && model.getPublishDate() == null ? new Date() : model.getPublishDate());
        notification.setNotificationUsers(null);
        notification.setSign(model.getSign());

        if (model.getUser() != null) {
            notification.setUser(userLocal.findUserbyLogin(model.getUser()));
        }
        if ((model.getUserIds() == null || model.getUserIds().isEmpty()) && model.getStatus() != CommunicatorNotificationStatus.PUBLISHED && model.getId() > 0) {
            notification.setNotificationUsers(communicatorLocal.loadCommunicationUsers(notification.getId()));
        }

        List<String> destinationUsers = new ArrayList<>();
        if (model.getUserIds() != null) {
            List<CommunicatorNotificationUser> userList = new ArrayList<>();
            for (long id : model.getUserIds()) {
                UserNotificationId userNotificationId = new UserNotificationId();

                userNotificationId.setUser(userLocal.findUserbyId(id));
                userNotificationId.setNotification(notification);

                CommunicatorNotificationUser communicatorNotificationUser = new CommunicatorNotificationUser();
                communicatorNotificationUser.setUserNotificationId(userNotificationId);
                communicatorNotificationUser.setStatus(notification.getStatus() == null || notification.getStatus() == CommunicatorNotificationStatus.CREATED || notification.getStatus() == CommunicatorNotificationStatus.PENDING ? CommunicatorReadStatus.PENDING : CommunicatorReadStatus.SENT);

                userList.add(communicatorNotificationUser);
                destinationUsers.add(userNotificationId.getUser().getLogin());
            }

            notification.setNotificationUsers(userList);
//            communicatorLocal.saveNotificationUsers(userList);
        }

        if (model.getId() > 0) {
            communicatorLocal.updateAttachments(model.getAttachments(), model.getId());
        }

        try {
            notification = communicatorLocal.saveNotificationWithAttachments(notification, attachments, model.getUserIds());
        } catch (Throwable t) {
            throw new FinATypeException(t.getMessage());
        }


        CommunicatorNotificationFullModel result = CommunicatorModelHelper.convertNotificationToModel(notification, ThreadLocalHolder.getLanguage().getId());

        updateCommunicatorNotificationClient(destinationUsers);

        return result;
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_REVIEW)
    public PaginatedListWrapper<NotificationToUserModel> loadUserNotifications(int start, int limit, SortField sortField) {
        long currentUserId = userLocal.getCurrentUserId();
        List<NotificationToUserModel> models = communicatorLocal.loadAll(start, limit, sortField).stream()
                .map(n -> {
                    NotificationToUserModel m = CommunicatorModelHelper.convertNotificationToCurrentUserToModel(n, currentUserId);
                    m.setAttachments(getMessageAttachments(n.getId(), CommunicatorAttachmentType.NOTIFICATION));
                    return m;
                })
                .collect(Collectors.toList());

        return new PaginatedListWrapper<>(models, limit, communicatorLocal.countUserNotifications());
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_REVIEW)
    public List<NotificationToUserModel> loadUserNotReadNotifications() {
        long currentUserId = userLocal.getCurrentUserId();
        return communicatorLocal.loadUserNotReadNotifications().stream()
                .map(n -> {
                    NotificationToUserModel m = CommunicatorModelHelper.convertNotificationToCurrentUserToModel(n, currentUserId);
                    m.setAttachments(getMessageAttachments(n.getId(), CommunicatorAttachmentType.NOTIFICATION));
                    return m;
                })
                .collect(Collectors.toList());
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_REVIEW)
    public void submitReadNotification(long id) {
        communicatorLocal.submitReadNotification(id);
    }

    @RolesAllowed(PermissionIdNames.FINA_COMMUNICATOR_NOTIFICATIONS_REVIEW)
    public Collection<CommunicatorAttachmentModel> getMessageAttachments(long messageId, CommunicatorAttachmentType type) {
        //TODO check user has access to messageId
        return communicatorLocal.getAttachmentsByMessageId(messageId, type, false)
                .stream().map(item -> CommunicatorModelHelper.getAttachmentModel(item, false)).collect(Collectors.toList());
    }

    private void updateCommunicatorNotificationClient(List<String> destinationUsers) {
        CommunicatorNotificationEvent event = new CommunicatorNotificationEvent(userLocal.getCurrentUserLogin(), destinationUsers);
        this.communicatorNotificationEvent.fire(event);
    }


    public List<Long> loadNotificationRecipientUserIds(long notificationId) {
        return communicatorLocal.loadNotificationRecipientUserIds(notificationId);
    }

    public List<UserModelSimple> loadNotificationRecipients(long notificationId) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return communicatorLocal.loadNotificationRecipients(notificationId).stream().map(u -> new UserModelSimple(u.getId(), u.getLogin(), u.getDescription().getDescription(langId))).collect(Collectors.toList());
    }


    private boolean notificationsHaveRecipient(List<Long> notificationIds) {
        for (Long notificationId : notificationIds) {
            if (communicatorLocal.loadCommunicationUsers(notificationId).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
