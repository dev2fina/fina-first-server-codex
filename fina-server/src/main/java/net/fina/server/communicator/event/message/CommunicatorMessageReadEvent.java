package net.fina.server.communicator.event.message;

import net.fina.server.communicator.event.CommunicatorEvent;
import net.fina.server.communicator.event.type.CommunicatorMessageEventType;

public class CommunicatorMessageReadEvent extends CommunicatorEvent {
    private long rootMessageId;
    private long userId;
    private CommunicatorMessageEventType type;


    public CommunicatorMessageReadEvent(long rootMessageId, long userId, CommunicatorMessageEventType type) {
        super("");
        this.rootMessageId = rootMessageId;
        this.userId = userId;
        this.type = type;
    }


    public long getRootMessageId() {
        return rootMessageId;
    }

    public void setRootMessageId(long rootMessageId) {
        this.rootMessageId = rootMessageId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public CommunicatorMessageEventType getType() {
        return type;
    }

    public void setType(CommunicatorMessageEventType type) {
        this.type = type;
    }
}
