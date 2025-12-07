package net.fina.common.shared.legalperson.metainfo;

import java.io.Serializable;

public class LegalPersonMetaInfoModel implements Serializable {
    private long id;

    private CodeDescriptionModel businessEntity;
    private CodeDescriptionModel economicEntity;
    private CodeDescriptionModel equityForm;
    private CodeDescriptionModel managementForm;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CodeDescriptionModel getBusinessEntity() {
        return businessEntity;
    }

    public void setBusinessEntity(CodeDescriptionModel businessEntity) {
        this.businessEntity = businessEntity;
    }

    public CodeDescriptionModel getEconomicEntity() {
        return economicEntity;
    }

    public void setEconomicEntity(CodeDescriptionModel economicEntity) {
        this.economicEntity = economicEntity;
    }

    public CodeDescriptionModel getEquityForm() {
        return equityForm;
    }

    public void setEquityForm(CodeDescriptionModel equityForm) {
        this.equityForm = equityForm;
    }

    public CodeDescriptionModel getManagementForm() {
        return managementForm;
    }

    public void setManagementForm(CodeDescriptionModel managementForm) {
        this.managementForm = managementForm;
    }
}
