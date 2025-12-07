package net.fina.server.fi.model;

import net.fina.server.fi.model.configuration.FiConfigurationStepMetaModel;

import java.util.List;

public class FiBranchTypeMetaModel {
    private long id;
    private String code;
    private String name;
    private long nameStrId;
    private List<FiConfigurationStepMetaModel> steps;
    private long count;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public List<FiConfigurationStepMetaModel> getSteps() {
        return steps;
    }

    public void setSteps(List<FiConfigurationStepMetaModel> steps) {
        this.steps = steps;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}

