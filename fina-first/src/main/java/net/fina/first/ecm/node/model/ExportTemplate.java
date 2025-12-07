package net.fina.first.ecm.node.model;

public enum ExportTemplate {
    GENERAL("EXPORT_TEMPLATE_GENERAL.xlsx"),
    FI_REGISTRY("EXPORT_TEMPLATE_FI_REGISTRY.xlsx");

    private String templateFileName;

    ExportTemplate(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public String getTemplateFileName() {
        return templateFileName;
    }

    public String getFullTemplateFilePath() {
        return getClass().getPackage().getName().replace('.', '/') + "/" + templateFileName;
    }
}
