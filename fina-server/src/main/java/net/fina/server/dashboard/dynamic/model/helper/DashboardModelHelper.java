package net.fina.server.dashboard.dynamic.model.helper;

import net.fina.server.dashboard.dynamic.entity.Dashboard;
import net.fina.server.dashboard.dynamic.model.DashboardModel;
import net.fina.server.i18n.helper.Description;

import java.util.List;
import java.util.stream.Collectors;

import static net.fina.server.dashboard.dynamic.model.helper.DashletModelHelper.toDashletList;

public class DashboardModelHelper {
    public static DashboardModel toModel(Dashboard dashboard, long langId) {
        DashboardModel result = new DashboardModel();
        result.setId(dashboard.getId());
        result.setName(dashboard.getName().getDescription(langId));
        result.setNameStrId(dashboard.getName().getNameStrId());
        result.setDefault(dashboard.isIsDefault());
        result.setColumnSize(dashboard.getColumnSize());
        result.setDashletList(toDashletList(dashboard.getDashletList(), langId));
        result.setConfigJson(dashboard.getConfigJson());
        result.setCode(dashboard.getCode());
        return result;

    }

    public static Dashboard toEntity(DashboardModel dashboard, long langId) {
        Dashboard result = new Dashboard();
        result.setId(dashboard.getId());
        result.setName(new Description(langId, dashboard.getNameStrId(), dashboard.getName()));
        result.setColumnSize(dashboard.getColumnSize());
        result.setIsDefault(dashboard.isDefault());
        result.setDashletList(DashletModelHelper.toDashletEntityList(dashboard.getDashletList(), langId));
        result.setConfigJson(dashboard.getConfigJson());
        result.setCode(dashboard.getCode());

        return result;
    }


    public static List<DashboardModel> toDashboardList(List<Dashboard> dashboardModels, long langID) {
        return dashboardModels.stream().map(d -> toModel(d, langID)).collect(Collectors.toList());
    }
}
