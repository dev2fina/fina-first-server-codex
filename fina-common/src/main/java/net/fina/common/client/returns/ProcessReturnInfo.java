package net.fina.common.client.returns;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProcessReturnInfo implements Serializable {

    private Long langId;
    private Long userId;
    private Long returnId;
    private boolean reprocess;
    private Long returnVersionId;
    private String numberFormat;
    private String dateFormat;
    private String dateTimeFormat;

    public ProcessReturnInfo() {

    }

    public ProcessReturnInfo(Long langId, Long userId, Long returnId, boolean reprocess, Long returnVersionId, String numberFormat, String dateFormat, String dateTimeFormat) {
        this.langId = langId;
        this.userId = userId;
        this.returnId = returnId;
        this.reprocess = reprocess;
        this.returnVersionId = returnVersionId;
        this.numberFormat = numberFormat;
        this.dateFormat = dateFormat;
        this.dateTimeFormat = dateTimeFormat;
    }

    public Long getLangId() {
        return langId;
    }

    public void setLangId(Long langId) {
        this.langId = langId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getReturnId() {
        return returnId;
    }

    public void setReturnId(Long returnId) {
        this.returnId = returnId;
    }

    public boolean isReprocess() {
        return reprocess;
    }

    public void setReprocess(boolean reprocess) {
        this.reprocess = reprocess;
    }

    public Long getReturnVersionId() {
        return returnVersionId;
    }

    public void setReturnVersionId(Long returnVersionId) {
        this.returnVersionId = returnVersionId;
    }

    public String getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((returnId == null) ? 0 : returnId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessReturnInfo other = (ProcessReturnInfo) obj;
        if (returnId == null) {
            if (other.returnId != null)
                return false;
        } else if (!returnId.equals(other.returnId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ProcessReturnInfo [langId=" + langId + ", userId=" + userId + ", returnId=" + returnId + ", reprocess=" + reprocess + ", returnVersionId=" + returnVersionId + ", numberFormat=" + numberFormat + ", dateFormat=" + dateFormat + ", dateTimeFormat=" + dateTimeFormat + "]";
    }


}
