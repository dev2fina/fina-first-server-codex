package net.fina.server.mdt.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "SYS_ROLE_MDT")
@Table(name = "SYS_ROLE_MDT")
public class RoleMdt implements Serializable, Audited {

    @EmbeddedId
    private RoleMdtId roleMdtId;

    @Column(name = "CAN_AMEND")
    private Boolean canAmend;

    public RoleMdtId getRoleMdtId() {
        return roleMdtId;
    }

    public void setRoleMdtId(RoleMdtId roleMdtId) {
        this.roleMdtId = roleMdtId;
    }

    public Boolean getCanAmend() {
        return canAmend;
    }

    public void setCanAmend(Boolean canAmend) {
        this.canAmend = canAmend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleMdt roleMdt = (RoleMdt) o;

        return Objects.equals(roleMdtId, roleMdt.roleMdtId);
    }

    @Override
    public int hashCode() {
        return roleMdtId != null ? roleMdtId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RoleMdt{" +
                "roleMdt=" + roleMdtId.toString() +
                ", canAmend=" + canAmend +
                '}';
    }

}
