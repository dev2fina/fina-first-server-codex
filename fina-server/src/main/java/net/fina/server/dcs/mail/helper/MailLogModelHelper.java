package net.fina.server.dcs.mail.helper;

import net.fina.common.client.constants.MailType;
import net.fina.common.client.constants.MessageReplySendStatus;
import net.fina.common.client.constants.MessageStatus;
import net.fina.common.shared.MailLogMetaModel;
import net.fina.server.dcs.mail.entity.MessageReply;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static net.fina.common.client.constants.MailType.MAIL_ROBOT;

public class MailLogModelHelper {


    public static MailLogMetaModel toMailLogModel(Object[] message) {
        MailLogMetaModel model = new MailLogMetaModel();
        model.setId((long) message[0]);
        model.setMailType(MAIL_ROBOT);
        model.setMailId((String) message[1]);
        model.setMailUser((String) message[2]);
        model.setAddress((String) message[3]);
        model.setFromAddress((String) message[4]);
        model.setStatus((MessageStatus) message[5]);
        model.setReceiveDate((Date) message[6]);
        model.setReadDate((Date) message[7]);
        model.setNote((String) message[8]);
        model.setReplyStatus((MessageReplySendStatus) message[9]);
        return model;
    }

    public static MailLogMetaModel toSystemMailModel(MessageReply reply) {
        MailLogMetaModel model = new MailLogMetaModel();
        model.setId(reply.getId());
        model.setMailId(String.valueOf(reply.getId()));
        model.setMailType(MailType.SYSTEM);
        model.setFromAddress(reply.getSender());
        model.setAddress(reply.getFrom());
        model.setMailUser(reply.getFrom());
        model.setReceiveDate(reply.getDate());
        String mailTo = generateToAddresses(reply.getTo());
        model.setToAddress(mailTo);
        model.setReplyStatus(reply.getSendStatus());

        return model;
    }

    public static List<MailLogMetaModel> toMailMogModels(List<Object[]> models){
        return models.stream().map(MailLogModelHelper::toMailLogModel).collect(Collectors.toList());
    }

    public static List<MailLogMetaModel> toSystemMailModels(List<MessageReply> models){
        return models.stream().map(MailLogModelHelper::toSystemMailModel).collect(Collectors.toList());
    }

    private static String generateToAddresses(String[] to) {
        StringBuilder sb = new StringBuilder();
        for (String mailTo : to) {
            if (sb.toString().isEmpty()) {
                sb.append(mailTo);
            } else {
                sb.append(", ").append(mailTo);
            }
        }
        return sb.toString();
    }

}
