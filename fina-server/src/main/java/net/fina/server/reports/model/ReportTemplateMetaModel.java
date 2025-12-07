package net.fina.server.reports.model;

import net.fina.server.reports.entity.ReportTemplate;

import java.io.Serializable;

public class ReportTemplateMetaModel implements Serializable {
    private int reportId;
    private long langId;
    private byte[] template;

    public ReportTemplateMetaModel setEntity(ReportTemplate template) {
        if(template!=null) {
            this.reportId = template.getReportTemplatePk().getReportId();
            this.langId = template.getReportTemplatePk().getLangId();
            this.template = template.getTemplate();
        }
        return this;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    public byte[] getTemplate() {
        return template;
    }

    public void setTemplate(byte[] template) {
        this.template = template;
    }
}
