package net.fina.common.shared;

import net.fina.common.client.fis.FiTypeModel;
import net.fina.common.client.returns.ReturnDefinitionModel;

import java.util.ArrayList;
import java.util.List;

public class PackageMetaModel {
    private long id;
    private String code;
    private String note;
    private List<ReturnDefinitionModel> returnDefinitions;
    private List<FiTypeModel> fiTypes;
    private boolean hasFiTypes;
    private boolean hasReturnDefinitions;

    public PackageMetaModel() {
    }

    public PackageMetaModel(long id, String code, String note) {
        this.id = id;
        this.code = code;
        this.note = note;
    }


    public boolean isHasFiTypes() {
        return hasFiTypes;
    }

    public void setHasFiTypes(boolean hasFiTypes) {
        this.hasFiTypes = hasFiTypes;
    }

    public boolean isHasReturnDefinitions() {
        return hasReturnDefinitions;
    }

    public void setHasReturnDefinitions(boolean hasReturnDefinitions) {
        this.hasReturnDefinitions = hasReturnDefinitions;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<ReturnDefinitionModel> getReturnDefinitions() {
        return returnDefinitions == null ? new ArrayList<ReturnDefinitionModel>() : returnDefinitions;
    }

    public void setReturnDefinitions(List<ReturnDefinitionModel> returnDefinitions) {
        this.returnDefinitions = returnDefinitions;
    }

    public List<FiTypeModel> getFiTypes() {
        return fiTypes == null ? new ArrayList<FiTypeModel>() : fiTypes;
    }

    public void setFiTypes(List<FiTypeModel> fiTypes) {
        this.fiTypes = fiTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PackageMetaModel that = (PackageMetaModel) o;

        if (id != that.id) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }
}
