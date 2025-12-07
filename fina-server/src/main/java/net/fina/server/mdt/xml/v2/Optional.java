package net.fina.server.mdt.xml.v2;

import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.returns.ReturnTableType;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OPTIONAL", propOrder = {"tableCode", "tableType", "evalType", "tableSequence"})
public class Optional {

    @XmlAttribute(name = "CODE")
    private String tableCode;

    @XmlAttribute(name = "TYPE")
    private ReturnTableType tableType;

    @XmlAttribute(name = "EVAL_TYPE")
    private MDTNodeEvalMethods evalType;

    @XmlAttribute(name = "SEQUENCE")
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
