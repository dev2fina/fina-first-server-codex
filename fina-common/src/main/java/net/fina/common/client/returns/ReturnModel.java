package net.fina.common.client.returns;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 7/3/13
 * Time: 3:50 PM
 */
public class ReturnModel implements Serializable {
    private long id;
    private long fiId;
    private String fiCode;
    private String fiDescription;
    private Map<Long, String> fiDescriptionMap;
    private long periodId;
    private Date fromDate;
    private Date toDate;
    private long periodTypeId;
    private String periodTypeCode;
    private long definitionId;
    private String definitionCode;
    private String definitionDescription;
    private long returnTypeId;
    private String returnTypeCode;
    private long statusId;
    private ProcessStatus status;
    private String statusDescription;
    private Date statusDate;
    private long versionId;
    private String versionCode;

    private String label;

    private boolean group;
    private int hash;
    private int delay;
    private int delayHour;
    private int delayMinute;
    private boolean create;

    private int identifier;
    private boolean excelTemplate;
    private long scheduleId;
    private boolean isReg;

    public ReturnModel() {
    }

    public ReturnModel(long id) {
        this.id = id;
    }

    public ReturnModel(long id, long scheduleId, long versionId) {
        this.id = id;
        this.scheduleId = scheduleId;
        this.versionId = versionId;
    }

    public ReturnModel(long fiId, String fiCode, long periodId, Date toDate, long periodTypeId, String periodTypeCode, long returnTypeId, String returnTypeCode, long versionId, String versionCode) {
        this.fiId = fiId;
        this.fiCode = fiCode;
        this.periodId = periodId;
        this.toDate = toDate;
        this.periodTypeId = periodTypeId;
        this.periodTypeCode = periodTypeCode;
        this.returnTypeId = returnTypeId;
        this.returnTypeCode = returnTypeCode;
        this.versionId = versionId;
        this.versionCode = versionCode;
        this.label = fiCode + " " + new SimpleDateFormat("dd-MM-yyyy").format(toDate) + " " + periodTypeCode + " " + returnTypeCode + " " + versionCode;
        setGroup(true);
    }

    public ReturnModel(int delay, int delayHour, int delayMinute, long fiId, String fiCode, long periodId, Date toDate, long periodTypeId, String periodTypeCode, long returnTypeId, String returnTypeCode, long versionId, String versionCode) {
        this.delay = delay;
        this.delayHour = delayHour;
        this.delayMinute = delayMinute;
        this.fiId = fiId;
        this.fiCode = fiCode;
        this.periodId = periodId;
        this.toDate = toDate;
        this.periodTypeId = periodTypeId;
        this.periodTypeCode = periodTypeCode;
        this.returnTypeId = returnTypeId;
        this.returnTypeCode = returnTypeCode;
        this.versionId = versionId;
        this.versionCode = versionCode;
        this.label = fiCode + " " + new SimpleDateFormat("dd-MM-yyyy").format(toDate) + " " + periodTypeCode + " " + returnTypeCode + " " + versionCode;
    }

    public ReturnModel(int delay, int delayHour, int delayMinute, long fiId, String fiCode, long periodId, Date toDate, long periodTypeId, String periodTypeCode, long returnTypeId, String returnTypeCode, long versionId, String versionCode, String definitionCode) {
        this.delay = delay;
        this.delayHour = delayHour;
        this.delayMinute = delayMinute;
        this.fiId = fiId;
        this.fiCode = fiCode;
        this.periodId = periodId;
        this.toDate = toDate;
        this.periodTypeId = periodTypeId;
        this.periodTypeCode = periodTypeCode;
        this.returnTypeId = returnTypeId;
        this.returnTypeCode = returnTypeCode;
        this.versionId = versionId;
        this.versionCode = versionCode;
        this.definitionCode = definitionCode;
        this.label = fiCode + " " + new SimpleDateFormat("dd-MM-yyyy").format(toDate) + " " + periodTypeCode + " " + returnTypeCode + " " + versionCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Map<Long, String> getFiDescriptionMap() {
        return this.fiDescriptionMap;
    }

    public void setFiDescriptionMap(Map<Long, String> descriptionMap) {
        this.fiDescriptionMap = descriptionMap;
    }

    public String getFiDescription() {
        return fiDescription;
    }

    public void setFiDescription(String fiDescription) {
        this.fiDescription = fiDescription;
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

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(long definitionId) {
        this.definitionId = definitionId;
    }

    public String getDefinitionCode() {
        return definitionCode;
    }

    public void setDefinitionCode(String definitionCode) {
        this.definitionCode = definitionCode;
    }

    public String getDefinitionDescription() {
        return definitionDescription;
    }

    public void setDefinitionDescription(String definitionDescription) {
        this.definitionDescription = definitionDescription;
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

    public long getStatusId() {
        return statusId;
    }

    public void setStatusId(long statusId) {
        this.statusId = statusId;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public boolean isExcelTemplate() {
        return excelTemplate;
    }

    public void setExcelTemplate(boolean excelTemplate) {
        this.excelTemplate = excelTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnModel that = (ReturnModel) o;

        if (hashCode() != that.hashCode()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        if (hash != 0) {
            result = hash;
        } else {
            if (!isGroup()) {
                result = (int) (id ^ (id >>> 32));
            } else {
                result = (int) (fiId ^ (fiId >>> 32));
                result = 31 * result + (int) (periodId ^ (periodId >>> 32));
                result = 31 * result + (int) (returnTypeId ^ (returnTypeId >>> 32));
                result = 31 * result + (int) (versionId ^ (versionId >>> 32));
            }
            hash = result;
        }
        return result;
    }

    @Override
    public String toString() {
        return "ReturnModel{" +
                "id=" + id +
                ", fiId=" + fiId +
                ", periodId=" + periodId +
                ", returnTypeId=" + returnTypeId +
                ", versionId=" + versionId +
                '}';
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelayHour() {
        return delayHour;
    }

    public void setDelayHour(int delayHour) {
        this.delayHour = delayHour;
    }

    public int getDelayMinute() {
        return delayMinute;
    }

    public void setDelayMinute(int delayMinute) {
        this.delayMinute = delayMinute;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public boolean isReg() {
        return isReg;
    }

    public void setReg(boolean reg) {
        isReg = reg;
    }

    public ReturnModel create() {
        this.setCreate(true);
        return this;
    }

    public ReturnModel group(){
        this.setGroup(true);
        return this;
    }
}
