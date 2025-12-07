package net.fina.server.fi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fina.common.client.fis.FiTypeSimpleModel;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiType;

import java.io.Serializable;

public class FiSimpleModel implements Serializable {

    private long id;
    private String code;

    private String name;
    private String address;
    private String createdAt;
    private int level;
    private boolean isRoleFi;
    private boolean hasFiModel;
    private long parentId;

    @JsonIgnore
    private long fiTypeId;


    public FiSimpleModel(FiType fiType, long langId) {
        this.id = fiType.getId();
        this.level = 0;

        this.code = fiType.getCode();
        if (fiType.getDescription() != null) {
            this.setName(fiType.getDescription().getDescription(langId));
        }
    }

    public FiSimpleModel(Fi fi, long langId) {
        this.id = fi.getId();
        this.level = 1;

        this.code = fi.getCode();
        this.fiTypeId = fi.getFiType().getId();
        this.parentId=this.fiTypeId;

        if (fi.getDescription() != null) {
            this.setName(fi.getDescription().getDescription(langId));
        }
    }

    public FiSimpleModel(Fi fi, long langId, boolean userRoleFi) {
        this(fi, langId);
        this.isRoleFi = userRoleFi;
    }

    public FiSimpleModel(FiTypeSimpleModel fiType) {
        this.id = fiType.getId();
        this.code = fiType.getCode();
        this.name = fiType.getName();
    }

    public FiSimpleModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isRoleFi() {
        return isRoleFi;
    }

    public void setRoleFi(boolean roleFi) {
        isRoleFi = roleFi;
    }

    public long getFiTypeId() {
        return fiTypeId;
    }

    public void setFiTypeId(long fiTypeId) {
        this.fiTypeId = fiTypeId;
    }

    public boolean isHasFiModel() {
        return hasFiModel;
    }

    public void setHasFiModel(boolean hasFiModel) {
        this.hasFiModel = hasFiModel;
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

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FiSimpleModel other = (FiSimpleModel) obj;
        return getId() == other.getId();
    }


}
