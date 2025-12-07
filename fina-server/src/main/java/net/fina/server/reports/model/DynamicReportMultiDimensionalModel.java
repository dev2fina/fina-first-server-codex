package net.fina.server.reports.model;

import net.fina.common.shared.i18n.DescriptionMetaModel;

import java.util.List;
import java.util.Map;

public class DynamicReportMultiDimensionalModel {
    private List<DescriptionMetaModel> fiDescriptions;
    private Map<Integer, String> periodValues;


    public List<DescriptionMetaModel> getFiDescriptions() {
        return fiDescriptions;
    }

    public void setFiDescriptions(List<DescriptionMetaModel> fiDescriptions) {
        this.fiDescriptions = fiDescriptions;
    }

    public Map<Integer, String> getPeriodValues() {
        return periodValues;
    }

    public void setPeriodValues(Map<Integer, String> periodValues) {
        this.periodValues = periodValues;
    }
}
