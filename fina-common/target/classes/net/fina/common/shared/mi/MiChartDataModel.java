package net.fina.common.shared.mi;

import net.fina.common.client.returns.ReturnModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MiChartDataModel implements Serializable {
    private int awaiting;
    private int inProgress;
    private int total;
    private long totalFormsSubmitted;

    public MiChartDataModel() {
    }

    public MiChartDataModel(int awaiting, int inProgress, int total) {
        this.awaiting = awaiting;
        this.inProgress = inProgress;
        this.total = total;
    }

    public int getAwaiting() {
        return awaiting;
    }

    public void setAwaiting(int awaiting) {
        this.awaiting = awaiting;
    }

    public int getInProgress() {
        return inProgress;
    }

    public void setInProgress(int inProgress) {
        this.inProgress = inProgress;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getTotalFormsSubmitted() {
        return totalFormsSubmitted;
    }

    public void setTotalFormsSubmitted(long totalFormsSubmitted) {
        this.totalFormsSubmitted = totalFormsSubmitted;
    }

    @Override
    public String toString() {
        return "MiChartDataModel{" +
                "awaiting=" + awaiting +
                ", inProgress=" + inProgress +
                ", total=" + total +
                ", totalFormsSubmitted=" + totalFormsSubmitted +
                '}';
    }
}
