package net.fina.server.classifier.model;

import java.io.Serializable;
import java.util.List;

public class MDTCatalogDeleteResultModel implements Serializable {

    private List<String> dependantNodes;

    private List<String> comparisons;


    public MDTCatalogDeleteResultModel(List<String> dependantNodes, List<String> comparisons) {
        this.dependantNodes = dependantNodes;
        this.comparisons = comparisons;
    }


    public List<String> getDependantNodes() {
        return dependantNodes;
    }

    public void setDependantNodes(List<String> dependantNodes) {
        this.dependantNodes = dependantNodes;
    }

    public List<String> getComparisons() {
        return comparisons;
    }

    public void setComparisons(List<String> comparisons) {
        this.comparisons = comparisons;
    }
}
