package net.fina.server.fi.model;


import java.util.ArrayList;
import java.util.List;

public class SimpleFiPairModel {
    private FiSimpleModel parent;
    private List<?> fis;

    public SimpleFiPairModel() {
    }



    public SimpleFiPairModel(FiSimpleModel parent, List<?> fis) {
        this.parent = parent;
        this.fis = fis;
    }

    public FiSimpleModel getParent() {
        return parent;
    }

    public void setParent(FiSimpleModel parent) {
        this.parent = parent;
    }

    public List<?> getFis() {
        return fis == null ? new ArrayList<>() : fis;
    }

    public void setFis(List<FiSimpleModel> fis) {
        this.fis = fis;
    }
}
