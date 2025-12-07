package net.fina.server.dcs.mail.model;

import net.fina.server.dcs.mail.entity.Message;
import net.fina.server.dcs.uploadfile.model.UploadFileMetaModel;
import net.fina.server.dcs.uploadfile.model.helper.UploadFileModelHelper;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MessageMetaModelHelper {

    public static MessageMetaModel toMetaModel(Message message) {
        MessageMetaModel result = new MessageMetaModel();

        result.setId(message.getId());
        result.setMessageId(message.getMessageId());
        result.setMailUser(message.getMailUser());
        result.setAddress(message.getAddress());
        result.setFrom(message.getFrom());
        result.setStatus(message.getStatus());
        result.setReceivedDate(message.getReceivedDate());
        result.setReadDate(message.getReadDate());
        result.setNote(message.getNote());
        result.setSubject(message.getSubject());
        result.setMessageReply(MessageReplyMetaModelHelper.toMetaModel(message.getMessageReply()));
        result.setUploadFiles(UploadFileModelHelper.toMetaModel(new ArrayList<>(message.getUploadFiles())));

        return result;
    }

    public static Message toEntity(MessageMetaModel message) {
        Message result = new Message();

        result.setId(message.getId());
        result.setMessageId(message.getMessageId());
        result.setMailUser(message.getMailUser());
        result.setAddress(message.getAddress());
        result.setFrom(message.getFrom());
        result.setStatus(message.getStatus());
        result.setReceivedDate(message.getReceivedDate());
        result.setReadDate(message.getReadDate());
        result.setNote(message.getNote());
        result.setSubject(message.getSubject());
        result.setMessageReply(MessageReplyMetaModelHelper.toEntity(message.getMessageReply()));
        result.setUploadFiles(message.getUploadFiles()
                .stream().map(UploadFileMetaModel::getUploadFile)
                .collect(Collectors.toList()));

        return result;
    }
}
