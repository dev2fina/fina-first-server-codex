package net.fina.common.shared.report;

import java.io.Serializable;
import java.util.List;

public class VctIteratorInfo implements Serializable {

    private String tableName;
    private String groupBy;
    private String aggregateBy;
    private String skipRowCondition;
    private String groupByDefinitionCode;
    private String versionCode;
    private String periodParameter;
    private String aggregateParameter;
    private List<String> periodParameterValues;
    private List<String> aggregateValues;


    public VctIteratorInfo(){}



    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getAggregateBy() {
        return aggregateBy;
    }

    public void setAggregateBy(String aggregateBy) {
        this.aggregateBy = aggregateBy;
    }

    public String getSkipRowCondition() {
        return skipRowCondition;
    }

    public void setSkipRowCondition(String skipRowCondition) {
        this.skipRowCondition = skipRowCondition;
    }

    public String getGroupByDefinitionCode() {
        return groupByDefinitionCode;
    }

    public void setGroupByDefinitionCode(String groupByDefinitionCode) {
        this.groupByDefinitionCode = groupByDefinitionCode;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getPeriodParameter() {
        return periodParameter;
    }

    public void setPeriodParameter(String periodParameter) {
        this.periodParameter = periodParameter;
    }

    public String getAggregateParameter() {
        return aggregateParameter;
    }

    public void setAggregateParameter(String aggregateParameter) {
        this.aggregateParameter = aggregateParameter;
    }

    public List<String> getPeriodParameterValues() {
        return periodParameterValues;
    }

    public void setPeriodParameterValues(List<String> periodParameterValues) {
        this.periodParameterValues = periodParameterValues;
    }

    public List<String> getAggregateValues() {
        return aggregateValues;
    }

    public void setAggregateValues(List<String> aggregateValues) {
        this.aggregateValues = aggregateValues;
    }
}
