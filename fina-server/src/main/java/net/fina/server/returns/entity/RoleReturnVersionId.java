package net.fina.server.returns.entity;

import net.fina.server.security.entity.Role;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@SuppressWarnings("serial")
@Embeddable
public class RoleReturnVersionId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "VERSION_ID", referencedColumnName = "ID")
    private ReturnVersion returnVersion;

    public ReturnVersion getReturnVersion() {
        return returnVersion;
    }

    public void setReturnVersion(ReturnVersion returnVersion) {
        this.returnVersion = returnVersion;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleReturnVersionId versionId = (RoleReturnVersionId) o;

        if (returnVersion != null ? !returnVersion.equals(versionId.returnVersion) : versionId.returnVersion != null)
            return false;
        if (role != null ? !role.equals(versionId.role) : versionId.role != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = role != null ? role.hashCode() : 0;
        result = 31 * result + (returnVersion != null ? returnVersion.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RoleReturnVersionId{" +
                "role=" + role.getId() +
                ", returnVersion=" + returnVersion.getId() +
                '}';
    }
}
