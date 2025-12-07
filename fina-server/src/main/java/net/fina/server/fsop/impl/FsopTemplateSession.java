package net.fina.server.fsop.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.fsop.api.FsopImportStoreLocal;
import net.fina.server.fsop.api.FsopTemplateLocal;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import net.fina.server.interceptors.LogDescription;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.impl.DependentProcessor;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.model.ProcessItemUtil;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.Return;
import net.fina.server.returns.entity.ReturnItemLite;
import net.fina.server.security.api.PropertyLocal;

import java.util.*;


@Stateless
@Local(FsopTemplateLocal.class)
@Interceptors(RecordingAuditor.class)
public class FsopTemplateSession implements FsopTemplateLocal {

    @EJB
    private FsopImportStoreLocal fsopImportStoreLocal;
    @Inject
    private PropertyLocal propertyLocal;

    @Override
    @LogDescription(logMethodParameters = false)
    public List<ProcessItem> getReturnTemplate(Return ret, Map<Long, List<MDTNode>> allMdtNodesByParentId, long langId, List<MDTDependentNode> mdtDependentNodes) {
        List<ProcessItem> items = new ArrayList<>();
        getReturnTemplate(ret, allMdtNodesByParentId, langId, mdtDependentNodes, items, null);
        return items;
    }

    @Override
    @LogDescription(logMethodParameters = false)
    public List<ProcessItem> getReturnTemplate(Return ret, Map<Long, List<MDTNode>> allMdtNodesByParentId, long langId, List<MDTDependentNode> mdtDependentNodes, List<DefinitionTable> definitionTables) {
        List<ProcessItem> items = new ArrayList<>();
        getReturnTemplate(ret, allMdtNodesByParentId, langId, mdtDependentNodes, items, null, definitionTables);
        return items;
    }

    @Override
    @LogDescription(logMethodParameters = false)
    public Map<String, ReturnItemLite> getReturnTemplate(Return ret, Map<Long, List<MDTNode>> allMdtNodesByParentId) {
        Map<String, ReturnItemLite> returnItems = new HashMap<>();
        getReturnTemplate(ret, allMdtNodesByParentId, -1, null, null, returnItems);
        return returnItems;
    }

    @Override
    @LogDescription(logMethodParameters = false)
    public Map<Long, ProcessItem> setReturnValues(FsopImportedReturnMetaModel importedReturn, net.fina.server.returns.xml.Return returnXml, List<ProcessItem> items) {
        String templateProcessorVersion = propertyLocal.getSystemProperty(PropertyKeys.PROCESSING_TEMPLATE_PROCESSOR_VERSION);
        FsopReturnTemplateProcessorBase templateProcessor;
        if (templateProcessorVersion != null && templateProcessorVersion.trim().equalsIgnoreCase("V2")) {
            templateProcessor = new FsopReturnTemplateProcessorV2(importedReturn, returnXml, items);
        } else {
            templateProcessor = new FsopReturnTemplateProcessor(importedReturn, returnXml, items);
        }
        return templateProcessor.process();
    }

    @LogDescription(logMethodParameters = false)
    private void getReturnTemplate(Return ret, Map<Long, List<MDTNode>> allMdtNodesByParentId, long langId, List<MDTDependentNode> mdtDependentNodes, List<ProcessItem> processItems, Map<String, ReturnItemLite> returnItemLiteMap) {
        List<DefinitionTable> definitionTables = fsopImportStoreLocal.getDefinitionTables(ret.getSchedule().getId());
        getReturnTemplate(ret, allMdtNodesByParentId, langId, mdtDependentNodes, processItems, returnItemLiteMap, definitionTables);
    }

