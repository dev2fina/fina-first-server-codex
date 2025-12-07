package net.fina.server.mdt.model.helper;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTPermissionType;
import net.fina.common.shared.mdt.MDTNodeModel;
import net.fina.server.i18n.helper.Description;
import net.fina.server.mdt.entity.MDTNode;

import java.util.List;
import java.util.stream.Collectors;

public class MdtNodeModelHelper {

    public static MDTNodeModel toModel(MDTNode node, long langId) {
        MDTNodeModel model = new MDTNodeModel();
        model.setId(node.getId());
        model.setCode(node.getCode());
        model.setName(node.getDescription().getDescription(langId));
        model.setNameStrId(node.getDescription().getNameStrId());
        model.setPermissionType(MDTPermissionType.NONE);
        model.setVersion(node.getVersion());
        model.setType(node.getType());
        model.setDamaged(node.isDamagedEquation());
        model.setCanUserAmend(node.getCanAmend() != null ? node.getCanAmend() : false);
        model.setCatalog(node.isCatalog());
        model.setDisabled(node.isDisabled());
        model.setRequired(node.isRequired());
        model.setSequence(node.getSequence());
        model.setParentId(node.getParentId());
        model.setKey(node.isKey());
        model.setEquation(node.getEquation());
        model.setEvalMethod(node.getEvalMethod());
        model.setDataType(node.getDataType());
        model.setDescriptions(node.getDescription().getDescriptions());
        return model;
    }


    public static MDTNode toEntity(MDTNodeModel node, long langId) {
        MDTNode result = new MDTNode();
        result.setId(node.getId());
        result.setCode(node.getCode());
        Description description = new Description();
        description.setNameStrId(node.getNameStrId());
        description.addDescription(langId, node.getName());
        result.setDescription(description);
        result.setVersion(node.getVersion());
        result.setType(node.getType());
        result.setCatalog(node.isCatalog());
        result.setDisabled(node.isDisabled());
        result.setRequired(node.isRequired());
        result.setSequence(node.getSequence());
        result.setParentId(node.getParentId());
        result.setKey(node.isKey());
        result.setEvalMethod(node.getEvalMethod());
        result.setDataType(node.getDataType() != null ? node.getDataType() : MDTNodeDataTypes.UNKNOWN);

        if (node.getEquation() == null || node.getEquation().trim().isEmpty()) {
            result.setEquation(" ");
        } else {
            result.setEquation(node.getEquation());
        }

        return result;
    }

    public static List<MDTNode> toEntities(List<MDTNodeModel> models, long langId) {
        return models.stream().map(n -> toEntity(n, langId)).collect(Collectors.toList());
    }

    public static List<MDTNodeModel> toModels(List<MDTNode> nodes, long langId) {
        return nodes.stream().map(node -> toModel(node, langId)).toList();
    }
}
