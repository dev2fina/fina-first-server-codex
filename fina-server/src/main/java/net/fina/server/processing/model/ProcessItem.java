package net.fina.server.processing.model;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.classifier.model.MDTCatalogMetaModel;
import net.fina.server.i18n.helper.Description;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProcessItem implements Serializable, Cloneable {
    public Map<Integer, Long> idByRowNumber = new HashMap<>();
    public long nodeId;
    public MDTNodeTypes nodeType;
    public MDTNodeEvalMethods nodeEvalMethod;
    public long parentId;
    public long returnId;
    public long tableId;
    public ReturnTableType tableType;
    public Map<Integer, String> values = new HashMap<>();
    public Map<Integer, Double> nValues = new HashMap<>();
    public String equation;
    public MDTNodeEvalMethods tableEvalType;
    public String code;
    public MDTNodeDataTypes dataType;
    public long versionId;
    public Dependent dependent;
    public boolean required;
    public String description;
    public long sequence;
    public List<ProcessItem> listElementValues = new ArrayList<>();
    public String dataElementValue;

    //Temp fields
    private long rowNumber;
    private long id;
    private String value;
    private Double nValue;
    private Description descriptions;
    private boolean init;

    private MDTCatalogMetaModel catalog;

    public ProcessItem() {
    }

    //Used in hibernate query
    public ProcessItem(
            long returnId,
            long nodeId,
            MDTNodeTypes nodeType,
            long rowNumber,
            String value,
            ReturnTableType tableType,
            long tableId,
            String equation,
            String code,
            MDTNodeEvalMethods tableEvalType,
            MDTNodeDataTypes dataType,
            long id,
            Double nValue,
            long parentId,
            MDTNodeEvalMethods nodeEvalMethod
    ) {
        this.returnId = returnId;
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.rowNumber = rowNumber;
        this.value = value;
        this.tableType = tableType;
        this.tableId = tableId;
        this.equation = equation;
        this.code = code;
        this.tableEvalType = tableEvalType;
        this.dataType = dataType;
        this.id = id;
        this.nValue = nValue;
        this.parentId = parentId;
        this.nodeEvalMethod = nodeEvalMethod;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Double getnValue() {
        return nValue;
    }

    public Description getDescriptions() {
        return descriptions;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public MDTCatalogMetaModel getCatalog() {
        return catalog;
    }

    public void setCatalog(MDTCatalogMetaModel catalog) {
        this.catalog = catalog;
    }

    @Override
    public ProcessItem clone() throws CloneNotSupportedException {
        return (ProcessItem) super.clone();
    }

    @Override
    public String toString() {
        return "ProcessItem{" +
                "nodeId=" + nodeId +
                ", returnId=" + returnId +
                ", tableId=" + tableId +
                ", code='" + code + '\'' +
                ", versionId=" + versionId +
                ", rowNumber=" + rowNumber +
                ", id=" + id +
                ", values='" + values + '\'' +
                ", nvalues=" + nValues +
                "}\n";
    }
}
