package net.fina.common.client.rvc;

import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.returns.ReturnTableType;

public class OptionalXmlMetaModel {

    private String tableCode;
    private ReturnTableType tableType;
    private MDTNodeEvalMethods evalType;
    private Long tableSequence;

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public ReturnTableType getTableType() {
        return tableType;
    }

    public void setTableType(ReturnTableType tableType) {
        this.tableType = tableType;
    }

    public MDTNodeEvalMethods getEvalType() {
        return evalType;
    }

    public void setEvalType(MDTNodeEvalMethods evalType) {
        this.evalType = evalType;
    }

    public Long getTableSequence() {
        return tableSequence;
    }

    public void setTableSequence(Long tableSequence) {
        this.tableSequence = tableSequence;
    }
}
