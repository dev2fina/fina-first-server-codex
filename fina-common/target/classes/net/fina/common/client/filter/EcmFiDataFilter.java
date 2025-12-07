package net.fina.common.client.filter;

/**
 * Created by baaka on 9/10/2015.
 */
public enum EcmFiDataFilter {
    FI_TYPE("FI Type"),
    FI("FI"),
    FI_MANAGEMENT("FI Management"),
    FI_LICENSE("FI License"),
    LICENSE_TYPE("License Type"),
    MANAGEMENT_TYPE("Management Type");

    private String code;

    EcmFiDataFilter(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static EcmFiDataFilter getFilter(int ordinal) {
        try {
            return values()[ordinal];
        } catch (Exception ex) {
            return null;
        }
    }
}
