package net.fina.server.reports.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity(name = "OUT_REPORTS_LANG")
@Table(name = "OUT_REPORTS_LANG")
public class ReportTemplate implements Serializable, Audited {

    @EmbeddedId
    private ReportTemplatePk reportTemplatePk;

    @Column
    private byte[] template;

    public ReportTemplatePk getReportTemplatePk() {
        return reportTemplatePk;
    }

    public void setReportTemplatePk(ReportTemplatePk reportTemplatePk) {
        this.reportTemplatePk = reportTemplatePk;
    }

    public byte[] getTemplate() {
        return template;
    }

    public void setTemplate(byte[] template) {
        this.template = template;
    }
}
