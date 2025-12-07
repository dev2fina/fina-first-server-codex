package net.fina.server.returns.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

@SuppressWarnings("serial")
@Entity(name = "SYS_ROLE_RETURN_VERSIONS")
@Table(name = "SYS_ROLE_RETURN_VERSIONS")
public class RoleReturnVersion implements Serializable, Audited {

    @EmbeddedId
    private RoleReturnVersionId returnVersionId;

    @Column(name = "CAN_AMEND")
    private Boolean canAmend;

    public RoleReturnVersionId getReturnVersionId() {
        return returnVersionId;
    }

    public void setReturnVersionId(RoleReturnVersionId returnVersionId) {
        this.returnVersionId = returnVersionId;
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

        RoleReturnVersion that = (RoleReturnVersion) o;

        if (returnVersionId != null ? !returnVersionId.equals(that.returnVersionId) : that.returnVersionId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return returnVersionId != null ? returnVersionId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RoleReturnVersion{" +
                "returnVersion=" + returnVersionId.toString() +
                ", canAmend=" + canAmend +
                '}';
    }
}
