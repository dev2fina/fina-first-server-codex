package net.fina.server.returns.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

@SuppressWarnings("serial")
@Entity(name = "SYS_USER_RETURN_VERSIONS")
@Table(name = "SYS_USER_RETURN_VERSIONS")
public class UserReturnVersion implements Serializable, Audited {

    @EmbeddedId
    private UserReturnVersionId returnVersionId;

    @Column(name = "CAN_AMEND")
    private Boolean canAmend;

    public UserReturnVersionId getReturnVersionId() {
        return returnVersionId;
    }

    public void setReturnVersionId(UserReturnVersionId returnVersionId) {
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

        UserReturnVersion that = (UserReturnVersion) o;

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
        return "UserReturnVersion{" +
                "returnVersion=" + returnVersionId.toString() +
                ", canAmend=" + canAmend +
                '}';
    }
}
