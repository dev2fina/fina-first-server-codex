package net.fina.server.mdt.xml.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OPTIONAL", propOrder = {"tableId", "tableType", "evalType"})
public class Optional {

    @XmlElement(name = "TABLE_ID", required = false)
    private long tableId;

    @XmlElement(name = "TABLE_TYPE", required = false)
    private int tableType;

    @XmlElement(name = "TABLE_EVAL_TYPE", required = false)
    private int evalType;

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public int getTableType() {
        return tableType;
    }

    public void setTableType(int tableType) {
        this.tableType = tableType;
    }

    public int getEvalType() {
        return evalType;
    }

    public void setEvalType(int evalType) {
        this.evalType = evalType;
    }
}
