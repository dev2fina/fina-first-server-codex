package net.fina.server.communicator.api;

import net.fina.server.communicator.event.message.CommunicatorMessageEvent;
import net.fina.server.communicator.event.notification.CommunicatorNotificationEvent;

public interface CommunicatorEventConsumer {

    void notificationEvent(CommunicatorNotificationEvent notificationEvent);

    void messageEvent(CommunicatorMessageEvent notificationEvent);
}
