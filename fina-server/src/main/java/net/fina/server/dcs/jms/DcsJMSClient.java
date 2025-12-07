package net.fina.server.dcs.jms;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.MapMessage;
import net.fina.common.shared.jms.model.UserInfoMessage;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class DcsJMSClient {

    private final Logger log = Logger.getLogger(getClass());


    @Resource(lookup = "java:jboss/exported/jms/queue/DcsCommunicatorQueue")
    private jakarta.jms.Queue dcsCommunicatorQueue;
    @Resource(lookup = "java:jboss/exported/jms/queue/DcsFileUploadQueue")
    private jakarta.jms.Queue dcsFileUploadQueue;
    @Resource(lookup = "java:jboss/exported/jms/queue/DcsUserInfoQueue")
    private jakarta.jms.Queue dcsUserInfoQueue;
    @Inject
    private JMSContext context;

    public DcsJMSClient() {
    }


    private void connectAndSend(jakarta.jms.Queue queue, DcsJmsClientMessageCallback messageCallback) {

        try {

            context.createProducer().send(queue, messageCallback.create(context));

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    public void sendCommunicatorMessage(boolean notification, String user, List<String> destinationUsers) {
        this.connectAndSend(dcsCommunicatorQueue, context -> {
            MapMessage mapMessage = context.createMapMessage();
            mapMessage.setBoolean("notification", notification);
            mapMessage.setString("user", user);
            mapMessage.setString("destinationUsers", getDestinationUsersSerializeString(destinationUsers));
            return mapMessage;
        });
    }

    public void sendCommunicatorMessage(String user, List<String> destinationUsers, long rootMessageId, boolean isNewMessage) {
        this.connectAndSend(dcsCommunicatorQueue, context -> {
            MapMessage mapMessage = context.createMapMessage();
            mapMessage.setString("user", user);
            mapMessage.setLong("rootMessageId", rootMessageId);
            mapMessage.setBoolean("isNewRootMessage", isNewMessage);
            mapMessage.setString("destinationUsers", getDestinationUsersSerializeString(destinationUsers));
            return mapMessage;
        });
    }

    private String getDestinationUsersSerializeString(List<String> destinationUsers) {
        String s = destinationUsers.toString();
        return s.substring(1, s.length() - 1);

    }

    public void sendFileUploadMessage(Serializable message) {
        this.connectAndSend(dcsFileUploadQueue, context -> context.createObjectMessage(message));
    }

    public void sendUserInfoMessage(UserInfoMessage message) {
        this.connectAndSend(dcsUserInfoQueue, context -> {
            MapMessage mapMessage = context.createMapMessage();
            mapMessage.setString("userLogin", message.getUserLogin());
            mapMessage.setBoolean("logoutUser", message.isLogoutUser());
            return mapMessage;
        });

    }

}
