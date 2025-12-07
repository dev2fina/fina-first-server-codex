package net.fina.common.shared.report;

public enum ReportParameterType {
    SCHEDULE(-1),
    BANK(1),
    PEER(2),
    NODE(3),
    PERIOD(4),
    OFFSET(5),
    VCT(6),
    PLAIN_VCT(7),
    VERSION(8);

    private final int typeID;

    ReportParameterType(int id) {
        this.typeID = id;
    }

    public int getTypeID() {
        return typeID;
    }

    public static ReportParameterType fromId(int id) {
        for (ReportParameterType type : values()) {
            if (type.getTypeID() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown id: " + id);
    }
}
