package net.fina.server.reports.model;

import net.fina.common.shared.i18n.DescriptionMetaModel;

import java.util.List;

public class LiveReportResultMetaModel {

    private String code;
    protected List<DescriptionMetaModel> descriptions;
    private double value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<DescriptionMetaModel> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DescriptionMetaModel> descriptions) {
        this.descriptions = descriptions;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}