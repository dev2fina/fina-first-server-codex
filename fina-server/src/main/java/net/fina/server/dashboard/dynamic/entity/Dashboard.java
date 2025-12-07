package net.fina.server.dashboard.dynamic.entity;

import jakarta.validation.constraints.Size;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "IN_DASHBOARDS")
@Table(name = "IN_DASHBOARDS")
public class Dashboard {
    @Id
    @SequenceGenerator(name = "dashboards_sequence", sequenceName = "dashboards_sequence", allocationSize = 1)
    @GeneratedValue(generator = "dashboards_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description name;

    @Column(name = "COLUMNS")
    private byte columnSize;

    @Column(name = "IS_DEFAULT")
    private boolean isDefault;

    @OneToMany
    @JoinTable(name = "IN_DASHBOARD_DASHLETS", joinColumns = @JoinColumn(name = "DASHBOARD_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "DASHLET_ID", referencedColumnName = "ID"))
    private List<Dashlet> dashletList;

    @Column(name = "CONFIG")
    private String configJson;

    @Column(name = "CODE",unique = true)
    @Size(max = 40, message = "Code must not exceed 40 characters")
    private String code;

    public Dashboard() {
    }

    public Dashboard(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Description getName() {
        return name;
    }

    public void setName(Description name) {
        this.name = name;
    }

    public byte getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(byte columnsSize) {
        this.columnSize = columnsSize;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean _default) {
        this.isDefault = _default;
    }

    public List<Dashlet> getDashletList() {
        return dashletList == null ? new ArrayList<>() : dashletList;
    }

    public void setDashletList(List<Dashlet> dashletList) {
        this.dashletList = dashletList;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dashboard dashboard = (Dashboard) o;
        return getId() == dashboard.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
