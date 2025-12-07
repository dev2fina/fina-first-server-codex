package net.fina.common.client.reports;

public class ReportConstants {

    public static final int NODETYPE_FOLDER = 1;
    public static final int NODETYPE_REPORT = 2;

    public static final int ITERATOR_REPORT = 1;
    public static final int RETURN_TABLE_REPORT = 2;

    public static final String LATEST_VERSION = "LATEST";

    public static final String PERIOD_ITERATOR = "Period Parameter";
    public static final String FI_PARAMETER = "Fi Parameter";
    public static final String PEER_GROUP_PARAMETER = "Peer Group Parameter";
    public static final String NODE_PARAMETER = "Node Parameter";

    public static final int BANK_PARAMETER = 1;
    public static final int PEER_PARAMETER = 2;
    public static final int NODE__PARAMETER = 3;
    public static final int PERIOD_PARAMETER = 4;

    public static String getIteratorType(Integer id) {
        String parameterType = null;
        switch (id) {

            case 1: {
                parameterType = FI_PARAMETER;
                break;
            }
            case 2: {
                parameterType = PEER_GROUP_PARAMETER;
                break;
            }
            case 3: {
                parameterType = NODE_PARAMETER;
                break;
            }
            case 4: {
                parameterType = PERIOD_ITERATOR;
                break;
            }
            default:
                break;
        }
        return parameterType;
    }
}
