package net.fina.server.mdt.model;

import net.fina.common.client.mdt.MDTDependency;

import java.io.Serializable;
import java.util.List;

public class MdtNodeDependenciesModel implements Serializable {
    private List<MDTDependency> nodes;
    private List<MDTDependency> tables;

    public MdtNodeDependenciesModel() {
    }

    public MdtNodeDependenciesModel(List<MDTDependency> nodes, List<MDTDependency> tables) {
        this.nodes = nodes;
        this.tables = tables;
    }

    public List<MDTDependency> getNodes() {
        return nodes;
    }

    public void setNodes(List<MDTDependency> nodes) {
        this.nodes = nodes;
    }

    public List<MDTDependency> getTables() {
        return tables;
    }

    public void setTables(List<MDTDependency> tables) {
        this.tables = tables;
    }
}
