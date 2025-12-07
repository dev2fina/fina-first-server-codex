package net.fina.server.reports.entity;

import jakarta.persistence.*;
import java.io.Serializable;


@Entity(name = "OUT_REPORT_PROPERTIES")
@Table(name = "OUT_REPORT_PROPERTIES")
@IdClass(ReportPropertyId.class)
public class ReportProperty implements Serializable {

    @Id
    @Column(name = "report_id")
    private int reportId;

    @Id
    @Column(name = "prop_key")
    private String key;

    private String name;

    private String value;

    private boolean renewable;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRenewable() {
        return renewable;
    }

    public void setRenewable(boolean renewable) {
        this.renewable = renewable;
    }
}
