package net.fina.server.reports.model;

import java.util.List;

public class DynamicReportMultiDimensionalResultModel {
    private String formula;
    private List<DynamicReportMultiDimensionalModel> dataModel;

    public DynamicReportMultiDimensionalResultModel() {
    }

    public DynamicReportMultiDimensionalResultModel(String formula, List<DynamicReportMultiDimensionalModel> dataModel) {
        this.formula = formula;
        this.dataModel = dataModel;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public List<DynamicReportMultiDimensionalModel> getDataModel() {
        return dataModel;
    }

    public void setDataModel(List<DynamicReportMultiDimensionalModel> dataModel) {
        this.dataModel = dataModel;
    }
}
