package net.fina.server.returns.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.common.client.constants.CalendarPeriodType;
import net.fina.common.shared.i18n.DescriptionMetaModel;
import net.fina.server.i18n.model.DescriptionModelHelper;
import net.fina.server.returns.entity.PeriodType;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodTypeMetaModel implements Serializable {

    private long id;
    private Integer version;
    private String code;
    protected List<DescriptionMetaModel> description;
    private CalendarPeriodType periodType;

    public PeriodTypeMetaModel() {
    }

    public PeriodTypeMetaModel setEntity(PeriodType periodType) {
        this.id = periodType.getId();
        this.version = periodType.getVersion();
        this.code = periodType.getCode();
        this.description = DescriptionModelHelper.toModel(periodType.getDescription());
        this.periodType = periodType.getPeriodType();
        return this;
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

    public List<DescriptionMetaModel> getDescription() {
        return description;
    }

    public void setDescription(List<DescriptionMetaModel> description) {
        this.description = description;
    }

    public CalendarPeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(CalendarPeriodType periodType) {
        this.periodType = periodType;
    }
}
