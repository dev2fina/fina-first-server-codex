package net.fina.common.client.constants;

public enum ImportStatus {

    UPLOADED("net.fina.returns.import.status.uploaded"),
    IN_PROGRESS("net.fina.returns.import.status.inProgress"),
    QUEUED("net.fina.returns.import.status.queued"),
    REJECTED("net.fina.returns.import.status.rejected"),
    IMPORTED("net.fina.returns.import.status.imported"),
    DECLINED("net.fina.returns.import.status.declined"),
    ERRORS("net.fina.returns.errors");

    private String code;

    private ImportStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Get status.
     *
     * @param ordinal status number.
     * @return status enum.
     */
    public static ImportStatus getStatus(int ordinal) {
        try {
            return values()[ordinal];
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Get status.
     *
     * @param ordinal status number string.
     * @return enum.
     */
    public static ImportStatus getStatus(String ordinal) {
        int x;
        try {
            x = Integer.parseInt(ordinal);
        } catch (Exception e) {
            return null;
        }
        return getStatus(x);
    }
}