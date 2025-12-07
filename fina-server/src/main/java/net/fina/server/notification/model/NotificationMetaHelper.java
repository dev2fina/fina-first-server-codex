package net.fina.server.notification.model;

import net.fina.common.shared.notification.SysNotificationMetaModel;
import net.fina.server.notification.entity.SystemNotification;

import java.util.ArrayList;
import java.util.List;

public class NotificationMetaHelper {

    public static SysNotificationMetaModel get(SystemNotification systemNotification) {
        SysNotificationMetaModel model = new SysNotificationMetaModel();
        model.setId(systemNotification.getId());
        model.setNotify(systemNotification.getNotify());
        model.setNotification(systemNotification.getNotification());
        model.setDatetimeAdded(systemNotification.getDatetimeAdded());
        model.setDatetimeRead(systemNotification.getDatetimeRead());
        return model;
    }

    public static SystemNotification get(SysNotificationMetaModel model) {
        SystemNotification systemNotification = new SystemNotification();
        systemNotification.setId(model.getId());
        systemNotification.setNotify(model.getNotify());
        systemNotification.setNotification(model.getNotification());
        systemNotification.setDatetimeAdded(model.getDatetimeAdded());
        systemNotification.setDatetimeRead(model.getDatetimeRead());
        return systemNotification;
    }

    public static List<SysNotificationMetaModel> get(List<SystemNotification> systemNotifications) {
        List<SysNotificationMetaModel> result = new ArrayList<>();
        for (SystemNotification systemNotification : systemNotifications) {
            result.add(get(systemNotification));
        }
        return result;
    }
}
