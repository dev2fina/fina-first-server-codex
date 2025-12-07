package net.fina.server.fi.model;

import net.fina.common.client.fis.FiGroupModel;
import net.fina.server.fi.entity.Criterion;
import net.fina.server.fi.entity.PeerGroup;
import net.fina.server.i18n.helper.Description;

import java.util.Collection;
import java.util.List;

public class FiGroupModelHelper {

    public static FiGroupModel toModel(Criterion criterion, long langId) {
        FiGroupModel model = new FiGroupModel();
        model.setCode(criterion.getCode());
        model.setDefault(criterion.getIsDefault());
        model.setId(criterion.getId());
        model.setVersion(criterion.getVersion());
        model.setName(criterion.getDescription().getDescription(langId));
        model.setNameStrId(criterion.getDescription().getNameStrId());
        model.setType(FiGroupModel.Type.PARENT);
        return model;
    }

    public static FiGroupModel toModel(PeerGroup peerGroup, long langId) {
        FiGroupModel model = new FiGroupModel();
        model.setCode(peerGroup.getCode());
        model.setId(peerGroup.getId());
        model.setVersion(peerGroup.getVersion());
        model.setName(peerGroup.getDescription().getDescription(langId));
        model.setNameStrId(peerGroup.getDescription().getNameStrId());
        model.setParentId(peerGroup.getParentId());
        model.setType(FiGroupModel.Type.CHILD);
        return model;
    }

    public static PeerGroup toPeerGroupEntity(FiGroupModel model, long langId) {
        PeerGroup entity = new PeerGroup();
        entity.setCode(model.getCode());
        entity.setId(model.getId());
        entity.setParentId(model.getParentId());
        entity.setDescription(new Description(langId, model.getNameStrId(), model.getName()));
        entity.setVersion(model.getVersion() == null ? 0 : model.getVersion());
        return entity;
    }

    public static Criterion toCriterionEntity(FiGroupModel model, long langId) {
        Criterion entity = new Criterion();
        entity.setCode(model.getCode());
        entity.setId(model.getId());
        entity.setDescription(new Description(langId, model.getNameStrId(), model.getName()));
        entity.setIsDefault(model.getIsDefault());
        entity.setVersion(model.getVersion() == null ? 0 : model.getVersion());
        return entity;
    }

    public static List<FiGroupModel> toModelsFromPeerGroups(Collection<PeerGroup> peerGroups, long langId) {
        return peerGroups.stream().map(peerGroup -> toModel(peerGroup, langId)).toList();
    }

    public static List<FiGroupModel> toModelsFromCriterions(Collection<Criterion> criterions, long langId) {
        return criterions.stream().map(criterion -> toModel(criterion, langId)).toList();
    }
}
