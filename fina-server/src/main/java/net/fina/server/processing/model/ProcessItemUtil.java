package net.fina.server.processing.model;


import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.server.mdt.entity.MDTNode;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

public class ProcessItemUtil {
    private final Logger log = Logger.getLogger(getClass());

    public void loadListElementNodes(List<ProcessItem> processItems, ProcessItem processItem, Map<Long, List<MDTNode>> nodesMap, long langId) {
        try {
            if ((processItem.equation != null) && (!processItem.equation.isEmpty())) {
                long nodeId = Long.parseLong(processItem.equation);
                List<MDTNode> nodes = nodesMap.get(nodeId);
                if (nodes != null) {
                    for (MDTNode n : nodes) {
                        ProcessItem item = processItem.clone();
                        item.dataElementValue = n.getEquation();
                        item.nodeId = n.getId();
                        item.code = n.getCode();
                        item.equation = n.getEquation();
                        item.description = n.getDescription().getDescription(langId);
                        item.nodeType = n.getType();
                        item.dataType = n.getDataType();
                        item.parentId = nodeId;
                        item.nodeEvalMethod = n.getEvalMethod();
                        item.tableEvalType = processItem.tableEvalType;
                        if (item.nodeType == MDTNodeTypes.LIST && item.equation != null && !item.equation.isBlank()) {
                            loadListElementNodes(processItems, item, nodesMap, langId);
//                            ????
//                            loadListElementNodes(processItems, processItem, nodesMap, langId);
                        } else {
                            processItems.add(item);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            log.debug(t.getMessage(), t);
        }
    }
}
