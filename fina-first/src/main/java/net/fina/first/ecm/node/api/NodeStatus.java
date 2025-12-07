package net.fina.first.ecm.node.api;

public enum NodeStatus {
    IN_PROGRESS("IN_PROGRESS"),
    ACCEPTED("ACCEPTED"),
    DECLINED("DECLINED"),
    CANCELED("CANCELED"),
    LIQUIDATION("LIQUIDATION"),
    GAP("GAP");

    private final String value;

    NodeStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
