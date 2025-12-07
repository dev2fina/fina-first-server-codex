package net.fina.server.dashboard.dynamic.api;

import net.fina.common.client.exception.FinATypeException;

import java.util.List;
import java.util.Map;

public interface DashletQueryExecutor {
    List<String> parseQueryColumns(String query) throws FinATypeException;

    List<Map<String, Object>> loadDashletData(String query, Map<String, Object> queryParams, int limit) throws FinATypeException;
}
