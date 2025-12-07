package net.fina.common.client.returns;

public enum ReturnTableType {
    UNKNOWN, MCT, NT, VCT;

    public static String getType(Integer ordinal) {
        String code = null;
        switch (ordinal) {
            case 1: {
                code = "MCT";
                break;
            }
            case 2: {
                code = "NT";
                break;
            }
            case 3: {
                code = "VCT";
                break;
            }
            default:
                code = "UNKNOWN";
                break;
        }
        return code;
    }

    public static Integer getTypeOrdinary(String code) {
        if (code.equals("MCT")) {
            return 1;
        } else if (code.equals("NT")) {
            return 2;
        } else if (code.equals("VCT")) {
            return 3;
        } else {
            return 0;
        }
    }

    public static ReturnTableType getReturnTableType(Integer ordinal) {
        ReturnTableType object = null;
        switch (ordinal) {
            case 1: {
                object = ReturnTableType.MCT;
                break;
            }
            case 2: {
                object = ReturnTableType.NT;
                break;
            }
            case 3: {
                object = ReturnTableType.VCT;
                break;
            }
            default:
                object = ReturnTableType.UNKNOWN;
                break;
        }
        return object;
    }
}
