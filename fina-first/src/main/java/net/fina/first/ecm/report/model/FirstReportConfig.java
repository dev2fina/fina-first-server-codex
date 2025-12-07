package net.fina.first.ecm.report.model;

import java.util.List;
import java.util.Map;

public class FirstReportConfig {
    private String fileName;
    private String langCode;
    private int startRow;
    private int startColumn;
    private byte[] template;
    private boolean enableRowNumbering;
    private String dateCellAddress;
    private List<Map<String, Object>> data;

    public FirstReportConfig() {
    }

    public String getFileName() {
        return fileName;
    }

    public FirstReportConfig fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getLangCode() {
        return langCode;
    }

    public FirstReportConfig langCode(String langCode) {
        this.langCode = langCode;
        return this;
    }


    public int getStartRow() {
        return startRow;
    }

    public FirstReportConfig startRow(int startRow) {
        this.startRow = startRow;
        return this;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public FirstReportConfig startColumn(int startColumn) {
        this.startColumn = startColumn;
        return this;
    }

    public byte[] getTemplate() {
        return template;
    }

    public FirstReportConfig template(byte[] template) {
        this.template = template;
        return this;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public FirstReportConfig data(List<Map<String, Object>> data) {
        this.data = data;
        return this;
    }

    public FirstReportConfig enableRowNumbering(boolean enableRowNumbering) {
        this.enableRowNumbering = enableRowNumbering;
        return this;
    }

    public boolean isEnableRowNumbering() {
        return enableRowNumbering;
    }

    public FirstReportConfig dateCellAddress(String dateCellAddress) {
        this.dateCellAddress = dateCellAddress;
        return this;
    }

    public String getDateCellAddress() {
        return dateCellAddress;
    }

}
