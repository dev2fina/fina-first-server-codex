package net.fina.server.returns.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.entity.DefinitionTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RItemMetaModel implements Serializable, Cloneable {

    private long id;
    private long returnId;
    private long versionId;
    private long tableId;
    private long nodeId;
    private long rowNumber;
    private String value;
    private double nvalue;
    private long nodeType;
    private long tableType;
    private String equation;
    private String code;
    private MDTNodeEvalMethods tableEvalMethod;
    private MDTNodeEvalMethods nodeEvalMethod;
    private MDTNodeDataTypes dataType;
    private long parentId;

    private String description;

    private List<RItemMetaModel> listElementValues;

    private String dataItemValue;

    private long sequence;

    private boolean itemNotInDatabase;

    private List<Long> dependentNodeIds;
    private List<Long> usedByNodeIds;

    @Override
    public RItemMetaModel clone() throws CloneNotSupportedException {
        return (RItemMetaModel) super.clone();
    }


    public RItemMetaModel setMetNode(MDTNode cell, DefinitionTable definitionTable, RDataMetaModel dataMetaModel, long langId, int row) {

        setValue(null);
        setNvalue(.0);

        setRowNumber(row);
        setId(0L);

        setCode(cell.getCode());
        setDescription(cell.getDescription().getDescription(langId));
        setNodeId(cell.getId());
        setDataType(cell.getDataType());
        setEquation(cell.getEquation());
        setTableType(definitionTable.getType().ordinal());
        setTableEvalMethod(definitionTable.getEvalType());
        setNodeEvalMethod(cell.getEvalMethod());
        setTableId(definitionTable.getId());
        setNodeType(cell.getType().ordinal());
        setReturnId(dataMetaModel.getReturnId());
        setVersionId(dataMetaModel.getVersionId());

        switch ((int) getNodeType()) {
            case 4: {  //LIST

                setListElementValues(new ArrayList<>());

                for (MDTNode n : cell.getListElementNodes()) {
                    getListElementValues().add(new RItemMetaModel().setMetNode(n, definitionTable, dataMetaModel, langId, row));
                }
                break;
            }
            case 5: { //DATA
                setDataItemValue(getEquation());
                break;
            }
        }
        return this;
    }

    public RItemMetaModel setProcessItem(ProcessItem pItem, int rowNumber) {

        switch (pItem.tableType) {
            case NT:
            case MCT:
                Iterator<Long> idIterator = pItem.idByRowNumber.values().iterator();
                if (idIterator.hasNext()) {
                    setId(idIterator.next());
                }
                Iterator<Double> nValueIterator = pItem.nValues.values().iterator();
                if (nValueIterator.hasNext()) {
                    Double nValue = nValueIterator.next();
                    if (nValue != null) {
                        setNvalue(nValue);
                    }
                }
                Iterator<String> valueIterator = pItem.values.values().iterator();
                if (valueIterator.hasNext()) {
                    setValue(valueIterator.next());
                }
                break;
            case VCT:
                Long id = pItem.idByRowNumber.get(rowNumber);
                if (id != null) {
                    setId(id);
                }

                Double nValue = pItem.nValues.get(rowNumber);
                if (nValue != null) {
                    setNvalue(nvalue);
                }

                setValue(pItem.values.get(rowNumber));
                break;
        }

        setRowNumber(rowNumber);
        setCode(pItem.code);
        setDescription(pItem.description);
        setNodeId(pItem.nodeId);
        setDataType(pItem.dataType);
        setEquation(pItem.equation);
        setTableType(pItem.tableType.ordinal());
        setTableEvalMethod(pItem.tableEvalType);
        setNodeEvalMethod(pItem.nodeEvalMethod);
        setTableId(pItem.tableId);
        setNodeType(pItem.nodeType.ordinal());
        setReturnId(pItem.returnId);
        setVersionId(pItem.versionId);

        switch ((int) getNodeType()) {
            case 4: {  //LIST
                setListElementValues(new ArrayList<>());
                for (ProcessItem p : pItem.listElementValues) {
                    getListElementValues().add(new RItemMetaModel().setProcessItem(p, rowNumber));
                }
                break;
            }
            case 5: { //DATA
                setDataItemValue(getEquation());
                break;
            }
        }

        return this;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(long rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getNvalue() {
        return nvalue;
    }

    public void setNvalue(double nvalue) {
        this.nvalue = nvalue;
    }

    public long getNodeType() {
        return nodeType;
    }

    public void setNodeType(long nodeType) {
        this.nodeType = nodeType;
    }

    public long getTableType() {
        return tableType;
    }

    public void setTableType(long tableType) {
        this.tableType = tableType;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MDTNodeEvalMethods getTableEvalMethod() {
        return tableEvalMethod;
    }

    public void setTableEvalMethod(MDTNodeEvalMethods tableEvalMethod) {
        this.tableEvalMethod = tableEvalMethod;
    }

    public MDTNodeEvalMethods getNodeEvalMethod() {
        return nodeEvalMethod;
    }

    public void setNodeEvalMethod(MDTNodeEvalMethods nodeEvalMethod) {
        this.nodeEvalMethod = nodeEvalMethod;
    }

    public MDTNodeDataTypes getDataType() {
        return dataType;
    }

    public void setDataType(MDTNodeDataTypes dataType) {
        this.dataType = dataType;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RItemMetaModel> getListElementValues() {
        return listElementValues;
    }

    public void setListElementValues(List<RItemMetaModel> listElementValues) {
        this.listElementValues = listElementValues;
    }

    public String getDataItemValue() {
        return dataItemValue;
    }

    public void setDataItemValue(String dataItemValue) {
        this.dataItemValue = dataItemValue;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public boolean isItemNotInDatabase() {
        return itemNotInDatabase;
    }

    public void setItemNotInDatabase(boolean itemNotInDatabase) {
        this.itemNotInDatabase = itemNotInDatabase;
    }

    public List<Long> getDependentNodeIds() {
        return dependentNodeIds;
    }

    public void setDependentNodeIds(List<Long> dependentNodeIds) {
        this.dependentNodeIds = dependentNodeIds;
    }

    public List<Long> getUsedByNodeIds() {
        return usedByNodeIds;
    }

    public void setUsedByNodeIds(List<Long> usedByNodeIds) {
        this.usedByNodeIds = usedByNodeIds;
    }

    @Override
    public String toString() {
        return "Id=" + id + ", returnId=" + returnId + ", versionId=" + versionId + ", versionId=" + versionId + ", nodeId=" + nodeId + ", code=" + code + ", rowNumber=" + rowNumber;
    }
}
