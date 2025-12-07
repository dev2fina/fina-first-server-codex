package net.fina.server.fi.model.configuration;

import java.util.List;

public class FiConfigurationStepMetaModel {
    private int index;
    private String name;
    private long nameStrId;
    private List<FiConfigurationStepColumnMetaModel> columns;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FiConfigurationStepColumnMetaModel> getColumns() {
        return columns;
    }

    public void setColumns(List<FiConfigurationStepColumnMetaModel> columns) {
        this.columns = columns;
    }
}
