package net.fina.server.inputs.model;

public class InputManagerFiMetaModel {
    private long id;
    private String code;
    private String fiType;
    private String name;
    private boolean active;
    private String email;
    private String phone;
    private String address;
    private String fax;
    private String regionalAddress;
    private Boolean leaf;
    private Boolean checked;

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

    public String getFiType() {
        return fiType;
    }

    public void setFiType(String fiType) {
        this.fiType = fiType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getRegionalAddress() {
        return regionalAddress;
    }

    public void setRegionalAddress(String regionalAddress) {
        this.regionalAddress = regionalAddress;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
