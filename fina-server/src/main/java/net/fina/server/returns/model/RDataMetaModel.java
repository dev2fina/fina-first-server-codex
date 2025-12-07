package net.fina.server.returns.model;

import net.fina.common.client.returns.ProcessStatus;

import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RDataMetaModel implements Serializable {

    private long returnId;
    private ProcessStatus status;
    private String statusName;

    //Version
    private long versionId;
    private String versionCode;
    private String versionDescription;

    //Period
    private long periodId;
    private Date fromDate;
    private Date toDate;
    private String periodTypeCode;
    private String periodTypeDescription;

    //FI
    private long fiId;
    private String fiCode;
    private String fiDescription;

    //Returtn Definition
    private long definitionId;
    private String returnCode;
    private String returnDescription;
    private String returnTypeCode;

    //User
    private String userLogin;
    private String userName;

    private Collection<RTableMetaModel> tables;

    private Collection<MComparisonMetaModel> comparisons;

    private Collection<RItemMetaModel> dependencies;

    private List<MDependentMetaModel> dependentNodes;

    @XmlTransient
    private Map<String, Map<Long, RItemMetaModel>> itemsByCodesMap;

    @XmlTransient
    private Map<Long, String> codeByIdMap;

    private boolean readonly;

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(long periodId) {
        this.periodId = periodId;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getPeriodTypeCode() {
        return periodTypeCode;
    }

    public void setPeriodTypeCode(String periodTypeCode) {
        this.periodTypeCode = periodTypeCode;
    }

    public String getPeriodTypeDescription() {
        return periodTypeDescription;
    }

    public void setPeriodTypeDescription(String periodTypeDescription) {
        this.periodTypeDescription = periodTypeDescription;
    }

    public long getFiId() {
        return fiId;
    }

    public void setFiId(long fiId) {
        this.fiId = fiId;
    }

    public String getFiCode() {
        return fiCode;
    }

    public void setFiCode(String fiCode) {
        this.fiCode = fiCode;
    }

    public String getFiDescription() {
        return fiDescription;
    }

    public void setFiDescription(String fiDescription) {
        this.fiDescription = fiDescription;
    }

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(long definitionId) {
        this.definitionId = definitionId;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnDescription() {
        return returnDescription;
    }

    public void setReturnDescription(String returnDescription) {
        this.returnDescription = returnDescription;
    }

    public String getReturnTypeCode() {
        return returnTypeCode;
    }

    public void setReturnTypeCode(String returnTypeCode) {
        this.returnTypeCode = returnTypeCode;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Collection<RTableMetaModel> getTables() {
        return tables;
    }

    public void setTables(Collection<RTableMetaModel> tables) {
        this.tables = tables;
    }

    public Collection<MComparisonMetaModel> getComparisons() {
        return comparisons;
    }

    public void setComparisons(Collection<MComparisonMetaModel> comparisons) {
        this.comparisons = comparisons;
    }

    public Collection<RItemMetaModel> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Collection<RItemMetaModel> dependencies) {
        this.dependencies = dependencies;
    }

    public List<MDependentMetaModel> getDependentNodes() {
        return dependentNodes;
    }

    public void setDependentNodes(List<MDependentMetaModel> dependentNodes) {
        this.dependentNodes = dependentNodes;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @XmlTransient
    public Map<String, Map<Long, RItemMetaModel>> getItemsByCodesMap() {
        return itemsByCodesMap;
    }

    public void setItemsByCodesMap(Map<String, Map<Long, RItemMetaModel>> itemsByCodesMap) {
        this.itemsByCodesMap = itemsByCodesMap;
    }

    @XmlTransient
    public Map<Long, String> getCodeByIdMap() {
        return codeByIdMap;
    }

    public void setCodeByIdMap(Map<Long, String> codeByIdMap) {
        this.codeByIdMap = codeByIdMap;
    }
}
