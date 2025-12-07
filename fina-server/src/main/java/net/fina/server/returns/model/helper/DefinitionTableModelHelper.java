package net.fina.server.returns.model.helper;

import net.fina.common.client.returns.DefinitionTableModel;
import net.fina.common.shared.mdt.MDTNodeModel;
import net.fina.server.i18n.helper.Description;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.returns.entity.DefinitionTable;

import java.util.List;
import java.util.stream.Collectors;

public class DefinitionTableModelHelper {
    public static DefinitionTableModel toModel(DefinitionTable definition, long langId) {
        DefinitionTableModel table = new DefinitionTableModel();

        table.setId(definition.getId());
        table.setVersion(definition.getVersion());
        table.setCode(definition.getCode());
        table.setEvalType(definition.getEvalType());
        table.setNodeVisible(definition.isNodeVisible());
        table.setType(definition.getType());
        table.setVisibleLevel(definition.getVisibleLevel());
        table.setSequence(definition.getSequence());

        MDTNode node = definition.getNode();

        if (node != null) {
            MDTNodeModel mdtNode = new MDTNodeModel();

            mdtNode.setId(node.getId());
            mdtNode.setVersion(node.getVersion());
            mdtNode.setCode(node.getCode());
            mdtNode.setName(node.getDescription().getDescription(langId));
            mdtNode.setType(node.getType());

            table.setNode(mdtNode);
        }

        return table;
    }

    public static DefinitionTable toEntity(DefinitionTableModel definition, long langId) {
        DefinitionTable table = new DefinitionTable();

        table.setId(definition.getId());
        table.setVersion(definition.getVersion());
        table.setCode(definition.getCode());
        table.setEvalType(definition.getEvalType());
        table.setNodeVisible(definition.isNodeVisible());
        table.setType(definition.getType());
        table.setVisibleLevel(definition.getVisibleLevel());
        table.setSequence(definition.getSequence());

        MDTNodeModel nodeModel = definition.getNode();

        if (nodeModel != null) {
            MDTNode mdtNode = new MDTNode();

            mdtNode.setId(nodeModel.getId());
            mdtNode.setVersion(nodeModel.getVersion());
            mdtNode.setCode(nodeModel.getCode());
            mdtNode.setDescription(new Description(langId, nodeModel.getNameStrId(), nodeModel.getName()));

            table.setNode(mdtNode);
        }

        return table;
    }

    public static List<DefinitionTableModel> toModels(List<DefinitionTable> definitionTables,long langId){
        return definitionTables.stream().map(d->toModel(d,langId)).collect(Collectors.toList());
    }
}
