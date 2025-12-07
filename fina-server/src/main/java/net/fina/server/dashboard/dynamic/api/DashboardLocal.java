package net.fina.server.dashboard.dynamic.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.SortField;
import net.fina.server.dashboard.dynamic.entity.Dashboard;
import net.fina.server.dashboard.dynamic.entity.Dashlet;
import net.fina.server.dashboard.dynamic.model.filter.DashletFilter;

import java.util.List;
import java.util.Map;

public interface DashboardLocal {
    List<Dashboard> loadAllDashboard();

    List<Dashboard> loadUserDashboards();

    Dashboard saveDashboard(Dashboard dashboard) throws FinATypeException;

    void deleteDashboard(long dashboardID);

    Dashlet saveDashlet(Dashlet dashlet, Map<String, Object> queryParams) throws FinATypeException;

    void deleteDashlet(long dashletId) throws FinATypeException;

    List<Map<String, Object>> loadDashletData(Dashlet dashlet, Map<String, Object> queryParams, int limit) throws FinATypeException;

    List<Map<String, Object>> loadDashletData(long dashletId, Map<String, Object> queryParams, int limit) throws FinATypeException;

    List<Dashlet> loadDashlets(int offsetFromPage, int limit, SortField sortField, Map<DashletFilter, String> filterStringMap, long langId);

    long countDashlets(Map<DashletFilter, String> filterStringMap, long langId);
}
