package net.fina.common.client.returns;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReturnDefinitionModel implements Serializable {

    private long id;

    private Integer version;

    private String code;

    // Description
    private long nameStrId;
    private String name;

    private Boolean disable;

    private ReturnTypeModel returnType;

    // ---
    private boolean userRoleReturnDefinition;
    private boolean hasReturnDefinition;
    @JsonProperty("manualInput")
    private boolean manualInput;

    private String generalInfo;

    private List<DefinitionTableModel> tables;

    public ReturnDefinitionModel() {
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

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public ReturnTypeModel getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnTypeModel returnType) {
        this.returnType = returnType;
    }

    public boolean isHasReturnDefinition() {
        return hasReturnDefinition;
    }

    public void setHasReturnDefinition(boolean hasReturnDefinition) {
        this.hasReturnDefinition = hasReturnDefinition;
    }

    public boolean isUserRoleReturnDefinition() {
        return userRoleReturnDefinition;
    }

    public void setUserRoleReturnDefinition(boolean userRoleReturnDefinition) {
        this.userRoleReturnDefinition = userRoleReturnDefinition;
    }

    public boolean isManualInput() {
        return manualInput;
    }

    public void setManualInput(boolean manualInput) {
        this.manualInput = manualInput;
    }

    public String getGeneralInfo() {
        return generalInfo;
    }

    public void setGeneralInfo(String generalInfo) {
        this.generalInfo = generalInfo;
    }

    public List<DefinitionTableModel> getTables() {
        return tables == null ? new ArrayList<DefinitionTableModel>() : tables;
    }

    public void setTables(List<DefinitionTableModel> tables) {
        this.tables = tables;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReturnDefinitionModel other = (ReturnDefinitionModel) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
