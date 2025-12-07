package net.fina.server.dashboard.legacy.model;

import java.util.Date;

public class LegacyAggregatedNodeData {

    private double val;
    private Date toDate;

    public LegacyAggregatedNodeData() {
    }

    public LegacyAggregatedNodeData(double val, Date toDate) {
        this.val = val;
        this.toDate = toDate;
    }

    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
