package net.fina.common.server.fi.sync.model;

public class FiDescriptionSyncMetaModel {
    private long langId;
    private String value;

    public FiDescriptionSyncMetaModel() {
    }

    public FiDescriptionSyncMetaModel(long langId, String value) {
        this.langId = langId;
        this.value = value;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
