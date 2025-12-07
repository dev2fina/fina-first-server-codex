package net.fina.server.returns.model;


import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.shared.i18n.DescriptionMetaModel;
import net.fina.server.mdt.entity.MDTNode;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MNodeMetaModel implements Serializable {
    private Logger log = Logger.getLogger(getClass());

    private long id;
    private String code;
    private String description;
    private long parentId;
    private MDTNodeTypes type;
    private MDTNodeDataTypes dataType;
    private String equation;
    private long sequence;
    private MDTNodeEvalMethods evalMethod;
    private boolean disabled;
    private boolean required;

    private List<MNodeMetaModel> listElementValues;
    private String dataItemValue;

    private List<DescriptionMetaModel> descriptions;
    private List<MNodeMetaModel> children = new ArrayList<MNodeMetaModel>();

    public MNodeMetaModel setMdtNode(MDTNode node, long langId, Map<Long, List<MDTNode>> allMdtNodesByParentId) {
        this.id = node.getId();
        this.code = node.getCode();
        this.description = node.getDescription().getDescription(langId);
        this.parentId = node.getParentId();
        this.type = node.getType();
        this.dataType = node.getDataType();
        this.equation = node.getEquation();
        this.sequence = node.getSequence();
        this.evalMethod = node.getEvalMethod();
        this.disabled = node.isDisabled();
        this.required = node.isRequired();

        switch (this.type) {
            case LIST:
                List<MNodeMetaModel> tmp = new ArrayList<>();
                loadListElementNodes(tmp, node, allMdtNodesByParentId, langId);
                listElementValues = tmp;
                break;
            case DATA:
                this.dataItemValue = node.getEquation();
                break;
        }

        return this;
    }

    public void loadListElementNodes(List<MNodeMetaModel> listElementValues, MDTNode node, Map<Long, List<MDTNode>> nodesMap, long langId) {
        try {
            if ((node.getEquation() != null) && (!node.getEquation().isEmpty())) {
                long nodeId = Long.parseLong(node.getEquation());
                if (nodesMap != null) {
                    List<MDTNode> nodes = nodesMap.get(nodeId);
                    if (nodes != null) {
                        for (MDTNode n : nodes) {
                            if (n.getType() == MDTNodeTypes.LIST) {
                                loadListElementNodes(listElementValues, n, nodesMap, langId);
                            } else {
                                listElementValues.add(new MNodeMetaModel().setMdtNode(n, langId, nodesMap));
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    public RItemMetaModel getRItemMetaModel(long returnId, long versionId, RTableMetaModel table) {

        RItemMetaModel model = new RItemMetaModel();

        model.setNodeId(getId());
        model.setCode(getCode());
        model.setDescription(getDescription());
        model.setNodeType(getType().ordinal());
        model.setDataType(getDataType());
        model.setEquation(getEquation());
        model.setNodeEvalMethod(getEvalMethod());
        model.setReturnId(returnId);
        model.setVersionId(versionId);
        model.setTableId(table.getTableId());
        model.setParentId(getParentId());
        model.setTableEvalMethod(table.getEvalMethod());
        model.setTableType(table.getType().ordinal());

        switch (getType()) {
            case LIST: {
                if (listElementValues != null) {
                    for (MNodeMetaModel n : listElementValues) {
                        if (model.getListElementValues() == null) {
                            model.setListElementValues(new ArrayList<>());
                        }
                        model.getListElementValues().add(n.getRItemMetaModel(returnId, versionId, table));
                    }
                }
                break;
            }
            case DATA: {
                model.setDataItemValue(model.getEquation());
                break;
            }
        }
        return model;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public MDTNodeTypes getType() {
        return type;
    }

    public void setType(MDTNodeTypes type) {
        this.type = type;
    }

    public MDTNodeDataTypes getDataType() {
        return dataType;
    }

    public void setDataType(MDTNodeDataTypes dataType) {
        this.dataType = dataType;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public MDTNodeEvalMethods getEvalMethod() {
        return evalMethod;
    }

    public void setEvalMethod(MDTNodeEvalMethods evalMethod) {
        this.evalMethod = evalMethod;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<MNodeMetaModel> getListElementValues() {
        return listElementValues;
    }

    public void setListElementValues(List<MNodeMetaModel> listElementValues) {
        this.listElementValues = listElementValues;
    }

    public String getDataItemValue() {
        return dataItemValue;
    }

    public void setDataItemValue(String dataItemValue) {
        this.dataItemValue = dataItemValue;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public List<DescriptionMetaModel> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DescriptionMetaModel> descriptions) {
        this.descriptions = descriptions;
    }

    public List<MNodeMetaModel> getChildren() {
        return children;
    }

    public void setChildren(List<MNodeMetaModel> children) {
        this.children = children;
    }
}
