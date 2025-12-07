package net.fina.server.fi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fina.common.client.fis.FiTypeSimpleModel;

import java.io.Serializable;
import java.util.Date;

public class UserRoleFiModel implements Serializable {

    private long id;
    private String code;
    private String name;
    private String address;
    private boolean userFi;
    private boolean roleFi;
    private FiTypeSimpleModel fiType;
    private String licenseCode;
    private Date createdAt;
    private Date modifiedAt;



    @JsonIgnore
    private long fiTypeId;

    public UserRoleFiModel() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUserFi() {
        return userFi;
    }

    public void setUserFi(boolean userFi) {
        this.userFi = userFi;
    }

    public boolean isRoleFi() {
        return roleFi;
    }

    public void setRoleFi(boolean roleFi) {
        this.roleFi = roleFi;
    }

    public long getFiTypeId() {
        return fiTypeId;
    }

    public void setFiTypeId(long fiTypeId) {
        this.fiTypeId = fiTypeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public FiTypeSimpleModel getFiType() {
        return fiType;
    }

    public void setFiType(FiTypeSimpleModel fiType) {
        this.fiType = fiType;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
