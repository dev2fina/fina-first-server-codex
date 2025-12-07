package net.fina.server.reports.entity;

import net.fina.auditlog.api.Audited;
import net.fina.common.client.reports.ReportType;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity(name = "OUT_REPORTS")
@Table(name = "OUT_REPORTS")
@NamedQueries({
        @NamedQuery(name = "REPORT.checkCodeUnique", query = "SELECT r FROM OUT_REPORTS r WHERE trim(r.code) like :code AND r.id <> :id"),
})

public class Report implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "out_reports_sequence", sequenceName = "out_reports_sequence", allocationSize = 1)
    @GeneratedValue(generator = "out_reports_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private int id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "CODE", length = 100, nullable = false)
    private String code;

    @Column(name = "PARENTID")
    private int parentId;

    @Column(name = "TYPE")
    private int type;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "TEMPLATE")
    private byte[] template;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "INFO")
    private byte[] info;

    @Column(name = "SEQUENCE")
    private Integer sequence;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description description;

    @Column(name = "REPORT_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private ReportType reportType;

    @Transient
    private boolean hasUserReport;

    @Transient
    private byte[] generatedContent;

    public Report() {
    }

    public Report(int id) {
        this.id = id;
    }

    public Report(int id, int parentId) {
        this.id = id;
        this.parentId = parentId;
    }


    public Report(int id, int parentId, ReportType reportType) {
        this.id = id;
        this.parentId = parentId;
        this.reportType = reportType;
    }


    public Report(int id, Integer version, String code, int parentId, int type, Integer sequence, Description description) {
        this.id = id;
        this.version = version;
        this.code = code;
        this.parentId = parentId;
        this.type = type;
        this.sequence = sequence;
        this.description = description;
    }

    public Report(int id, Integer version, int parentId, String code, Description description, Integer sequence, int type, ReportType reportType) {
        this.id = id;
        this.version = version;
        this.parentId = parentId;
        this.code = code;
        this.description = description;
        this.sequence = sequence;
        this.type = type;
        this.reportType = reportType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getTemplate() {
        return template;
    }

    public void setTemplate(byte[] template) {
        this.template = template;
    }

    public byte[] getInfo() {
        return info;
    }

    public void setInfo(byte[] info) {
        this.info = info;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public boolean isHasUserReport() {
        return hasUserReport;
    }

    public void setHasUserReport(boolean hasUserReport) {
        this.hasUserReport = hasUserReport;
    }

    public byte[] getGeneratedContent() {
        return generatedContent;
    }

    public void setGeneratedContent(byte[] generatedContent) {
        this.generatedContent = generatedContent;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", version=" + version +
                ", parentId=" + parentId +
                ", type=" + type +
                ", sequence=" + sequence +
                ", description=" + description +
                ", hasUserReport=" + hasUserReport +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        Report other = (Report) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
