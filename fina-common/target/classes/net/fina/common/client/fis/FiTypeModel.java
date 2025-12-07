package net.fina.common.client.fis;

import net.fina.common.shared.i18n.DescriptionMetaModel;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FiTypeModel implements Serializable {

    private long id;
    private String code;
    private Integer version;
    private long nameStrId;
    private String name;
    private DescriptionMetaModel descriptionModel;

    private int activeFisCount;
    private int inactiveFisCount;

    public FiTypeModel() {
    }
    public FiTypeModel(long id) {
        this.id = id;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public DescriptionMetaModel getDescriptionModel() {
        return descriptionModel;
    }

    public void setDescriptionModel(DescriptionMetaModel descriptionModel) {
        this.descriptionModel = descriptionModel;
    }

    public int getActiveFisCount() {
        return activeFisCount;
    }

    public void setActiveFisCount(int activeFisCount) {
        this.activeFisCount = activeFisCount;
    }

    public int getInactiveFisCount() {
        return inactiveFisCount;
    }

    public void setInactiveFisCount(int inactiveFisCount) {
        this.inactiveFisCount = inactiveFisCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiTypeModel that = (FiTypeModel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return 31 * ((Object) id).hashCode();
    }

    @Override
    public String toString() {
        return "FiTypeModel{" +
                "code='" + code + '\'' +
                '}';
    }
}
