package net.fina.common.shared.user;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PermissionModel implements Serializable {

    private long id;
    private long nameStrId;
    private String name;
    private String idName;

    private boolean userRolePermission;
    private boolean permitted;

    public PermissionModel() {
    }

    public PermissionModel(long id, long nameStrId, String name, String idName, boolean userRolePermission, boolean permitted) {
        this.id = id;
        this.nameStrId = nameStrId;
        this.name = name;
        this.idName = idName;
        this.userRolePermission = userRolePermission;
        this.permitted = permitted;
    }

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
        PermissionModel other = (PermissionModel) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
