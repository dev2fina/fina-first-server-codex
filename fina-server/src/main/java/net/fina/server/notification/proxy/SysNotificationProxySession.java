package net.fina.server.notification.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.notification.NotificationFilter;
import net.fina.common.shared.notification.SysNotificationMetaModel;
import net.fina.messages.MessagesUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.notification.api.SysNotificationLocal;
import net.fina.server.notification.entity.SystemNotification;
import net.fina.server.notification.event.NotificationEvent;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.fina.server.notification.model.NotificationMetaHelper.get;

@Stateless
public class SysNotificationProxySession {

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @EJB
    private SysNotificationLocal sysNotificationLocal;

    @EJB
    private UserLocal userLocal;

    public PaginatedListWrapper<SysNotificationMetaModel> loadNotifications(int offset, int limit, NotificationFilter filter) {
        String langCode = ThreadLocalHolder.getLanguage().getCode();
        long userId = userLocal.getCurrentUserId();

        List<SysNotificationMetaModel> notifications = findByUserId(userId, offset, limit, filter);
        notifications.forEach(notification -> notification.setNotification(getInitializedNotificationText(notification.getNotification(), langCode)));

        return new PaginatedListWrapper<>(notifications, limit, countNotificationsByUserId(userId, filter));
    }

    public SysNotificationMetaModel save(SysNotificationMetaModel model) {
        SystemNotification systemNotification = sysNotificationLocal.save(get(model));
        notificationEvent.fire(new NotificationEvent(userLocal.findUserbyId(model.getNotify()).getLogin()));
        return get(systemNotification);
    }

    public SysNotificationMetaModel save(SysNotificationMetaModel model, String userLogin) {
        model.setNotify(userLocal.getUserIdByLogin(userLogin));
        SystemNotification systemNotification = sysNotificationLocal.save(get(model));
        notificationEvent.fire(new NotificationEvent(userLogin));
        return get(systemNotification);
    }

    public List<SysNotificationMetaModel> findByUserId(long userId, int offset, int limit, NotificationFilter filter) {
        return get(sysNotificationLocal.findByUserId(userId, offset, limit, filter));
    }

    public long countNotificationsByUserId(long userId, NotificationFilter filter) {
        return sysNotificationLocal.countNotifications(userId, filter);
    }

    public long countUnreadNotificationsByUserId(long userId) {
        return sysNotificationLocal.countUnreadNotifications(userId);
    }

    public long countUnreadNotificationsByCurrentUser() {
        return sysNotificationLocal.countUnreadNotifications(userLocal.getCurrentUserId());
    }

    public void markNotificationAsRead(long notificationId, String userLogin) {
        sysNotificationLocal.markNotificationAsRead(notificationId, userLocal.getUserIdByLogin(userLogin));
    }

    public void markAllNotificationsAsRead(String userLogin) {
        sysNotificationLocal.markAllNotificationsAsRead(userLocal.getUserIdByLogin(userLogin));
    }

    public void notifyUsers(List<User> users, String content) {
        if (users != null) {
            for (User user : users) {
                SystemNotification systemNotification = new SystemNotification();
                systemNotification.setNotify(user.getId());
                systemNotification.setNotification(content);
                systemNotification.setDatetimeAdded(new Date());

                sysNotificationLocal.save(systemNotification);
                notificationEvent.fire(new NotificationEvent(user.getLogin()));
            }
        }
    }

    private String getInitializedNotificationText(String notification, String langCode) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(notification);
            String template = MessagesUtil.getString(jsonNode.get("templateRef").asText(), langCode);

            JsonNode templateParams = jsonNode.get("templateParams");
            if (templateParams.isArray()) {
                int i = 0;
                for (final JsonNode objNode : templateParams) {
                    template = template.replace("{" + i + "}", MessagesUtil.getString(objNode.asText(), langCode));
                    i++;
                }
                return template;
            }
        } catch (Throwable ignored) {
        }

        return notification;
    }

    public Map<String, Long> countNotificationsByType() {
        Map<String, Long> result = new HashMap<>();
        long userId = userLocal.getCurrentUserId();

        long unreadCount = sysNotificationLocal.countNotifications(userId, NotificationFilter.UNREAD);
        long readCount = sysNotificationLocal.countNotifications(userId, NotificationFilter.READ);

        result.put(NotificationFilter.UNREAD.name(), unreadCount);
        result.put(NotificationFilter.READ.name(), readCount);
        result.put(NotificationFilter.ALL.name(), readCount + unreadCount);

        return result;
    }
}
