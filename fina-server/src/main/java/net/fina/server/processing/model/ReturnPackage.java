package net.fina.server.processing.model;

import java.util.Date;

public class ReturnPackage {

    private String bankCode;
    private String versionCode;
    private Date periodStart;
    private Date periodEnd;

    // ReturnPackage File id
    private Long fileId;

    public ReturnPackage() {
    }

    public ReturnPackage(String bankCode, String versionCode, Date periodStart, Date periodEnd, Long fileId) {
        this.bankCode = bankCode;
        this.versionCode = versionCode;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.fileId = fileId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnPackage returnPackage = (ReturnPackage) o;

        if (!bankCode.equals(returnPackage.bankCode)) return false;
        if (!periodEnd.equals(returnPackage.periodEnd)) return false;
        if (!periodStart.equals(returnPackage.periodStart)) return false;
        if (!versionCode.equals(returnPackage.versionCode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bankCode.hashCode();
        result = 31 * result + versionCode.hashCode();
        result = 31 * result + periodStart.hashCode();
        result = 31 * result + periodEnd.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[fi='" + bankCode + '\'' +
                ", version='" + versionCode + '\'' +
                ", start=" + periodStart +
                ", end=" + periodEnd +
                ", file=" + fileId +
                ']';
    }
}

