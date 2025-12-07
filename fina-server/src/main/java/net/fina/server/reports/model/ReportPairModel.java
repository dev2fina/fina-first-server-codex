package net.fina.server.reports.model;

import net.fina.reporting.model.ReportMetaModel;

import java.util.ArrayList;
import java.util.List;

public class ReportPairModel {
    private net.fina.reporting.model.ReportMetaModel parent;
    private List<net.fina.reporting.model.ReportMetaModel> children;

    public ReportPairModel() {
    }

    public ReportPairModel(net.fina.reporting.model.ReportMetaModel parent, List<net.fina.reporting.model.ReportMetaModel> children) {
        this.parent = parent;
        this.children = children;
    }

    public net.fina.reporting.model.ReportMetaModel getParent() {
        return parent;
    }

    public void setParent(net.fina.reporting.model.ReportMetaModel parent) {
        this.parent = parent;
    }

    public List<net.fina.reporting.model.ReportMetaModel> getChildren() {
        return children == null ? new ArrayList<>() : children;
    }

    public void setChildren(List<ReportMetaModel> children) {
        this.children = children;
    }
}
