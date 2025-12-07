package net.fina.server.fi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.common.shared.i18n.DescriptionMetaModel;
import net.fina.server.fi.entity.Fi;
import net.fina.server.i18n.model.DescriptionModelHelper;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiMetaModel implements Serializable {

    private long id;
    private int version;
    private String code;
    private FiTypeMetaModel fiType;
    private List<DescriptionMetaModel> description;

    public FiMetaModel() {
    }

    public FiMetaModel setEntity(Fi fi) {
        this.id = fi.getId();
        this.version = fi.getVersion();
        this.code = fi.getCode();
        this.description = DescriptionModelHelper.toModel(fi.getDescription());
        

        FiTypeMetaModel fiTypeMetaModel = new FiTypeMetaModel();
        fiTypeMetaModel.setEntity(fi.getFiType());

        this.setFiType(fiTypeMetaModel);

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

    public List<DescriptionMetaModel> getDescription() {
        return description;
    }

    public void setDescription(List<DescriptionMetaModel> description) {
        this.description = description;
    }

    public FiTypeMetaModel getFiType() {
        return fiType;
    }

    public void setFiType(FiTypeMetaModel fiType) {
        this.fiType = fiType;
    }
}
