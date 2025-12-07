package net.fina.server.cems.model;

import net.fina.common.shared.KeyValuePair;
import net.fina.server.cems.entity.sanction.CEMSSanctionRegulationCatalog;

public class CEMSSanctionRegulationModel {
    private long id;
    private KeyValuePair<String,String> regulationCatalog;
    private String value;
    private String actualValue;

    public CEMSSanctionRegulationModel() {
    }

    public CEMSSanctionRegulationModel(long id, KeyValuePair<String,String> regulationCatalog, String value, String actualValue) {
        this.id = id;
        this.regulationCatalog = regulationCatalog;
        this.value = value;
        this.actualValue = actualValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public KeyValuePair<String,String> getRegulationCatalog() {
        return regulationCatalog;
    }

    public void setRegulationCatalog(KeyValuePair<String,String> regulationCatalog) {
        this.regulationCatalog = regulationCatalog;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }
}
