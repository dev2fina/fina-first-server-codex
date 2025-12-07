package net.fina.server.fi.model;

import net.fina.server.fi.model.configuration.FiConfigurationStepMetaModel;

import java.util.List;

public class ManagementMetaModel {
    private long id;
    private String code;
    private Integer version;
    private long nameStrId;
    private String name;
    private List<FiConfigurationStepMetaModel> steps;

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FiConfigurationStepMetaModel> getSteps() {
        return steps;
    }

    public void setSteps(List<FiConfigurationStepMetaModel> steps) {
        this.steps = steps;
    }
}
