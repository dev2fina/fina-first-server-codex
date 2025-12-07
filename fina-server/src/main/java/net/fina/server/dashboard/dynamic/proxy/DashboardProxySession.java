package net.fina.server.dashboard.dynamic.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.SortField;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.dashboard.dynamic.api.DashboardLocal;
import net.fina.server.dashboard.dynamic.entity.Dashboard;
import net.fina.server.dashboard.dynamic.entity.Dashlet;
import net.fina.server.dashboard.dynamic.model.DashboardModel;
import net.fina.server.dashboard.dynamic.model.DashletModel;
import net.fina.server.dashboard.dynamic.model.filter.DashletFilter;
import net.fina.server.dashboard.dynamic.model.helper.DashboardModelHelper;
import net.fina.server.dashboard.dynamic.model.helper.DashletModelHelper;
import org.apache.commons.lang.StringUtils;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class DashboardProxySession {

    @Inject
    private DashboardLocal dashboardLocal;


    public List<DashboardModel> loadAllDashboard() {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return DashboardModelHelper.toDashboardList(dashboardLocal.loadAllDashboard(), langId);
    }

    public List<DashboardModel> loadUserDashboard() {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return DashboardModelHelper.toDashboardList(dashboardLocal.loadUserDashboards(), langId);
    }

    public DashboardModel saveDashboard(DashboardModel model) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Dashboard dashboard = dashboardLocal.saveDashboard(DashboardModelHelper.toEntity(model, langId));
        return DashboardModelHelper.toModel(dashboard, langId);
    }

    public void deleteDashboard(long dashboardId) {
        dashboardLocal.deleteDashboard(dashboardId);
    }

    @RolesAllowed(PermissionIdNames.DASHBOARD_MANAGER)
    public DashletModel saveDashlet(DashletModel model, Map<String, Object> queryParams) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();

        Dashlet dashlet = dashboardLocal.saveDashlet(DashletModelHelper.toEntity(model, langId), queryParams);
        DashletModel result = DashletModelHelper.toModel(dashlet, langId);
        //extract columns and add to filter parameters
        extractColumns(result);

        return result;
    }

    public void deleteDashlet(long dashletId) throws FinATypeException {
        dashboardLocal.deleteDashlet(dashletId);
    }

    @RolesAllowed(PermissionIdNames.DASHBOARD_MANAGER)
    public List<Map<String, Object>> previewDashletData(DashletModel model, Map<String, Object> queryParams) throws FinATypeException {
        if (StringUtils.isBlank(model.getDataQuery())) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Query is not provided!");
        }
        String query = model.getDataQuery().toLowerCase();
        int limit = 10;
        if (query.contains(" offset ") || query.contains(" top ")) {
            limit = -1;
        }
        return dashboardLocal.loadDashletData(DashletModelHelper.toEntity(model, ThreadLocalHolder.getLanguage().getId()), queryParams, limit);
    }

    public List<Map<String, Object>> loadDashletData(long dashletId, Map<String, Object> queryParams) throws FinATypeException {
        return dashboardLocal.loadDashletData(dashletId, queryParams, -1);
    }

    public PaginatedListWrapper<DashletModel> loadAllDashlet(int offsetFromPage, int limit, SortField sortField, Map<DashletFilter, String> filterStringMap) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<DashletModel> models = DashletModelHelper.toDashletList(dashboardLocal.loadDashlets(offsetFromPage, limit, sortField, filterStringMap, langId), langId);
        models.forEach(this::extractColumns);

        return new PaginatedListWrapper<>(models, limit, dashboardLocal.countDashlets(filterStringMap, langId));
    }

    @SuppressWarnings("java:S5852")
    private void extractColumns(DashletModel dashlet) {
        Pattern pattern = Pattern.compile("(?i)SELECT\\s+(.*?)\\s+FROM");
        Matcher matcher = pattern.matcher(dashlet.getDataQuery());

        if (matcher.find()) {
            String conditionsString = matcher.group(1).trim();
            String[] conditionArray = conditionsString.split(",");

            for (String condition : conditionArray) {
                String[] parts = condition.trim().split("\\s+");
                String columnName = parts[parts.length - 1];

                if (condition.toLowerCase().contains("f_")) {
                    dashlet.getFilters().add(columnName);
                } else {
                    dashlet.getDataColumns().add(columnName);
                }
            }
        }
    }
}
