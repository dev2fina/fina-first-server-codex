package net.fina.common.shared.fi;

import net.fina.common.shared.i18n.DescriptionMetaModel;

import java.io.Serializable;
import java.util.List;

public class PeerGroupMetaModel implements Serializable {
    private long id;
    private int version;
    private String code;
    private List<DescriptionMetaModel> descriptions;
    private long parentId;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<DescriptionMetaModel> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DescriptionMetaModel> descriptions) {
        this.descriptions = descriptions;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
}
