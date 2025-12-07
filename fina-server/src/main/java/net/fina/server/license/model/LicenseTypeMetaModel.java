package net.fina.server.license.model;

import java.util.ArrayList;
import java.util.List;

public class LicenseTypeMetaModel {
    private long id;
    private String code;
    private Integer version;
    private long nameStrId;
    private String name;

    private List<BankingOperationMetaModel> operations;

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

    public List<BankingOperationMetaModel> getOperations() {
        return operations == null ? new ArrayList<>() : operations;
    }

    public void setOperations(List<BankingOperationMetaModel> operations) {
        this.operations = operations;
    }
}
