package net.fina.server.returns.model;

import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.model.helper.MNodeModelHelper;

import java.io.Serializable;

public class DefinitionTableMetaModel implements Serializable {
    private long id;
    private String code;
    private MNodeMetaModel node;
    private boolean nodeVisible;
    private long visibleLevel;
    private ReturnTableType type;
    private MDTNodeEvalMethods evalType;
    private Integer version;
    private long sequence;

    private ReturnDefinitionMetaModel returnDefinition;

    public DefinitionTableMetaModel setEntity(DefinitionTable table) {
        this.id = table.getId();
        this.code = table.getCode();
        this.node = MNodeModelHelper.toModel(table.getNode());
        this.nodeVisible = table.isNodeVisible();
        this.visibleLevel = table.getVisibleLevel();
        this.type = table.getType();
        this.evalType = table.getEvalType();
        this.version = table.getVersion();
        this.sequence = table.getSequence();

        return this;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ReturnDefinitionMetaModel getReturnDefinition() {
        return returnDefinition;
    }

    public void setReturnDefinition(ReturnDefinitionMetaModel returnDefinition) {
        this.returnDefinition = returnDefinition;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MNodeMetaModel getNode() {
        return node;
    }

    public void setNode(MNodeMetaModel node) {
        this.node = node;
    }

    public boolean isNodeVisible() {
        return nodeVisible;
    }

    public void setNodeVisible(boolean nodeVisible) {
        this.nodeVisible = nodeVisible;
    }

    public long getVisibleLevel() {
        return visibleLevel;
    }

    public void setVisibleLevel(long visibleLevel) {
        this.visibleLevel = visibleLevel;
    }

    public ReturnTableType getType() {
        return type;
    }

    public void setType(ReturnTableType type) {
        this.type = type;
    }

    public MDTNodeEvalMethods getEvalType() {
        return evalType;
    }

    public void setEvalType(MDTNodeEvalMethods evalType) {
        this.evalType = evalType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
