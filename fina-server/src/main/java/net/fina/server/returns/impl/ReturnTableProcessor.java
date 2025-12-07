package net.fina.server.returns.impl;

import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReturnTableProcessor implements ReturnTableProcessorBase {
    private final Map<Long, List<MDTNode>> allMdtNodesByParentId;

    public ReturnTableProcessor(Map<Long, List<MDTNode>> allMdtNodesByParentId) {
        this.allMdtNodesByParentId = allMdtNodesByParentId;
    }

    @Override
    public RTableMetaModel process(final DefinitionTable definitionTable, Map<Long, ProcessItem> processItemMap, long langId, RDataMetaModel dataMetaModel) {
        RTableMetaModel tableMetaModel = new RTableMetaModel();
        tableMetaModel.setTableId(definitionTable.getId());
        tableMetaModel.setType(definitionTable.getType());
        tableMetaModel.setEvalMethod(definitionTable.getEvalType());
        tableMetaModel.setSequence(definitionTable.getSequence());
        tableMetaModel.setVisibleLevel(definitionTable.getVisibleLevel());

        ReturnTableType tableType = definitionTable.getType();
        switch (tableType) {
            case NT:
            case MCT: {
                compileMCTAndNTTable(definitionTable, tableMetaModel, processItemMap, langId, dataMetaModel);
                break;
            }
            case VCT: {
                compileVCTTable(definitionTable, tableMetaModel, processItemMap, langId, dataMetaModel);
                break;
            }
            default:
                throw new RuntimeException("type = " + tableType + " - Return Table Type not Supported.");
        }

        return tableMetaModel;
    }

    // MCT
    public void compileMCTAndNTTable(DefinitionTable definitionTable, RTableMetaModel tableMetaModel, Map<Long, ProcessItem> processItemMap, long langId, RDataMetaModel dataMetaModel) {
        MDTNode title = definitionTable.getNode();
        tableMetaModel.setDescription(title.getDescription().getDescription(langId));

        RTableRowMetaModel firstRow = new RTableRowMetaModel();

        RItemMetaModel firstEmptyItem = new RItemMetaModel();
        firstRow.getRowItems().add(firstEmptyItem);

        List<MNodeMetaModel> columns = new ArrayList<>();

        //Load columns
        List<MDTNode> mdtNodeColumns = allMdtNodesByParentId.get(title.getId());
        if (mdtNodeColumns != null) {
            for (MDTNode node : mdtNodeColumns) {
                MNodeMetaModel model = new MNodeMetaModel();
                columns.add(model.setMdtNode(node, langId, allMdtNodesByParentId));
            }
        }
        sortMetaModels(columns);

        if (definitionTable.getType() == ReturnTableType.NT) {
            columns = Collections.singletonList(new MNodeMetaModel().setMdtNode(title, langId, allMdtNodesByParentId));
        }

        //Add column titles row
        for (MNodeMetaModel column : columns) {
            RItemMetaModel item = column.getRItemMetaModel(dataMetaModel.getReturnId(), dataMetaModel.getVersionId(), tableMetaModel);
            firstRow.getRowItems().add(item);

        }
        tableMetaModel.getRows().add(firstRow);

        //Compile table
        List<List<MDTNode>> table = new ArrayList<>();

        for (MNodeMetaModel model : columns) {
            List<MDTNode> columnChildren = allMdtNodesByParentId.get(model.getId());

            if (columnChildren != null) {
                sort(columnChildren);

                List<MDTNode> cells = new ArrayList<>();

                for (MDTNode cell : columnChildren) {
                    cells.add(cell);
                    if (model.getType() == MDTNodeTypes.NODE) {
                        compileSubNodes(cells, cell);
                    }
                }

                table.add(cells);
            }
        }

        //Check MCT columns row size
        int size = 0;
        for (int i = 0; i < table.size(); i++) {
            List<MDTNode> column = table.get(i);
            if (i != 0 && size != column.size()) {
                throw new RuntimeException(String.format("Invalid Return format. Return : %s column:%s size:%s actual size is:%s ", definitionTable.getCode(), i, size, column.size()));
            }
            size = column.size();
        }

        //Complete data and template
        for (int row = 0; row < size; row++) {

            RTableRowMetaModel rowMetaModel = new RTableRowMetaModel();

            for (int column = 0; column < table.size(); column++) {
                List<MDTNode> columnList = table.get(column);

                MDTNode cell = columnList.get(row);

                ProcessItem pItem = processItemMap.get(cell.getId());

                RItemMetaModel model;
                if (pItem == null) {
                    model = new RItemMetaModel().setMetNode(cell, definitionTable, dataMetaModel, langId, row);
                    model.setItemNotInDatabase(true);
                } else {
                    model = new RItemMetaModel().setProcessItem(pItem, row);
                }

                rowMetaModel.getRowItems().add(model);
                //Add Titles column
                if (column == 0) {
                    rowMetaModel.getRowItems().add(model);
                }
            }

            tableMetaModel.getRows().add(rowMetaModel);
        }
    }

    private void compileSubNodes(List<MDTNode> nodes, MDTNode node) {
        List<MDTNode> children = allMdtNodesByParentId.get(node.getId());
        if (children != null) {
            children = new ArrayList<>(children);
            sort(children);
            for (MDTNode n : children) {
                nodes.add(n);
                if (node.getType() == MDTNodeTypes.NODE) {
                    compileSubNodes(nodes, n);
                }
            }
        }
    }

    // VCT
    public void compileVCTTable(DefinitionTable definitionTable, RTableMetaModel tableMetaModel, Map<Long, ProcessItem> processItemMap, long langId, RDataMetaModel dataMetaModel) {
        MDTNode title = definitionTable.getNode();
        tableMetaModel.setDescription(title.getDescription().getDescription(langId));

        List<MDTNode> mdtNodeColumns = allMdtNodesByParentId.get(title.getId());
        List<MNodeMetaModel> columns = nodesToMetaModel(mdtNodeColumns, langId);

        RTableRowMetaModel defaultRow = new RTableRowMetaModel();

        loadVCTRows(columns, defaultRow, dataMetaModel.getReturnId(), dataMetaModel.getVersionId(), tableMetaModel, langId);

        int rowCount = getVctRowCount(defaultRow, processItemMap);

        // Initial Table
        for (int i = 0; i <= rowCount; i++) {
            RTableRowMetaModel row = new RTableRowMetaModel();
            for (int column = 0; column < defaultRow.getRowItems().size(); column++) {
                RItemMetaModel item = defaultRow.getRowItems().get(column);
                try {
                    RItemMetaModel tmp = item.clone();
                    tmp.setRowNumber(i);
                    row.getRowItems().add(tmp);
                } catch (CloneNotSupportedException ignored) {
                }
            }
            tableMetaModel.getRows().add(row);
        }

        // Load Data
        for (RTableRowMetaModel rowMeta : tableMetaModel.getRows()) {
            for (RItemMetaModel itemMetaModel : rowMeta.getRowItems()) {
                ProcessItem pItem = processItemMap.get(itemMetaModel.getNodeId());
                if (pItem != null) {
                    itemMetaModel.setProcessItem(pItem, (int) itemMetaModel.getRowNumber());
                }
            }
        }

        // Set First Row
        tableMetaModel.getRows().add(0, defaultRow);
    }

    private int getVctRowCount(RTableRowMetaModel defaultRow, Map<Long, ProcessItem> processItemMap) {
        int count = 0;
        for (RItemMetaModel model : defaultRow.getRowItems()) {
            ProcessItem pItem = processItemMap.get(model.getNodeId());
            if (pItem != null) {
                for (int rowNumber : pItem.idByRowNumber.keySet()) {
                    if (count < rowNumber) {
                        count = rowNumber;
                    }
                }
            }
        }
        return count;
    }

    private List<MNodeMetaModel> nodesToMetaModel(List<MDTNode> nodes, long langId) {
        List<MNodeMetaModel> models = new ArrayList<>();
        if (nodes != null) {
            for (MDTNode node : nodes) {
                MNodeMetaModel model = new MNodeMetaModel();
                models.add(model.setMdtNode(node, langId, this.allMdtNodesByParentId));
            }
        }
        sortMetaModels(models);
        return models;
    }

    private void loadVCTRows(List<MNodeMetaModel> columns, RTableRowMetaModel rowMetaModel, long returnId, long versionId, RTableMetaModel tableMetaModel, long langId) {
        for (MNodeMetaModel node : columns) {
            RItemMetaModel model = node.getRItemMetaModel(returnId, versionId, tableMetaModel);
            rowMetaModel.getRowItems().add(model);

            List<MNodeMetaModel> models = nodesToMetaModel(allMdtNodesByParentId.get(node.getId()), langId);
            loadVCTRows(models, rowMetaModel, returnId, versionId, tableMetaModel, langId);
        }

    }

    private void sort(List<MDTNode> nodes) {
        if (nodes != null) {
            Collections.sort(nodes, (o1, o2) -> Long.compare(o1.getSequence(), o2.getSequence()));
        }
    }

    private void sortMetaModels(List<MNodeMetaModel> nodes) {
        Collections.sort(nodes, (o1, o2) -> Long.compare(o1.getSequence(), o2.getSequence()));
    }

    @Override
    public List<RItemMetaModel> processItemToRItemMetaModel(ProcessItem pItem) {
        List<RItemMetaModel> result = new ArrayList<>();
        for (Map.Entry<Integer, Long> rowAndId : pItem.idByRowNumber.entrySet()) {
            RItemMetaModel model = new RItemMetaModel().setProcessItem(pItem, rowAndId.getKey());
            result.add(model);
        }
        return result;
    }
}
