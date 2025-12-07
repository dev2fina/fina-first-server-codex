package net.fina.common.shared.fi;

import net.fina.common.shared.i18n.DescriptionMetaModel;

import java.io.Serializable;
import java.util.List;

public class CriterionMetaModel implements Serializable {
    private long id;
    private String code;
    private List<DescriptionMetaModel> descriptions;
    private Boolean defaultGroup;
    private Integer version;

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

    public List<DescriptionMetaModel> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DescriptionMetaModel> descriptions) {
        this.descriptions = descriptions;
    }

    public Boolean getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(Boolean aDefault) {
        defaultGroup = aDefault;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
