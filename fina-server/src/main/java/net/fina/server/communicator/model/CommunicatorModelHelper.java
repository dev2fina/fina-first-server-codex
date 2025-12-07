package net.fina.server.communicator.model;

import net.fina.common.client.constants.CommunicatorAttachmentType;
import net.fina.common.client.constants.CommunicatorReadStatus;
import net.fina.common.shared.comunicator.CommunicatorAttachmentModel;
import net.fina.common.shared.comunicator.CommunicatorMessageModel;
import net.fina.common.shared.comunicator.CommunicatorNotificationFullModel;
import net.fina.common.shared.comunicator.NotificationToUserModel;
import net.fina.server.communicator.entity.CommunicatorAttachment;
import net.fina.server.communicator.entity.CommunicatorMessage;
import net.fina.server.communicator.entity.CommunicatorNotification;
import net.fina.server.communicator.entity.CommunicatorNotificationUser;
import net.fina.server.security.entity.User;
import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CommunicatorModelHelper {

    public static CommunicatorAttachmentModel getAttachmentModel(CommunicatorAttachment attachment, boolean withContent) {
        CommunicatorAttachmentModel model = new CommunicatorAttachmentModel();
        model.setName(attachment.getFileName());
        model.setId(attachment.getId());
        model.setMessageId(attachment.getMessageId());
        model.setContentLength(FileUtils.byteCountToDisplaySize(attachment.getContentSize()));
        if (withContent) {
            model.setContent(attachment.getContent());
        }
        return model;
    }

    public static CommunicatorAttachment getAttachmentEntity(CommunicatorAttachmentModel model, CommunicatorAttachmentType type) {
        CommunicatorAttachment attachment = new CommunicatorAttachment();
        attachment.setId(model.getId());
        attachment.setMessageId(model.getMessageId());
        attachment.setFileName(model.getName());
        attachment.setType(type);
        attachment.setContent(model.getContent());
        attachment.setContentSize(model.getContent() != null ? model.getContent().length : 0);
        return attachment;
    }

    public static CommunicatorMessageModel getMessageModel(CommunicatorMessage message, long langId) {
        CommunicatorMessageModel model = new CommunicatorMessageModel();
        model.setId(message.getId());
        model.setReplyToId(message.getReplyToId());
        model.setReplyToUserId(message.getReplyToUserId());
        model.setTitle(message.getTitle());
        model.setContent(message.getContent());
        model.setCreationDate(message.getCreationDate());
        model.setSendDate(message.getSendDate());
        model.setStatus(message.getStatus());
        model.setSign(message.getSign());
        model.setRejectionNote(message.getRejectionNote());
        model.setUserLogin(message.getUser() != null ? message.getUser().getLogin() : null);
        model.setLastConversationMessageDate(message.getLastConversationMessageDate());
        model.setUserId(message.getUser() != null ? message.getUser().getId() : null);
        model.setHasAttachments(message.isHasAttachments());
        model.setReadDate(message.getRecipientReadDate());
        model.setMarkType(message.getMarkType());
        User author = message.getUser();
        String i18nUserName = (author.getDescription() != null && author.getDescription().getDescription(langId) != null && !author.getDescription().getDescription(langId).trim().isEmpty()) ? author.getDescription().getDescription(langId) : model.getUser();
        model.setUser(i18nUserName);
        return model;
    }

    public static CommunicatorMessage getMessageEntity(CommunicatorMessageModel model) {
        CommunicatorMessage entity = new CommunicatorMessage();
        entity.setReplyToId(model.getReplyToId());
        entity.setCreationDate(new Date());
        entity.setSendDate(new Date());
        entity.setContent(model.getContent());
        entity.setTitle(model.getTitle());
        return entity;
    }

    public static NotificationToUserModel convertNotificationToCurrentUserToModel(CommunicatorNotification entity, long userId) {
        NotificationToUserModel model = new NotificationToUserModel();
        model.setContent(entity.getContent());
        model.setId(entity.getId());
        model.setPublishDate(entity.getPublishDate());
        model.setStatus(CommunicatorReadStatus.SENT);
        model.setFrom(entity.getUser().getLogin());
        model.setTitle(entity.getTitle());

        for (CommunicatorNotificationUser communicatorNotificationUser : entity.getNotificationUsers()) {
            if (userId == communicatorNotificationUser.getUserNotificationId().getUser().getId()) {
                model.setReadStatus(communicatorNotificationUser.getStatus());
            }
        }
        return model;
    }

    public static CommunicatorNotificationFullModel convertNotificationToModel(CommunicatorNotification notification, long langId) {
        CommunicatorNotificationFullModel model = new CommunicatorNotificationFullModel();
        model.setId(notification.getId());
        model.setTitle(notification.getTitle());
        model.setContent(notification.getContent());
        model.setCreationDate(notification.getCreationDate());
        model.setPublishDate(notification.getPublishDate());
        model.setStatus(notification.getStatus());
        model.setRecipientCount(notification.getNotificationUsers().size());

        User author = notification.getUser();
        String authorDescription = null;
        if (author != null) {
            model.setUser(author.getLogin());
            model.setUserId(author.getId());

            if (author.getDescription() != null && author.getDescription().getDescription(langId) != null && !author.getDescription().getDescription(langId).trim().isEmpty()) {
                authorDescription = author.getDescription().getDescription(langId);
            } else {
                authorDescription = author.getLogin();
            }
        }
        model.setUserDescription(authorDescription);
        model.setSign(notification.getSign());
        model.setHasAttachments(notification.isHasAttachments());

        return model;
    }

    public static List<CommunicatorNotificationFullModel> toNotificationsModels(List<CommunicatorNotification> notifications, long langId) {
        return notifications.stream().map(n -> convertNotificationToModel(n, langId)).collect(Collectors.toList());
    }

    public static List<CommunicatorAttachment> getAttachmentEntities(Collection<CommunicatorAttachmentModel> attachments, CommunicatorAttachmentType type) {
        if (attachments != null) {
            return attachments.stream().map(at -> getAttachmentEntity(at, type)).toList();
        }
        return new ArrayList<>();
    }
}
