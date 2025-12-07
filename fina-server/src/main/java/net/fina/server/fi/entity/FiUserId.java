package net.fina.server.fi.entity;

import java.io.Serializable;

/**
 * @author vamekh
 */
public class FiUserId implements Serializable {

    private long bankId;
    private long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FiUserId fiUserId = (FiUserId) o;

        if (bankId != fiUserId.bankId) return false;
        return userId == fiUserId.userId;

    }

    @Override
    public int hashCode() {
        int result = (int) (bankId ^ (bankId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "FiUserId{" +
                "bankId=" + bankId +
                ", userId=" + userId +
                '}';
    }
}
