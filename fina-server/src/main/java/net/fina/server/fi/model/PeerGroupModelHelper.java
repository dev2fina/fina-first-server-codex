package net.fina.server.fi.model;


import net.fina.common.shared.fi.PeerGroupMetaModel;
import net.fina.server.fi.entity.PeerGroup;
import net.fina.server.i18n.model.DescriptionModelHelper;

import java.util.List;
import java.util.stream.Collectors;

public class PeerGroupModelHelper {
    public static List<PeerGroupMetaModel> toModels(List<PeerGroup> peerGroups) {
        return peerGroups.stream().map(peerGroup -> {
            PeerGroupMetaModel peerGroupMetaModel = new PeerGroupMetaModel();
            peerGroupMetaModel.setId(peerGroup.getId());
            peerGroupMetaModel.setVersion(peerGroup.getVersion());
            peerGroupMetaModel.setCode(peerGroup.getCode());
            peerGroupMetaModel.setDescriptions(DescriptionModelHelper.toModel(peerGroup.getDescription()));
            peerGroupMetaModel.setParentId(peerGroup.getParentId());
            return peerGroupMetaModel;
        }).collect(Collectors.toList());
    }
}
