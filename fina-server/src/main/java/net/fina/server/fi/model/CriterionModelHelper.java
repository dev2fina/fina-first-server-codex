package net.fina.server.fi.model;

import net.fina.common.shared.fi.CriterionMetaModel;
import net.fina.server.fi.entity.Criterion;
import net.fina.server.i18n.model.DescriptionModelHelper;

import java.util.List;
import java.util.stream.Collectors;

public class CriterionModelHelper {
    public static CriterionMetaModel toModel(Criterion criterion) {
        CriterionMetaModel result = new CriterionMetaModel();
        result.setId(criterion.getId());
        result.setCode(criterion.getCode());
        result.setDescriptions(DescriptionModelHelper.toModel(criterion.getDescription()));
        result.setVersion(criterion.getVersion());
        result.setDefaultGroup(criterion.getIsDefault());

        return result;
    }


    public static List<CriterionMetaModel> toModels(List<Criterion> criterionList) {
        return criterionList.stream().map(criterion -> toModel(criterion)).collect(Collectors.toList());
    }

}
