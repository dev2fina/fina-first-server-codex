package net.fina.common.client.returns;

import java.io.Serializable;
import java.util.Date;

import net.fina.common.client.constants.FilterPeriodType;

@SuppressWarnings("serial")
public class ImportedFileFilterConfig implements Serializable {
    private Date importStart;
    private Date importEnd;
    private Date periodStart;
    private Date periodEnd;
    private FilterPeriodType periodType;

    public Date getImportStart() {
        return importStart;
    }

    public void setImportStart(Date importStart) {
        this.importStart = importStart;
    }

    public Date getImportEnd() {
        return importEnd;
    }

    public void setImportEnd(Date importEnd) {
        this.importEnd = importEnd;
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

    public FilterPeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(FilterPeriodType periodType) {
        this.periodType = periodType;
    }

    @Override
    public String toString() {
        return "ImportedFileFilterConfig [importStart=" + importStart + ", importEnd=" + importEnd + ", periodStart=" + periodStart + ", periodEnd=" + periodEnd + ", periodType=" + periodType + "]";
    }

}