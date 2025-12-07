package net.fina.server.reports.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LiveReportMultiDimensionResultMetaModel {

    private Map<String,  List<LiveReportResultMetaModel>> columns = new LinkedHashMap<>();

    public Map<String, List<LiveReportResultMetaModel>> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, List<LiveReportResultMetaModel>> columns) {
        this.columns = columns;
    }
}