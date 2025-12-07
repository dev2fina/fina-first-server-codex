package net.fina.common.client.mdt;

import java.io.Serializable;

/**
 * Meta Data Tree node evaluation methods list
 *
 * @author David Chokhonelidze
 * @version 0.1
 */
public enum MDTNodeEvalMethods implements Serializable {
    /**
     * Unknown evaluation method
     */
    UNKNOWN("mdt.node.eval.method.unknown"),
    /**
     * Sum
     */
    SUM("mdt.node.eval.method.sum"),
    /**
     * Average
     */
    AVERAGE("mdt.node.eval.method.avg"),
    /**
     * Max
     */
    MAX("mdt.node.eval.method.max"),
    /**
     * Min
     */
    MIN("mdt.node.eval.method.min");

    private String code;

    private MDTNodeEvalMethods(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static String getType(Integer ordinal) {
        String code = null;
        switch (ordinal) {
            case 1: {
                code = "SUM";
                break;
            }
            case 2: {
                code = "AVERAGE";
                break;
            }
            case 3: {
                code = "MAX";
                break;
            }
            case 4: {
                code = "MIN";
                break;
            }
            default:
                code = "UNKNOWN";
                break;
        }
        return code;
    }

    public static Integer getOrdinal(String code) {
        if (code.equals("SUM")) {
            return 1;
        } else if (code.equals("AVERAGE")) {
            return 2;
        } else if (code.equals("MAX")) {
            return 3;
        } else if (code.equals("MIN")) {
            return 4;
        } else {
            return 0;
        }
    }

    public static MDTNodeEvalMethods getObject(Integer ordinal) {
        MDTNodeEvalMethods code = null;
        switch (ordinal) {
            case 1: {
                code = MDTNodeEvalMethods.SUM;
                break;
            }
            case 2: {
                code = MDTNodeEvalMethods.AVERAGE;
                break;
            }
            case 3: {
                code = MDTNodeEvalMethods.MAX;
                break;
            }
            case 4: {
                code = MDTNodeEvalMethods.MIN;
                break;
            }
            default:
                code = MDTNodeEvalMethods.UNKNOWN;
                break;
        }
        return code;
    }
}
