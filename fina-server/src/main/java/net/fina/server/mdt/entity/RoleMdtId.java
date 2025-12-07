package net.fina.server.mdt.entity;

import net.fina.server.security.entity.Role;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;


@Embeddable
public class RoleMdtId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "NODE_ID", referencedColumnName = "ID")
    private MDTNode node;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public MDTNode getNode() {
        return node;
    }

    public void setNode(MDTNode node) {
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleMdtId roleMdtId = (RoleMdtId) o;

        if (role != null ? !role.equals(roleMdtId.role) : roleMdtId.role != null) return false;
        return node != null ? node.equals(roleMdtId.node) : roleMdtId.node == null;

    }

    @Override
    public int hashCode() {
        int result = role != null ? role.hashCode() : 0;
        result = 31 * result + (node != null ? node.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RoleMdtId{" +
                "role=" + role.getId() +
                ", node=" + node.getId() +
                '}';
    }
}
