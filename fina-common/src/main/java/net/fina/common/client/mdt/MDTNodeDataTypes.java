package net.fina.common.client.mdt;

import java.io.Serializable;

/**
 * Meta Data Tree Node data types possible values
 */
public enum MDTNodeDataTypes implements Serializable {
    /**
     * Unknown status
     */
    UNKNOWN("mdt.node.datatype.unknown"),
    /**
     * Numeric type applies on input
     */
    NUMERIC("mdt.node.datatype.numeric"),
    /**
     * Text type applies on input
     */
    TEXT("mdt.node.datatype.text"),
    /**
     * Date type applies on input
     */
    DATE("mdt.node.datatype.date"),
    /**
     * Date Time type applies on input
     */
    DATE_TIME("mdt.node.datatype.dateTime");

    private String code;

    private MDTNodeDataTypes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
