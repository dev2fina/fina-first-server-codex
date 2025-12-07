package net.fina.server.communicator.impl;

import jakarta.inject.Inject;
import net.fina.server.communicator.api.CommunicatorEventConsumer;
import net.fina.server.communicator.event.message.CommunicatorMessageEvent;
import net.fina.server.communicator.event.notification.CommunicatorNotificationEvent;
import net.fina.server.dcs.jms.DcsJMSClient;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;

@Stateless
@Local(CommunicatorEventConsumer.class)
public class CommunicatorEventConsumerSession implements CommunicatorEventConsumer {
    @Inject
    private DcsJMSClient dcsJmsClient;


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void notificationEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) CommunicatorNotificationEvent notificationEvent) {
        if (notificationEvent.getAuthor() != null) {
            dcsJmsClient.sendCommunicatorMessage(true, notificationEvent.getAuthor(), notificationEvent.getDestinationUsers());
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void messageEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) CommunicatorMessageEvent messageEvent) {
        if (messageEvent.getDestinationUsers() != null) {
            dcsJmsClient.sendCommunicatorMessage(messageEvent.getAuthor(), messageEvent.getDestinationUsers(), messageEvent.getRootMessageId(), messageEvent.isNewRootMessage());
        }
    }

}
