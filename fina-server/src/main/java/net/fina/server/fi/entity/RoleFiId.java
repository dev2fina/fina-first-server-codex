package net.fina.server.fi.entity;

import java.io.Serializable;

public class RoleFiId implements Serializable {
    private long roleId;
    private long bankId;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (bankId ^ (bankId >>> 32));
        result = prime * result + (int) (roleId ^ (roleId >>> 32));
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
        RoleFiId other = (RoleFiId) obj;
        if (bankId != other.roleId)
            return false;
        if (roleId != other.roleId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "roleFiId{" +
                "roleId=" + roleId +
                ", bankId=" + bankId +
                '}';
    }

}
