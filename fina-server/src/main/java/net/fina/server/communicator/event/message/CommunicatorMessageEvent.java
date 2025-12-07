package net.fina.server.communicator.event.message;

import net.fina.server.communicator.event.CommunicatorEvent;
import net.fina.server.communicator.event.type.CommunicatorMessageEventType;

import java.util.List;

public class CommunicatorMessageEvent extends CommunicatorEvent {

    private long rootMessageId;

    private boolean isNewRootMessage;

    private List<String> destinationUsers;

    private CommunicatorMessageEventType type;

    public CommunicatorMessageEvent(String author) {
        super(author);
    }

    public CommunicatorMessageEvent(String author, List<String> destinationUsers, long rootMessageId, boolean isNewRootMessage) {
        super(author);
        this.destinationUsers = destinationUsers;
        this.rootMessageId = rootMessageId;
        this.isNewRootMessage = isNewRootMessage;
    }

    public CommunicatorMessageEvent(String author, long rootMessageId, CommunicatorMessageEventType type) {
        super(author);
        this.type = type;
        this.rootMessageId = rootMessageId;
    }

    public List<String> getDestinationUsers() {
        return destinationUsers;
    }

    public void setDestinationUsers(List<String> destinationUsers) {
        this.destinationUsers = destinationUsers;
    }

    public long getRootMessageId() {
        return rootMessageId;
    }

    public void setRootMessageId(long rootMessageId) {
        this.rootMessageId = rootMessageId;
    }

    public boolean isNewRootMessage() {
        return isNewRootMessage;
    }

    public void setNewRootMessage(boolean newRootMessage) {
        isNewRootMessage = newRootMessage;
    }

    public CommunicatorMessageEventType getType() {
        return type;
    }

    public void setType(CommunicatorMessageEventType type) {
        this.type = type;
    }

}
