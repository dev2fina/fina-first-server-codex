package net.fina.common.shared.report;

import java.util.ArrayList;
import java.util.List;

public class ReportParameterModel {
    private String name;
    private ReportParameterType type;
    private List<Object> values=new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReportParameterType getType() {
        return type;
    }

    public void setType(ReportParameterType type) {
        this.type = type;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }
}
