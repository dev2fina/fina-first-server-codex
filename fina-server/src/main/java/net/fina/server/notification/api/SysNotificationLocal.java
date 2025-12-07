package net.fina.server.notification.api;

import net.fina.common.shared.notification.NotificationFilter;
import net.fina.server.notification.entity.SystemNotification;

import java.util.List;

public interface SysNotificationLocal {

    SystemNotification save(SystemNotification systemNotification);

    List<SystemNotification> findByUserId(long userId, int offset, int limit, NotificationFilter filter);

    long countUnreadNotifications(long userId);

    long countNotifications(long userId, NotificationFilter filter);

    void markNotificationAsRead(long notificationId, long userId);

    void markAllNotificationsAsRead(long userId);
}
