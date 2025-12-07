package net.fina.server.dashboard.dynamic.model;

import java.util.ArrayList;
import java.util.List;

public class DashletModel {
    private long id;
    private String name;
    private long nameStrId;
    private String dataQuery;
    private String metaInfoJson;

    private List<String> dataColumns;

    private List<String> filters;

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

    public String getDataQuery() {
        return dataQuery;
    }

    public void setDataQuery(String dataQuery) {
        this.dataQuery = dataQuery;
    }

    public String getMetaInfoJson() {
        return metaInfoJson;
    }

    public void setMetaInfoJson(String metaInfoJson) {
        this.metaInfoJson = metaInfoJson;
    }

    public List<String> getDataColumns() {
        if (dataColumns == null){
            dataColumns = new ArrayList<>();
        }
        return dataColumns;
    }

    public void setDataColumns(List<String> dataColumns) {
        this.dataColumns = dataColumns;
    }

    public List<String> getFilters() {
        if (filters == null){
            filters = new ArrayList<>();
        }
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
