package net.fina.first.ecm.notification.api;

import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.notification.model.NotificationInfoMetaModel;

import java.util.List;

public interface NotificationInfoLocal {

    List<NotificationInfoMetaModel> load();

    void save(NotificationInfoMetaModel model);

    void delete(String notificationId) throws NodeException;

    void delete(AlfrescoClient client, String notificationId);

    void deleteNotificationsForAction(String actionId, String userLogin);

    void updateActionNotifications(String acceptLanguage, String fiRegistryId, String actionId, String userLogin, boolean isPausedOnGap) throws NodeException;

    void markAsSent(String notificationId) throws NodeException;

    void changeActionNotificationsAddressee(String actionId, String addresseeLogin) throws NodeException;
}
