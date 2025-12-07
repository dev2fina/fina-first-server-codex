package net.fina.server.returns.entity;

import jakarta.persistence.*;
import net.fina.common.client.returns.ProcessStatus;
import org.hibernate.annotations.Immutable;

import java.util.Date;

@Entity
@Immutable // Marks the entity as read-only
@Table(name = "IN_RETURNS_MV")
public class ReturnMaterializedView {

    @Id
    @Column(name = "RETURN_ID")
    private long id;

    @Column(name = "SCHEDULEID")
    private long scheduleId;
    private long versionId;
    @Column(name = "LATEST_VERSION")
    private long latestVersion;
    private long bankId;
    @Column(name = "BANK_CODE")
    private String bankCode;
    private long namestrId;
    private long periodId;
    private Date fromDate;
    private Date toDate;
    private long periodTypeId;
    private String periodTypeCode;
    private long returnTypeId;
    private String returnTypeCode;
    private String versionCode;
    private long definitionId;
    private boolean excelTemplate;
    private boolean manualInput;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private ProcessStatus status;

    public ReturnMaterializedView() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public long getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(long latestVersion) {
        this.latestVersion = latestVersion;
    }

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public long getNamestrId() {
        return namestrId;
    }

    public void setNamestrId(long namestrId) {
        this.namestrId = namestrId;
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

    public long getPeriodTypeId() {
        return periodTypeId;
    }

    public void setPeriodTypeId(long periodTypeId) {
        this.periodTypeId = periodTypeId;
    }

    public String getPeriodTypeCode() {
        return periodTypeCode;
    }

    public void setPeriodTypeCode(String periodTypeCode) {
        this.periodTypeCode = periodTypeCode;
    }

    public long getReturnTypeId() {
        return returnTypeId;
    }

    public void setReturnTypeId(long returnTypeId) {
        this.returnTypeId = returnTypeId;
    }

    public String getReturnTypeCode() {
        return returnTypeCode;
    }

    public void setReturnTypeCode(String returnTypeCode) {
        this.returnTypeCode = returnTypeCode;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(long definitionId) {
        this.definitionId = definitionId;
    }

    public boolean isExcelTemplate() {
        return excelTemplate;
    }

    public void setExcelTemplate(boolean excelTemplate) {
        this.excelTemplate = excelTemplate;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
}
