package net.fina.common.client.returns;

import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.shared.mdt.MDTNodeModel;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DefinitionTableModel implements Serializable {

    private long id;
    private long definitionId;
    private Integer version;
    private String code;
    private MDTNodeModel node;
    private boolean nodeVisible;
    private long visibleLevel;
    private ReturnTableType type;
    private MDTNodeEvalMethods evalType;
    private long sequence;

    private static int COUNTER = 0;
    private int innerId;


    public DefinitionTableModel() {
        super();
        innerId = COUNTER++;
    }

    public DefinitionTableModel(long id, String code, ReturnTableType type) {
        this.id = id;
        this.code = code;
        this.type = type;
    }

    public int getInnerId() {
        return innerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MDTNodeModel getNode() {
        return node;
    }

    public void setNode(MDTNodeModel node) {
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

    public void setVisibleLevel(Long visibleLevel) {
        if (visibleLevel != null) {
            this.visibleLevel = visibleLevel;
        }
    }

    public ReturnTableType getType() {
        return type;
    }

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(long definitionId) {
        this.definitionId = definitionId;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public void setType(ReturnTableType type) {
        this.type = type;
        if (type == ReturnTableType.NT || type == ReturnTableType.MCT) {
            this.setEvalType(MDTNodeEvalMethods.UNKNOWN);
        }
    }

    public MDTNodeEvalMethods getEvalType() {
        return evalType;
    }

    public void setEvalType(MDTNodeEvalMethods evalType) {
        if (this.getType() == ReturnTableType.MCT || this.getType() == ReturnTableType.NT) {
            evalType = MDTNodeEvalMethods.UNKNOWN;
        }
        this.evalType = evalType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + innerId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefinitionTableModel other = (DefinitionTableModel) obj;
        if (id != other.id)
            return false;
        if (innerId != other.innerId)
            return false;
        return true;
    }

}
