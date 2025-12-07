package net.fina.server.dcs.mail.model;

import net.fina.server.dcs.mail.entity.MessageReply;

import java.util.ArrayList;
import java.util.Arrays;

public class MessageReplyMetaModelHelper {

    public static MessageReplyMetaModel toMetaModel(MessageReply mr) {
        if (mr != null) {

            MessageReplyMetaModel result = new MessageReplyMetaModel();
            result.setBcc(mr.getBcc() != null ? Arrays.asList(mr.getBcc()) : new ArrayList<>());
            result.setCc(mr.getCc() != null ? Arrays.asList(mr.getCc()) : new ArrayList<>());
            result.setContent(mr.getContent());
            result.setDate(mr.getDate());
            result.setFrom(mr.getFrom());
            result.setId(mr.getId());
            result.setSendStatus(mr.getSendStatus());
            result.setSender(mr.getSender());
            result.setSubject(mr.getSubject());
            result.setTo(mr.getTo() != null ? Arrays.asList(mr.getTo()) : new ArrayList<>());

            return result;
        }
        return null;
    }

    public static MessageReply toEntity(MessageReplyMetaModel mr) {
        if (mr != null) {

            MessageReply result = new MessageReply();
            result.setBcc(mr.getBcc().stream().toArray(String[]::new));
            result.setCc(mr.getCc().stream().toArray(String[]::new));
            result.setContent(mr.getContent());
            result.setDate(mr.getDate());
            result.setFrom(mr.getFrom());
            result.setId(mr.getId());
            result.setSendStatus(mr.getSendStatus());
            result.setSender(mr.getSender());
            result.setSubject(mr.getSubject());
            result.setTo(mr.getTo().stream().toArray(String[]::new));

            return result;
        }
        return null;
    }

}
