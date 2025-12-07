package net.fina.server.returns.model;

import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.returns.ReturnTableType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RTableMetaModel implements Serializable, Comparable<RTableMetaModel> {

    private long tableId;
    private String description;
    private ReturnTableType type;
    private MDTNodeEvalMethods evalMethod;
    private long sequence;

    private long visibleLevel;

    private List<RTableRowMetaModel> rows = new ArrayList<>();

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReturnTableType getType() {
        return type;
    }

    public void setType(ReturnTableType type) {
        this.type = type;
    }

    public MDTNodeEvalMethods getEvalMethod() {
        return evalMethod;
    }

    public void setEvalMethod(MDTNodeEvalMethods evalMethod) {
        this.evalMethod = evalMethod;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getVisibleLevel() {
        return visibleLevel;
    }

    public void setVisibleLevel(long visibleLevel) {
        this.visibleLevel = visibleLevel;
    }

    public List<RTableRowMetaModel> getRows() {
        return rows;
    }

    public void setRows(List<RTableRowMetaModel> rows) {
        this.rows = rows;
    }

    @Override
    public int compareTo(RTableMetaModel o) {
        if (o != null) {
            return Long.compare(this.getSequence(), o.getSequence());
        }
        return 0;
    }
}
