package net.fina.first.ecm.notification.proxy;

import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.notification.api.NotificationInfoLocal;
import net.fina.first.ecm.notification.model.NotificationInfoMetaModel;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
public class ECMNotificationInfoProxySession {

    @Inject
    private NotificationInfoLocal notificationInfoLocal;

    public List<NotificationInfoMetaModel> load() {
        return notificationInfoLocal.load();
    }

    public void save(NotificationInfoMetaModel model) {
        notificationInfoLocal.save(model);
    }

    public void delete(String notificationId) throws NodeException {
        notificationInfoLocal.delete(notificationId);
    }

    public void deleteNotificationsForAction(String actionId, String userLogin) {
        notificationInfoLocal.deleteNotificationsForAction(actionId, userLogin);
    }

    public void updateActionNotifications(String acceptLanguage, String fiRegistryId, String actionId, String userLogin, boolean isPausedOnGap) throws NodeException {
        notificationInfoLocal.updateActionNotifications(acceptLanguage, fiRegistryId, actionId, userLogin, isPausedOnGap);
    }

    public void changeActionNotificationsAddressee(String actionId, String addresseeLogin) throws NodeException {
        notificationInfoLocal.changeActionNotificationsAddressee(actionId, addresseeLogin);
    }
}
