package net.fina.common.client.returns;

import net.fina.common.client.constants.CalendarPeriodType;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PeriodTypeModel implements Serializable {

    private long id;
    private Integer version;
    private String code;
    private long nameStrId;
    private String name;
    private CalendarPeriodType periodType;

    public PeriodTypeModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public CalendarPeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(CalendarPeriodType periodType) {
        this.periodType = periodType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
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
        PeriodTypeModel other = (PeriodTypeModel) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