    @LogDescription(logMethodParameters = false)
    private void getReturnTemplate(Return ret, Map<Long, List<MDTNode>> allMdtNodesByParentId, long langId, List<MDTDependentNode> mdtDependentNodes, List<ProcessItem> processItems, Map<String, ReturnItemLite> returnItemLiteMap, List<DefinitionTable> definitionTables) {
        for (DefinitionTable dt : definitionTables) {

            long tableId = dt.getId();

            MDTNode root = dt.getNode();
            ReturnTableType returnTableType = dt.getType();

            int rowCount = -1;

            List<List<MDTNode>> columns = new ArrayList<>();

            DependentProcessor dependentProcessor = new DependentProcessor(mdtDependentNodes);

            List<MDTNode> nodes = allMdtNodesByParentId.get(root.getId());
            if (nodes != null) {
                switch (returnTableType) {
                    case MCT: {
                        // Compile Columns
                        for (MDTNode node : nodes) {
                            if (node.getType() != MDTNodeTypes.NODE) {
                                continue;
                            }
                            loadDefinitionTableNodes(node, allMdtNodesByParentId);
                            List<MDTNode> temp = new ArrayList<>();
                            nodeChildrenToList(node, temp);
                            columns.add(temp);
                        }
                        // Calculate row count
                        for (int i = 0; i < columns.size(); i++) {
                            List<MDTNode> rows = columns.get(i);
                            if (rowCount == -1) {
                                rowCount = rows.size();
                            } else if (rowCount != rows.size()) {
                                throw new RuntimeException("Return Code:" + dt.getCode() + " Wrong MDT Column: " + (i + 1) + " required rows: " + rowCount + " exist rows: " + rows.size());
                            }
                        }
                        for (int i = 0; i < rowCount; i++) {
                            for (List<MDTNode> rows : columns) {
                                MDTNode mdtNode = rows.get(i);
                                if (processItems != null) {
                                    processItems.add(createProcessItem(mdtNode, ret, dt, i + 1, i, langId, allMdtNodesByParentId, dependentProcessor));
                                }
                                if (returnItemLiteMap != null) {
                                    returnItemLiteMap.put(mdtNode.getCode().trim(), createReturnItemLite(i + 1, i, mdtNode, tableId, returnTableType, ret));
                                }
                            }
                        }
                        break;
                    }
                    case NT: {
                        // Compile Columns
                        Collections.sort(nodes, new MDTNodeSequenceComparator());
                        for (MDTNode node : nodes) {
                            columns.add(Collections.singletonList(node));
                        }
                        // Insert Rows
                        for (int i = 0; i < columns.size(); i++) {
                            List<MDTNode> row = columns.get(i);
                            Iterator<MDTNode> iter = row.iterator();
                            if (iter.hasNext()) {
                                MDTNode mdtNode = iter.next();
                                if (processItems != null) {
                                    processItems.add(createProcessItem(mdtNode, ret, dt, i + 1, i, langId, allMdtNodesByParentId, dependentProcessor));
                                }
                                if (returnItemLiteMap != null) {
                                    returnItemLiteMap.put(mdtNode.getCode().trim(), createReturnItemLite(i + 1, i, mdtNode, tableId, returnTableType, ret));
                                }
                            }
                        }
                        break;
                    }
                    case VCT: {
                        // Insert Rows
                        for (int i = 0; i < nodes.size(); i++) {
                            MDTNode mdtNode = nodes.get(i);
                            if (processItems != null) {
                                processItems.add(createProcessItem(mdtNode, ret, dt, i + 1, 0, langId, allMdtNodesByParentId, dependentProcessor));
                            }
                            if (returnItemLiteMap != null) {
                                returnItemLiteMap.put(mdtNode.getCode().trim(), createReturnItemLite(i + 1, 0, mdtNode, tableId, returnTableType, ret));
                            }
                        }
                        break;
                    }
                    case UNKNOWN: {
                        throw new IllegalArgumentException(ReturnTableType.UNKNOWN.name() + " return table type doesn't supported.");
                    }
                }
            }
        }
    }

    @LogDescription(logMethodParameters = false)
    private ProcessItem createProcessItem(MDTNode mdtNode, Return ret, DefinitionTable dt, long id, int rowNumber, long langId, Map<Long, List<MDTNode>> allMdtNodesByParentId, DependentProcessor dependentProcessor) {

        ProcessItem item = new ProcessItem();
        item.returnId = ret.getId();
        item.nodeId = mdtNode.getId();
        item.nodeType = mdtNode.getType();

        item.idByRowNumber.put(rowNumber, id);
        item.values.put(rowNumber, "");
        item.nValues.put(rowNumber, .0);

        item.tableType = dt.getType();
        item.tableId = dt.getId();
        item.equation = mdtNode.getEquation();
        item.code = mdtNode.getCode().trim();
        item.tableEvalType = dt.getEvalType();
        item.dataType = mdtNode.getDataType();
        item.description = mdtNode.getDescription().getDescription(langId);
        item.parentId = mdtNode.getParentId();
        item.nodeEvalMethod = mdtNode.getEvalMethod();
        item.versionId = ret.getReturnVersion().getId();
        item.required = mdtNode.isRequired();

        if (item.nodeType == MDTNodeTypes.LIST) {
            List<ProcessItem> processItems = new ArrayList<>();
            ProcessItemUtil util = new ProcessItemUtil();
            util.loadListElementNodes(processItems, item, allMdtNodesByParentId, langId);
            item.listElementValues = processItems;
        }

        item.dependent = dependentProcessor.getNodeDependent(item.nodeId);

        return item;
    }

    @LogDescription(logMethodParameters = false)
    private ReturnItemLite createReturnItemLite(long id, long rowNumber, MDTNode node, long tableId, ReturnTableType tableType, Return procesedReturn) {
        ReturnItemLite rItem = new ReturnItemLite();
        rItem.setId(id);
        rItem.setNodeId(node.getId());
        rItem.setTableId(tableId);
        rItem.setRowNumber(rowNumber);
        rItem.setVersionId(procesedReturn.getReturnVersion().getId());
        rItem.setReturnId(procesedReturn.getId());
        rItem.setTableType(tableType);
        rItem.setNodeType(node.getType());
        rItem.setDataType(node.getDataType());
        rItem.setNodeCode(node.getCode());
        rItem.setRequired(node.isRequired());
        return rItem;
    }

    private void loadDefinitionTableNodes(MDTNode mdtNode, Map<Long, List<MDTNode>> allMdtNodesByParentId) {
        List<MDTNode> mdtNodes = allMdtNodesByParentId.get(mdtNode.getId());
        if (mdtNodes != null) {
            for (MDTNode node : mdtNodes) {
                loadDefinitionTableNodes(node, allMdtNodesByParentId);
            }
            mdtNode.setChildren(mdtNodes);
        }
    }

    private void nodeChildrenToList(MDTNode node, List<MDTNode> nodes) {
        List<MDTNode> children = node.getChildren();
        Collections.sort(children, new MDTNodeSequenceComparator());
        for (MDTNode childNode : children) {
            nodes.add(childNode);
            nodeChildrenToList(childNode, nodes);
        }
    }

    static class MDTNodeSequenceComparator implements Comparator<MDTNode> {
        @Override
        public int compare(MDTNode o1, MDTNode o2) {
            if (o1 != null && o2 != null) {
                return Long.compare(o1.getSequence(), o2.getSequence());
            }
            return 0;
        }

    }
}
