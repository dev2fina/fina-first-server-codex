package net.fina.server.returns.entity;

import java.io.Serializable;

import jakarta.persistence.*;

import net.fina.auditlog.api.Audited;
import net.fina.common.client.constants.CalendarPeriodType;
import net.fina.server.i18n.helper.Description;

import org.hibernate.annotations.Type;

@Entity(name = "IN_PERIOD_TYPES")
@Table(name = "IN_PERIOD_TYPES")
@NamedQueries({@NamedQuery(name = "loadperiodtypes", query = "select r from IN_PERIOD_TYPES as r ")})
public class PeriodType implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_period_types_sequence", sequenceName = "in_period_types_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_period_types_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "CODE", length = 12, unique = true, nullable = false)
    private String code;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description description;

    @Column(name = "PERIODTYPE")
    @Enumerated(EnumType.ORDINAL)
    private CalendarPeriodType periodType;

    public PeriodType() {
    }

    public PeriodType(long id) {
        this.id = id;
    }

    public PeriodType(String code) {
        this.code = code;
    }

    public PeriodType(long id, String code, Description description, int version) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public CalendarPeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(CalendarPeriodType periodType) {
        this.periodType = periodType;
    }

    @Override
    public String toString() {
        return "PeriodType{" +
                "id=" + id +
                ", version=" + version +
                ", code='" + code + '\'' +
                ", description=" + description +
                ", periodType=" + periodType +
                '}';
    }
}
