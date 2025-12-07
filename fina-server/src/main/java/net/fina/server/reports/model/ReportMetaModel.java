package net.fina.server.reports.model;

import net.fina.common.client.reports.ReportType;
import net.fina.common.shared.i18n.DescriptionMetaModel;
import net.fina.server.i18n.model.DescriptionModelHelper;
import net.fina.server.reports.entity.Report;

import java.io.Serializable;
import java.util.List;

public class ReportMetaModel implements Serializable {

    private int id;
    private Integer version;
    private String code;
    private int parentId;
    private int type;
    private byte[] template;
    private byte[] info;
    private Integer sequence;
    private List<DescriptionMetaModel> descriptions;
    private ReportType reportType;

    public ReportMetaModel setEntity(Report report) {
        this.id = report.getId();
        this.version = report.getVersion();
        this.code = report.getCode();
        this.parentId = report.getParentId();
        this.type = report.getType();
        this.template = report.getTemplate();
        this.info = report.getInfo();
        this.sequence = report.getSequence();
        this.descriptions = DescriptionModelHelper.toModel(report.getDescription());
        this.reportType = report.getReportType();
        return this;
    }

    public Report toEntity() {
        Report report = new Report();
        report.setId(id);
        report.setVersion(version);
        report.setCode(code);
        report.setParentId(parentId);
        report.setType(type);
        report.setTemplate(template);
        report.setInfo(info);
        report.setSequence(sequence);
        report.setDescription(DescriptionModelHelper.toEntity(descriptions));
        report.setReportType(reportType);
        return report;
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

    public List<DescriptionMetaModel> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DescriptionMetaModel> descriptions) {
        this.descriptions = descriptions;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
}
