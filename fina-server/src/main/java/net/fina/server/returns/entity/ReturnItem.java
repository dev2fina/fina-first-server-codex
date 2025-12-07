package net.fina.server.returns.entity;

import net.fina.server.mdt.entity.MDTNode;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity(name = "IN_RETURN_ITEMS")
@Table(name = "IN_RETURN_ITEMS")
@IdClass(ReturnItemId.class)
public class ReturnItem implements Serializable {

    @Id
    @SequenceGenerator(name = "in_return_items_sequence", sequenceName = "in_return_items_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_return_items_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Id
    @OneToOne
    @JoinColumn(name = "RETURNID")
    private Return returns;

    @Id
    @OneToOne
    @JoinColumn(name = "NODEID")
    private MDTNode mdtNode;

    @Id
    @OneToOne
    @JoinColumn(name = "VERSIONID")
    private ReturnVersion returnVersion;

    @Id
    @Column(name = "TABLEID")
    private long tableId;

    @Id
    @Column(name = "ROWNUMBER")
    private long rowNumber;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "NVALUE")
    private double nValue;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getnValue() {
        return nValue;
    }

    public void setnValue(double nValue) {
        this.nValue = nValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Return getReturns() {
        return returns;
    }

    public void setReturns(Return returns) {
        this.returns = returns;
    }

    public MDTNode getMdtNode() {
        return mdtNode;
    }

    public void setMdtNode(MDTNode mdtNode) {
        this.mdtNode = mdtNode;
    }

    public ReturnVersion getReturnVersion() {
        return returnVersion;
    }

    public void setReturnVersion(ReturnVersion returnVersion) {
        this.returnVersion = returnVersion;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(long rowNumber) {
        this.rowNumber = rowNumber;
    }

    @Override
    public String toString() {
        return "ReturnItem{" +
                "id=" + id +
                ", returns=" + returns +
                ", mdtNode=" + mdtNode +
                ", returnVersion=" + returnVersion +
                ", tableId=" + tableId +
                ", rowNumber=" + rowNumber +
                ", value='" + value + '\'' +
                ", nValue=" + nValue +
                '}';
    }
}
