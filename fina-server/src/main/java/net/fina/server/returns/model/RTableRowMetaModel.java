package net.fina.server.returns.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RTableRowMetaModel implements Serializable {

    private List<RItemMetaModel> rowItems = new ArrayList<>();

    public List<RItemMetaModel> getRowItems() {
        return rowItems;
    }

    public void setRowItems(List<RItemMetaModel> rowItems) {
        this.rowItems = rowItems;
    }
}
