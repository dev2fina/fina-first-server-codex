package net.fina.server.fi.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserFiId implements Serializable {

    private long userId;
    private long bankId;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (bankId ^ (bankId >>> 32));
        result = prime * result + (int) (userId ^ (userId >>> 32));
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
        UserFiId other = (UserFiId) obj;
        if (bankId != other.bankId)
            return false;
        if (userId != other.userId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserFiId{" +
                "userId=" + userId +
                ", bankId=" + bankId +
                '}';
    }
}
