package net.fina.server.returns.model;

import net.fina.common.shared.i18n.DescriptionMetaModel;
import net.fina.server.i18n.model.DescriptionModelHelper;
import net.fina.server.returns.entity.ReturnVersion;

import java.io.Serializable;
import java.util.List;

public class ReturnVersionMetaModel implements Serializable {
    private long id;
    private Integer version;
    private String code;
    private long sequence;
    private List<DescriptionMetaModel> descriptions;

    public ReturnVersionMetaModel setEntity(ReturnVersion entity) {
        this.id = entity.getId();
        this.version = entity.getVersion();
        this.code = entity.getCode();
        this.sequence = entity.getSequence();
        this.descriptions = DescriptionModelHelper.toModel(entity.getDescription());

        return this;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public List<DescriptionMetaModel> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DescriptionMetaModel> descriptions) {
        this.descriptions = descriptions;
    }
}
