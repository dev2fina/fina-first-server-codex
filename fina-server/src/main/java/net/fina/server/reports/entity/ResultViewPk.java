package net.fina.server.reports.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Embeddable
public class ResultViewPk implements Serializable {
    @Column(name = "VALUE")
    private String value;

    @Column(name = "NVALUE")
    private float nvalue;

    @Column(name = "NODEID")
    private long nodeId;

    @Column(name = "BANKID")
    private long bankId;

    @Column(name = "PERIODTYPEID")
    private int periodTypeId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FROMDATE")
    private Date fromDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TODATE")
    private Date toDate;

    @Column(name = "PERIODID")
    private int periodId;

    @Column(name = "VERSIONCODE")
    private String versionCode;

    @Column(name = "LATESTVERSIONCODE")
    private String latestVersionCode;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public float getNvalue() {
        return nvalue;
    }

    public void setNvalue(float nvalue) {
        this.nvalue = nvalue;
    }

    public int getPeriodTypeId() {
        return periodTypeId;
    }

    public void setPeriodTypeId(int periodTypeId) {
        this.periodTypeId = periodTypeId;
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

    public int getPeriodId() {
        return periodId;
    }

    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getLatestVersionCode() {
        return latestVersionCode;
    }

    public void setLatestVersionCode(String latestVersionCode) {
        this.latestVersionCode = latestVersionCode;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResultViewPk that = (ResultViewPk) o;

        if (bankId != that.bankId) return false;
        if (nodeId != that.nodeId) return false;
        if (periodId != that.periodId) return false;
        if (periodTypeId != that.periodTypeId) return false;
        if (fromDate != null ? !fromDate.equals(that.fromDate) : that.fromDate != null) return false;
        if (latestVersionCode != null ? !latestVersionCode.equals(that.latestVersionCode) : that.latestVersionCode != null)
            return false;
        if (toDate != null ? !toDate.equals(that.toDate) : that.toDate != null) return false;
        if (versionCode != null ? !versionCode.equals(that.versionCode) : that.versionCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (nodeId ^ (nodeId >>> 32));
        result = 31 * result + (int) (bankId ^ (bankId >>> 32));
        result = 31 * result + periodTypeId;
        result = 31 * result + (fromDate != null ? fromDate.hashCode() : 0);
        result = 31 * result + (toDate != null ? toDate.hashCode() : 0);
        result = 31 * result + periodId;
        result = 31 * result + (versionCode != null ? versionCode.hashCode() : 0);
        result = 31 * result + (latestVersionCode != null ? latestVersionCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResultViewPk [value=" + value + ", nvalue=" + nvalue + ", nodeId=" + nodeId + ", bankId=" + bankId + ", periodTypeId=" + periodTypeId + ", fromDate=" + fromDate + ", toDate=" + toDate + ", periodId=" + periodId + ", versionCode=" + versionCode + ", latestVersionCode=" + latestVersionCode + "]";
    }


}
