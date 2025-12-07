package net.fina.common.shared.fi;

import net.fina.common.shared.i18n.DescriptionMetaModel;

import jakarta.persistence.Column;
import jakarta.persistence.Version;
import java.io.Serializable;

public class ManagementMetaModel implements Serializable {
    private long id;
    private Integer version;
    private String code;
    protected DescriptionMetaModel description;

    public ManagementMetaModel() {
    }

    public ManagementMetaModel(String code) {
        this.code = code;
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

    public DescriptionMetaModel getDescription() {
        return description;
    }

    public void setDescription(DescriptionMetaModel description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Management{" +
                "id=" + id +
                ", version=" + version +
                ", code='" + code + '\'' +
                ", description=" + description +
                '}';
    }
}
