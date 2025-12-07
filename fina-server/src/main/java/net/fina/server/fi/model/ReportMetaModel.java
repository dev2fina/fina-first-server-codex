package net.fina.server.fi.model;

import net.fina.common.client.reports.ReportType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportMetaModel {
    private int id;
    private Integer version;
    private long parentId;
    private int type;
    private String code;
    private Integer sequence;
    private long nameStrId;
    private String name;
    private ReportType reportType;

    private boolean userPermission;
    private boolean rolePermission;
    private boolean hasSomeChildrenSelected;

    private List<ReportInfoMetaModel> parameters = new ArrayList<ReportInfoMetaModel>();
    private List<ReportInfoMetaModel> iterators = new ArrayList<ReportInfoMetaModel>();

    private Map<String, Integer> needParameters = new HashMap<String, Integer>();
    private Map<String, Integer> needIterators = new HashMap<String, Integer>();
    private String selectedParametersData;
    private String selectedIteratorsData;

    private Map<String, String> needProperties = new HashMap<String, String>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public long getParentId() {
        return parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
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

    public List<ReportInfoMetaModel> getParameters() {
        return parameters;
    }

    public void setParameters(List<ReportInfoMetaModel> parameters) {
        this.parameters = parameters;
    }

    public List<ReportInfoMetaModel> getIterators() {
        return iterators;
    }

    public void setIterators(List<ReportInfoMetaModel> iterators) {
        this.iterators = iterators;
    }

    public boolean isRolePermission() {
        return rolePermission;
    }

    public void setRolePermission(boolean rolePermission) {
        this.userPermission = rolePermission;
        this.rolePermission = rolePermission;
    }

    public void setOnlyRolePermission(boolean rolePermission) {
        this.rolePermission = rolePermission;
    }

    public boolean isUserPermission() {
        return userPermission;
    }

    public void setUserPermission(boolean userPermission) {
        this.userPermission = this.rolePermission | userPermission;
    }

    public boolean isHasSomeChildrenSelected() {
        return hasSomeChildrenSelected;
    }

    public void setHasSomeChildrenSelected(boolean hasSomeChildrenSelected) {
        this.hasSomeChildrenSelected = hasSomeChildrenSelected;
    }

    public Map<String, Integer> getNeedParameters() {
        return needParameters;
    }

    public void setNeedParameters(Map<String, Integer> needParameters) {
        this.needParameters = needParameters;
    }

    public Map<String, Integer> getNeedIterators() {
        return needIterators;
    }

    public void setNeedIterators(Map<String, Integer> needIterators) {
        this.needIterators = needIterators;
    }

    public String getSelectedIteratorsData() {
        return selectedIteratorsData;
    }

    public void setSelectedIteratorsData(String selectedIteratorsData) {
        this.selectedIteratorsData = selectedIteratorsData;
    }

    public String getSelectedParametersData() {
        return selectedParametersData;
    }

    public void setSelectedParametersData(String selectedParametersData) {
        this.selectedParametersData = selectedParametersData;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public Map<String, String> getNeedProperties() {
        return needProperties;
    }

    public void setNeedProperties(Map<String, String> needProperties) {
        this.needProperties = needProperties;
    }
}
