package net.fina.server.fi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.common.shared.i18n.DescriptionMetaModel;
import net.fina.server.fi.entity.FiType;
import net.fina.server.i18n.model.DescriptionModelHelper;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiTypeMetaModel implements Serializable {

    private long id;
    private int version;
    private String code;
    protected List<DescriptionMetaModel> descriptions;

    public FiTypeMetaModel() {
    }

    public FiTypeMetaModel setEntity(FiType fiType) {
        this.id = fiType.getId();
        this.version = fiType.getVersion();
        this.code = fiType.getCode();
        this.descriptions = DescriptionModelHelper.toModel(fiType.getDescription());
        return this;
    }

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
}
