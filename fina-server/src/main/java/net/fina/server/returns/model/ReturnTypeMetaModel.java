package net.fina.server.returns.model;

import net.fina.common.shared.i18n.DescriptionMetaModel;
import net.fina.server.i18n.model.DescriptionModelHelper;
import net.fina.server.returns.entity.ReturnType;

import java.util.List;

public class ReturnTypeMetaModel {

    private long id;
    private Integer version;
    private String code;
    private List<DescriptionMetaModel> descriptions;

    public ReturnTypeMetaModel setEntity(ReturnType entity) {
        id = entity.getId();
        version = entity.getVersion();
        code = entity.getCode();
        descriptions = DescriptionModelHelper.toModel(entity.getDescription());
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

    public List<DescriptionMetaModel> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DescriptionMetaModel> descriptions) {
        this.descriptions = descriptions;
    }
}
