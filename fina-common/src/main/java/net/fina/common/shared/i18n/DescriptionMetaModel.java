package net.fina.common.shared.i18n;

import java.io.Serializable;

public class DescriptionMetaModel implements Serializable {
    private long nameStrId;
    private long langId;
    private String description;

    public DescriptionMetaModel() {
    }

    public DescriptionMetaModel(long langId, long nameStrId, String description) {
        this.langId = langId;
        this.nameStrId = nameStrId;
        this.description = description;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
