package net.fina.server.communicator.entity;

import net.fina.server.security.entity.User;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class UserNotificationId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "NOTIFICATION_ID", referencedColumnName = "ID")
    private CommunicatorNotification notification;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CommunicatorNotification getNotification() {
        return notification;
    }

    public void setNotification(CommunicatorNotification message) {
        this.notification = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserNotificationId that = (UserNotificationId) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return notification != null ? notification.equals(that.notification) : that.notification == null;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (notification != null ? notification.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserNotificationId{" +
                "user=" + (user != null ? user.getId() : null) +
                ", notification=" + (notification != null ? notification.getId() : null) +
                '}';
    }
}
