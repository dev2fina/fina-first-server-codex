package net.fina.server.returns.entity;

import net.fina.server.security.entity.User;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class UserReturnVersionId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "VERSION_ID", referencedColumnName = "ID")
    private ReturnVersion returnVersion;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ReturnVersion getReturnVersion() {
        return returnVersion;
    }

    public void setReturnVersion(ReturnVersion returnVersion) {
        this.returnVersion = returnVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserReturnVersionId versionId = (UserReturnVersionId) o;

        if (returnVersion != null ? !returnVersion.equals(versionId.returnVersion) : versionId.returnVersion != null)
            return false;
        if (user != null ? !user.equals(versionId.user) : versionId.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (returnVersion != null ? returnVersion.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserReturnVersionId{" +
                "user=" + user.getId() +
                ", returnVersion=" + returnVersion.getId() +
                '}';
    }
}
