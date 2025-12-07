package net.fina.server.communicator.entity;

import net.fina.common.client.constants.CommunicatorReadStatus;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "IN_COMMUNICATOR_NOTIFICATION_USERS")
@Table(name = "IN_COMMUNICATOR_NOTIFICATION_USERS")
public class CommunicatorNotificationUser implements Serializable {

    @EmbeddedId
    private UserNotificationId userNotificationId;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private CommunicatorReadStatus status;

    @Column(name = "READ_DATE")
    private Date ReadDate;

    public UserNotificationId getUserNotificationId() {
        return userNotificationId;
    }

    public void setUserNotificationId(UserNotificationId userNotificationId) {
        this.userNotificationId = userNotificationId;
    }

    public CommunicatorReadStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicatorReadStatus status) {
        this.status = status;
    }

    public Date getReadDate() {
        return ReadDate;
    }

    public void setReadDate(Date readDate) {
        ReadDate = readDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommunicatorNotificationUser that = (CommunicatorNotificationUser) o;

        return userNotificationId != null ? userNotificationId.equals(that.userNotificationId) : that.userNotificationId == null;
    }

    @Override
    public int hashCode() {
        return userNotificationId != null ? userNotificationId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "NotificationUser{" +
                "userNotification=" + userNotificationId.toString() +
                ", status=" + status +
                ", ReadDate=" + ReadDate +
                '}';
    }
}
