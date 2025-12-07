package net.fina.server.reg.api;

import java.util.List;
import java.util.Map;

public interface RegReportDataLocal {

    double cregvaluevctsumif(String tableName, String columnName, String sumColumnNane, String criterion, char operation, String criterionValue, List<Long> scheduleIds);

    double cregvaluevctsumifs(String tableName, String sumColumnName, Map<String, String> criterions, List<Long> scheduleIDs);
}
