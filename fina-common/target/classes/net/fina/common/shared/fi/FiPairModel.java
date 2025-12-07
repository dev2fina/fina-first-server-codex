package net.fina.common.shared.fi;

import net.fina.common.client.fis.FiModel;

import java.util.ArrayList;
import java.util.List;

public class FiPairModel {
    private FiModel parent;
    private List<FiModel> fis;

    public FiPairModel() {
    }

    public FiPairModel(FiModel parent, List<FiModel> fis) {
        this.parent = parent;
        this.fis = fis;
    }

    public FiModel getParent() {
        return parent;
    }

    public void setParent(FiModel parent) {
        this.parent = parent;
    }

    public List<FiModel> getFis() {
        return fis == null ? new ArrayList<FiModel>() : fis;
    }

    public void setFis(List<FiModel> fis) {
        this.fis = fis;
    }
}
