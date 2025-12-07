package net.fina.server.mdt.entity;

import net.fina.server.security.entity.User;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;


@Embeddable
public class UserMdtId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "NODE_ID", referencedColumnName = "ID")
    private MDTNode node;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

        UserMdtId userMdtId = (UserMdtId) o;

        if (user != null ? !user.equals(userMdtId.user) : userMdtId.user != null) return false;
        return node != null ? node.equals(userMdtId.node) : userMdtId.node == null;

    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (node != null ? node.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserMdtId{" +
                "user=" + user.getId() +
                ", node=" + node.getId() +
                '}';
    }
}
