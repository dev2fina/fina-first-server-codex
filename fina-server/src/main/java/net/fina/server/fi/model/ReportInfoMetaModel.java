package net.fina.server.fi.model;

import java.util.List;

public class ReportInfoMetaModel {
    private int id;
    private String name;
    private String type;
    private List<String> values;
    private List<String> invalidValues;

    private static int COUNTER = 0;

    public ReportInfoMetaModel() {
        this.id = COUNTER++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<String> getInvalidValues() {
        return invalidValues;
    }

    public void setInvalidValues(List<String> invalidValues) {
        this.invalidValues = invalidValues;
    }
}
