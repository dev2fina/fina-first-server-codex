package net.fina.common.client.returns;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ReturnTypeModel implements Serializable, Comparable<ReturnTypeModel> {

    private long id;
    private Integer version;
    private String code;
    private long nameStrId;
    private String name;
    private boolean editable;
    private boolean excelTemplate;

    public ReturnTypeModel() {
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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isExcelTemplate() {
        return excelTemplate;
    }

    public void setExcelTemplate(boolean excelTemplate) {
        this.excelTemplate = excelTemplate;
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
        ReturnTypeModel other = (ReturnTypeModel) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public String toString() {
        return "ReturnTypeModel [id=" + id + "," +
                " code=" + code +
                ", name=" + name + "]";
    }

    @Override
    public int compareTo(ReturnTypeModel o) {
        return this.code.compareToIgnoreCase(o.getCode());
    }
}
