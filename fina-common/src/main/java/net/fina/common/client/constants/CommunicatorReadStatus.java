package net.fina.common.client.constants;

public enum CommunicatorReadStatus {
    READ("net.dcs.communicator.notifications.read"),
    SENT("net.dcs.communicator.notifications.sent"),
    PENDING("net.dcs.communicator.notifications.pending"),
    RECEIVED("net.dcs.communicator.notifications.replied"),
    REJECTED("net.dcs.communicator.notifications.rejected");

    private String bundleCode;

    CommunicatorReadStatus(String bundleCode) {
        this.bundleCode = bundleCode;
    }

    public String getBundleCode() {
        return bundleCode;
    }

    public void setBundleCode(String bundleCode) {
        this.bundleCode = bundleCode;
    }
}
