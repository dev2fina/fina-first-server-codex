package net.fina.common.client.mdt;

import java.io.Serializable;

/**
 * Enumeration for Meta Data Tree node types
 *
 * @author David Chokhonelidze
 * @version 0.1
 */
public enum MDTNodeTypes implements Serializable {
    /**
     * When system can't identify node type
     */
    UNKNOWN("fina.mdt.unknown"),
    /**
     * When Meta Data Tree element is only node
     */
    NODE("fina.mdt.node"),
    /**
     * When Meta Data Tree element is only input
     */
    INPUT("fina.mdt.input"),
    /**
     * When Meta Data Tree element is only variable
     */
    VARIABLE("fina.mdt.variable"),

    /**
     * List element type
     */
    LIST("fina.mdt.list"),

    /**
     * List data element type
     */
    DATA("fina.mdt.data");

    private String code;

    private MDTNodeTypes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
