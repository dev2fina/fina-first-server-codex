package net.fina.server.matrix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.NodeDataType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubMatrixTableMappingModel {
    private long id;
    private long mdtNodeId;
    private String mdtNodeCode;
    private String mdtNodeDescription;
    private String cell;
    private NodeDataType dataType;
    private long sequence;

    public SubMatrixTableMappingModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMdtNodeId() {
        return mdtNodeId;
    }

    public void setMdtNodeId(long mdtNodeId) {
        this.mdtNodeId = mdtNodeId;
    }

    public String getMdtNodeCode() {
        return mdtNodeCode;
    }

    public void setMdtNodeCode(String mdtNodeCode) {
        this.mdtNodeCode = mdtNodeCode;
    }

    public String getMdtNodeDescription() {
        return mdtNodeDescription;
    }

    public void setMdtNodeDescription(String mdtNodeDescription) {
        this.mdtNodeDescription = mdtNodeDescription;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public NodeDataType getDataType() {
        return dataType;
    }

    public void setDataType(NodeDataType dataType) {
        this.dataType = dataType;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
