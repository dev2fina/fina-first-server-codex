package net.fina.common.shared.report;

import net.fina.common.client.reports.ReportType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ReportModel implements Serializable {

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

    private List<ReportInfoModel> parameters = new ArrayList<ReportInfoModel>();
    private List<ReportInfoModel> iterators = new ArrayList<ReportInfoModel>();

    private Map<String, Integer> needParameters = new HashMap<String, Integer>();
    private Map<String, Integer> needIterators = new HashMap<String, Integer>();
    private String selectedParametersData;
    private String selectedIteratorsData;

    private Map<String, String> needProperties = new HashMap<String, String>();

    public ReportModel() {
    }

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

    public List<ReportInfoModel> getParameters() {
        return parameters;
    }

    public void setParameters(List<ReportInfoModel> parameters) {
        this.parameters = parameters;
    }

    public List<ReportInfoModel> getIterators() {
        return iterators;
    }

    public void setIterators(List<ReportInfoModel> iterators) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportModel that = (ReportModel) o;

        if (id != that.id) return false;
        if (parentId != that.parentId) return false;
        if (type != that.type) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (parentId ^ (parentId >>> 32));
        result = 31 * result + type;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }
}
