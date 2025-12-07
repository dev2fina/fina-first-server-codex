package net.fina.server.dashboard.dynamic.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class DashboardModel {
    private long id;

    private String name;
    private long nameStrId;

    private byte columnSize;
    private boolean isDefault;
    private List<DashletModel> dashletList;
    private String configJson;
    private String code;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public byte getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(byte columnSize) {
        this.columnSize = columnSize;
    }

    @JsonProperty("isDefault")
    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        this.isDefault = aDefault;
    }

    public List<DashletModel> getDashletList() {
        return dashletList == null ? new ArrayList<>() : dashletList;
    }

    public void setDashletList(List<DashletModel> dashletList) {
        this.dashletList = dashletList;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
