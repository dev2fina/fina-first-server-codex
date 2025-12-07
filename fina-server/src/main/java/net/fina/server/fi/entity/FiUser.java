package net.fina.server.fi.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * @author vamekh
 */

@Entity(name = "SYS_BANK_USERS")
@Table(name = "SYS_BANK_USERS")
@IdClass(FiUserId.class)
public class FiUser implements Audited {

    @Id
    private long bankId;

    @Id
    private long userId;

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "FiUser{" +
                "bankId=" + bankId +
                ", userId=" + userId +
                '}';
    }
}
