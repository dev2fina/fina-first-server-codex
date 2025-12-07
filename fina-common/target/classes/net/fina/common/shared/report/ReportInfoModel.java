package net.fina.common.shared.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportInfoModel implements Serializable {
    private static int COUNTER = 0;
    private int id;
    private String name;
    private ReportParameterType type;
    private List<String> values;
    private List<String> invalidValues;
    private VctIteratorInfo vctIteratorInfo;

    public ReportInfoModel() {
        this.id = COUNTER++;
    }

    public ReportInfoModel(String name, ReportParameterType type) {
        this.name = name;
        this.type = type;
    }

    public ReportInfoModel(String name, int typeId) {
        this.name = name;
        this.type = ReportParameterType.fromId(typeId);
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

    public ReportParameterType getType() {
        return type;
    }

    public void setType(ReportParameterType type) {
        this.type = type;
    }

    public List<String> getValues() {
        return values == null ? new ArrayList<>() : values;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportInfoModel that = (ReportInfoModel) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "[name=" + name + ", type=" + type + ", values=" + values + "]";
    }

    public VctIteratorInfo getVctIteratorInfo() {
        return vctIteratorInfo;
    }

    public void setVctIteratorInfo(VctIteratorInfo vctIteratorInfo) {
        this.vctIteratorInfo = vctIteratorInfo;
    }
}
