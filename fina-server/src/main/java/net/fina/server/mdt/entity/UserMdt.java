package net.fina.server.mdt.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity(name = "SYS_USER_MDT")
@Table(name = "SYS_USER_MDT")
public class UserMdt implements Serializable, Audited {

    @EmbeddedId
    private UserMdtId userMdtId;

    @Column(name = "CAN_AMEND")
    private Boolean canAmend;

    public UserMdtId getUserMdtId() {
        return userMdtId;
    }

    public void setUserMdtId(UserMdtId userMdtId) {
        this.userMdtId = userMdtId;
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

        UserMdt userMdt = (UserMdt) o;

        return userMdtId != null ? userMdtId.equals(userMdt.userMdtId) : userMdt.userMdtId == null;

    }

    @Override
    public int hashCode() {
        return userMdtId != null ? userMdtId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserMdt{" +
                "userMdt=" + userMdtId.toString() +
                ", canAmend=" + canAmend +
                '}';
    }
}
