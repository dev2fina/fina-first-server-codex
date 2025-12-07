package net.fina.common.client.rvc;

public class FileTypeXmlMetaModel {

    protected String value;
    protected String returnFileVersionId;
    protected String mdtFileVersionId;
    protected String returnTemplateVersionId;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getReturnFileVersionId() {
        return returnFileVersionId;
    }

    public void setReturnFileVersionId(String returnFileVersionId) {
        this.returnFileVersionId = returnFileVersionId;
    }

    public String getMdtFileVersionId() {
        return mdtFileVersionId;
    }

    public void setMdtFileVersionId(String mdtFileVersionId) {
        this.mdtFileVersionId = mdtFileVersionId;
    }

    public String getReturnTemplateVersionId() {
        return returnTemplateVersionId;
    }

    public void setReturnTemplateVersionId(String returnTemplateVersionId) {
        this.returnTemplateVersionId = returnTemplateVersionId;
    }
}
