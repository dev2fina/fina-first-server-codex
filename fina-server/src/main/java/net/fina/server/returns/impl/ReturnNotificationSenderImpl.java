package net.fina.server.returns.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.fina.common.client.constants.CommunicatorNotificationStatus;
import net.fina.common.client.constants.CommunicatorReadStatus;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.CommonUtil;
import net.fina.messages.MessagesUtil;
import net.fina.server.communicator.api.CommunicatorLocal;
import net.fina.server.communicator.entity.CommunicatorNotification;
import net.fina.server.communicator.entity.CommunicatorNotificationUser;
import net.fina.server.communicator.entity.UserNotificationId;
import net.fina.server.communicator.event.notification.CommunicatorNotificationEvent;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.returns.api.ReturnNotificationSender;
import net.fina.server.returns.qualifier.ReturnNotificationQualifier;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Stateless
@Local(ReturnNotificationSender.class)
@ReturnNotificationQualifier(value = "notification")
public class ReturnNotificationSenderImpl implements ReturnNotificationSender {
    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private UserLocal userLocal;

    @Inject
    private CommunicatorLocal communicatorLocal;

    @Inject
    private Event<CommunicatorNotificationEvent> communicatorNotificationEvent;


    @Override
    public void send(List<UploadFile> uploadFiles, String returnStatusBundleText, String note, String langCode) {
        if (uploadFiles == null || uploadFiles.isEmpty()) {
            return;
        }
        try {

            User currUser = userLocal.getCurrentUser();

            String notificationTitle = MessagesUtil.getString("net.fina.return.status.notification.title", langCode);
            String notificationContent = MessagesUtil.getString("net.fina.return.status.notification.content", langCode);
            String returnStatus = MessagesUtil.getString(returnStatusBundleText, langCode);

            if (note != null && !note.isEmpty()) {
                String noteI18n = MessagesUtil.getString("net.fina.dcs.importStatus.note", langCode);
                notificationContent = String.format("%s<br><br>%s: %s", notificationContent, noteI18n, note);
            }

            List<String> destinationUsers = new ArrayList<>();

            for (UploadFile uploadFile : uploadFiles) {
                String userLogin = uploadFile.getUser().getLogin();
                destinationUsers.add(userLogin);


                log.info("sending notification to user [" + userLogin + "]");

                CommunicatorNotification notification = constructReturnStatusChangeNotification(currUser, uploadFile, notificationTitle, notificationContent, returnStatus, note);

                try {
                    communicatorLocal.saveNotification(notification);
                } catch (FinATypeException e) {
                    log.error(e.getMessage(), e);
                }

            }
            communicatorNotificationEvent.fire(new CommunicatorNotificationEvent(currUser.getLogin(), destinationUsers));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }


    private CommunicatorNotification constructReturnStatusChangeNotification(User currUser, UploadFile uploadFile, String title, String content, String returnStatus, String note) {
        CommunicatorNotification notification = new CommunicatorNotification();

        notification.setUser(currUser);
        notification.setStatus(CommunicatorNotificationStatus.PUBLISHED);
        notification.setCreationDate(new Date());
        notification.setPublishDate(new Date());
        notification.setTitle(title);
        notification.setContent(CommonUtil.compileMessageWithParams(content, uploadFile.getFileName(), uploadFile.getUploadedTime().toString(), returnStatus, note));

        UserNotificationId userNotificationId = new UserNotificationId();
        userNotificationId.setNotification(notification);
        userNotificationId.setUser(uploadFile.getUser());

        CommunicatorNotificationUser nUser = new CommunicatorNotificationUser();
        nUser.setStatus(CommunicatorReadStatus.SENT);
        nUser.setUserNotificationId(userNotificationId);

        notification.setNotificationUsers(Collections.singletonList(nUser));

        return notification;
    }

}
