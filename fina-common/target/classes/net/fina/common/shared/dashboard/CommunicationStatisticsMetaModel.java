package net.fina.common.shared.dashboard;

public class CommunicationStatisticsMetaModel {

    private long incomingNotifications;
    private long outgoingNotifications;
    private long incomingMessages;
    private long outgoingMessages;

    public CommunicationStatisticsMetaModel() {}

    public long getIncomingNotifications() {
        return incomingNotifications;
    }

    public void setIncomingNotifications(long incomingNotifications) {
        this.incomingNotifications = incomingNotifications;
    }

    public long getOutgoingNotifications() {
        return outgoingNotifications;
    }

    public void setOutgoingNotifications(long outgoingNotifications) {
        this.outgoingNotifications = outgoingNotifications;
    }

    public long getIncomingMessages() {
        return incomingMessages;
    }

    public void setIncomingMessages(long incomingMessages) {
        this.incomingMessages = incomingMessages;
    }

    public long getOutgoingMessages() {
        return outgoingMessages;
    }

    public void setOutgoingMessages(long outgoingMessages) {
        this.outgoingMessages = outgoingMessages;
    }

}
