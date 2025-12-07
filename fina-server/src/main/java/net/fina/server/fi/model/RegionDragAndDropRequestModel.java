package net.fina.server.fi.model;

import java.util.List;

public class RegionDragAndDropRequestModel {
    private RegionMetaModel parent;
    private List<RegionMetaModel> children;

    public RegionMetaModel getParent() {
        return parent;
    }

    public void setParent(RegionMetaModel parent) {
        this.parent = parent;
    }

    public List<RegionMetaModel> getChildren() {
        return children;
    }

    public void setChildren(List<RegionMetaModel> children) {
        this.children = children;
    }
}
