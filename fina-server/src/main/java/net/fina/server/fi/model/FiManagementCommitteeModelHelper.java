package net.fina.server.fi.model;

import net.fina.server.fi.entity.FiManagementCommittee;
import net.fina.server.i18n.helper.Description;

import java.util.ArrayList;
import java.util.List;

public class FiManagementCommitteeModelHelper {

    public static FiManagementCommittee toEntity(FiManagementCommitteeMetaModel model, long langId) {
        FiManagementCommittee entity = new FiManagementCommittee();
        entity.setId(model.getId());
        entity.setName(new Description(langId, model.getNameStrId(), model.getName()));
        entity.setPosition(new Description(langId, model.getPositionNameStrId(), model.getPosition()));
        entity.setApprovalDate(model.getApprovalDate());
        entity.setElectionDate(model.getElectionDate());
        entity.setComment(model.getComment());

        return entity;
    }

    public static FiManagementCommitteeMetaModel toModel(FiManagementCommittee entity, long langId) {
        FiManagementCommitteeMetaModel model = new FiManagementCommitteeMetaModel();
        model.setId(entity.getId());
        model.setName(entity.getName().getDescription(langId));
        model.setNameStrId(entity.getName().getNameStrId());
        model.setPosition(entity.getPosition().getDescription(langId));
        model.setApprovalDate(entity.getApprovalDate());
        model.setElectionDate(entity.getElectionDate());
        model.setComment(entity.getComment());

        return model;
    }

    public static List<FiManagementCommittee> toEntities(List<FiManagementCommitteeMetaModel> models, long langId) {
        List<FiManagementCommittee> result = new ArrayList<>();
        models.forEach(m -> result.add(toEntity(m, langId)));
        return result;
    }

    public static List<FiManagementCommitteeMetaModel> toModels(List<FiManagementCommittee> entities, long langId) {
        List<FiManagementCommitteeMetaModel> result = new ArrayList<>();
        entities.forEach(e -> result.add(toModel(e, langId)));
        return result;
    }
}
