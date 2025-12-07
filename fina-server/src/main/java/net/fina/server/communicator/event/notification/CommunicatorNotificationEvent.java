package net.fina.server.communicator.event.notification;

import net.fina.server.communicator.event.CommunicatorEvent;

import java.util.List;

public class CommunicatorNotificationEvent extends CommunicatorEvent {

    private List<String> destinationUsers;

    public CommunicatorNotificationEvent(String author) {
        super(author);
    }

    public CommunicatorNotificationEvent(String author, List<String> destinationUsers) {
        super(author);
        this.destinationUsers = destinationUsers;
    }

    public List<String> getDestinationUsers() {
        return destinationUsers;
    }

    public void setDestinationUsers(List<String> destinationUsers) {
        this.destinationUsers = destinationUsers;
    }
}
