package net.fina.server.returns.model.helper;


import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.server.i18n.model.DescriptionModelHelper;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.returns.model.MNodeMetaModel;

import java.util.ArrayList;
import java.util.List;

public class MNodeModelHelper {

    public static MNodeMetaModel toModel(MDTNode mdtNode) {
        MNodeMetaModel model = new MNodeMetaModel();
        model.setId(mdtNode.getId());
        model.setCode(mdtNode.getCode());
        model.setParentId(mdtNode.getParentId());
        model.setDescriptions(DescriptionModelHelper.toModel(mdtNode.getDescription()));
        model.setType(mdtNode.getType());
        model.setDataType(mdtNode.getDataType());
        model.setSequence(mdtNode.getSequence());
        model.setEvalMethod(mdtNode.getEvalMethod());
        return model;
    }

    public static List<MNodeMetaModel> toModels(List<MDTNode> nodes) {
        List<MNodeMetaModel> result = new ArrayList<>();
        for (MDTNode mdtNode : nodes) {
            result.add(toModel(mdtNode));
        }
        return result;
    }

    public static void loadMdtNodeChildren(MNodeMetaModel node, MDTNodeLocal mdtNodeLocal) {
        node.setChildren(MNodeModelHelper.toModels(mdtNodeLocal.loadMdtNodesByParentId(node.getId())));
        node.getChildren().stream().filter(child -> child.getType() == MDTNodeTypes.NODE).forEach(child -> {
            loadMdtNodeChildren(child, mdtNodeLocal);
        });
    }


}
