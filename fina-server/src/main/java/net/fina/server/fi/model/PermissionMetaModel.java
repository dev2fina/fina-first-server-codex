package net.fina.server.fi.model;

public class PermissionMetaModel {
    private long id;
    private long nameStrId;
    private String name;
    private String idName;


    private boolean userRolePermission;
    private boolean permitted;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
    }

    public boolean isUserRolePermission() {
        return userRolePermission;
    }

    public void setUserRolePermission(boolean userRolePermission) {
        this.userRolePermission = userRolePermission;
    }
}
