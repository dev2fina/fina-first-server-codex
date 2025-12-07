package net.fina.common.shared.legalperson.metainfo;

import net.fina.common.shared.i18n.DescriptionMetaModel;
import java.io.Serializable;
import java.util.List;

public class CodeDescriptionModel implements Serializable {
    private long id;
    private String code;
    private String description;

    private long nameStrId;

    public CodeDescriptionModel() {
    }

    public CodeDescriptionModel(long id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }
    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
