package net.fina.server.fi.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.*;

@Entity(name = "SYS_ROLE_BANKS")
@Table(name = "SYS_ROLE_BANKS")
@IdClass(RoleFiId.class)
public class RoleFi implements Audited {
    @Id
    @Column(name = "ROLE_ID")
    private long roleId;

    @Id
    @Column(name = "BANK_ID")
    private long bankId;

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    @Override
    public String toString() {
        return "RoleFi{" +
                "roleId=" + roleId +
                ", bankId=" + bankId +
                '}';
    }
}
