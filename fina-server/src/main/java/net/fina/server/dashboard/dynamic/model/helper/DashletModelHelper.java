package net.fina.server.dashboard.dynamic.model.helper;

import net.fina.server.dashboard.dynamic.entity.Dashlet;
import net.fina.server.dashboard.dynamic.model.DashletModel;
import net.fina.server.i18n.helper.Description;

import java.util.List;
import java.util.stream.Collectors;

public class DashletModelHelper {
    public static Dashlet toEntity(DashletModel dashlet, long langId) {
        Dashlet result = new Dashlet();
        result.setId(dashlet.getId());
        result.setDataQuery(dashlet.getDataQuery());
        result.setName(new Description(langId, dashlet.getNameStrId(), dashlet.getName()));
        result.setMetaInfoJson(dashlet.getMetaInfoJson());
        result.setCode(dashlet.getCode());
        return result;
    }

    public static DashletModel toModel(Dashlet dashlet, long langId) {
        DashletModel result = new DashletModel();
        result.setId(dashlet.getId());
        result.setName(dashlet.getName().getDescription(langId));
        result.setNameStrId(dashlet.getName().getNameStrId());
        result.setDataQuery(dashlet.getDataQuery());
        result.setMetaInfoJson(dashlet.getMetaInfoJson());
        result.setCode(dashlet.getCode());
        return result;
    }

    public static List<DashletModel> toDashletList(List<Dashlet> dashletList, long langID) {
        return dashletList.stream().map(d -> toModel(d, langID)).collect(Collectors.toList());
    }
    public static List<Dashlet> toDashletEntityList(List<DashletModel> dashletList, long langID) {
        return dashletList.stream().map(d -> toEntity(d, langID)).collect(Collectors.toList());
    }


}
