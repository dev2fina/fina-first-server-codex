package net.fina.server.util;

import org.jboss.logging.Logger;

import jakarta.jms.ObjectMessage;

public class MdbMessageLoggerUtil {
    public static String getMessageInfo(Logger log, ObjectMessage objectMessage) {
        try {
            return "JMS: " + objectMessage.getJMSDestination() + ", message:" + objectMessage.getJMSMessageID() + ", redelivered:" + objectMessage.getJMSRedelivered() + ", delivery count:" + objectMessage.getIntProperty("JMSXDeliveryCount") + ", Object:" + objectMessage.getObject().toString();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return "JMS message info is null";
    }
}
