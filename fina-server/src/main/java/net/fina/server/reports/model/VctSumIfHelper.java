package net.fina.server.reports.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VctSumIfHelper {

    public static List<VctSumIfDataItemMetaModel> extractModel(List<Object[]> databaseValues) {

        List<VctSumIfDataItemMetaModel> result = new ArrayList<>();

        if (databaseValues != null) {
            for (Object[] objects : databaseValues) {

                VctSumIfDataItemMetaModel item = new VctSumIfDataItemMetaModel();
                item.setReturnId(getLongValue(objects[0]));
                item.setTableId(getLongValue(objects[1]));
                item.setRowNumber(getLongValue(objects[2]));
                item.setnValue(getDoubleValue(objects[3]));
                item.setValue(objects[4] != null ? objects[4].toString() : null);
                item.setNodeId(getLongValue(objects[5]));

                result.add(item);
            }
        }

        return result;
    }

    private static long getLongValue(Object o) {

        if (o instanceof BigDecimal) {
            return ((BigDecimal) o).longValue();
        }

        return (long) o;
    }

    private static double getDoubleValue(Object o) {

        if (o instanceof BigDecimal) {
            return ((BigDecimal) o).doubleValue();
        }

        return (double) o;
    }
}
