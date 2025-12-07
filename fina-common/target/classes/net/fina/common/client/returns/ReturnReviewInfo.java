package net.fina.common.client.returns;

import java.io.Serializable;
import java.util.Date;

/**
 * Class to handle information about return
 *
 * @author dato.java
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ReturnReviewInfo implements Serializable {

    private Long returnId;
    private Integer versionId;
    private String defCode;
    private String defName;
    private String fiCode;
    private String fiName;
    private Date periodFrom;
    private Date periodTo;
    private String periodCode;
    private String periodName;
    private String versionCode;
    private String versionName;
    private String statusName;
    private String htmlContent;

    public Long getReturnId() {
        return returnId;
    }

    public void setReturnId(Long returnId) {
        this.returnId = returnId;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public String getDefCode() {
        return defCode;
    }

    public void setDefCode(String defCode) {
        this.defCode = defCode;
    }

    public String getDefName() {
        return defName;
    }

    public void setDefName(String defName) {
        this.defName = defName;
    }

    public String getFiCode() {
        return fiCode;
    }

    public void setFiCode(String fiCode) {
        this.fiCode = fiCode;
    }

    public String getFiName() {
        return fiName;
    }

    public void setFiName(String fiName) {
        this.fiName = fiName;
    }

    public Date getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    public Date getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
    }

    public String getPeriodCode() {
        return periodCode;
    }

    public void setPeriodCode(String periodCode) {
        this.periodCode = periodCode;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    @Override
    public String toString() {
        return "ReturnReviewInfo [returnId=" + returnId + ", versionId=" + versionId + ", defCode=" + defCode + ", defName=" + defName + ", fiCode=" + fiCode + ", fiName=" + fiName + ", periodFrom=" + periodFrom + ", periodTo=" + periodTo + ", periodCode=" + periodCode + ", periodName=" + periodName + ", versionCode=" + versionCode + ", versionName=" + versionName + ", statusName=" + statusName + "]";
    }

}
