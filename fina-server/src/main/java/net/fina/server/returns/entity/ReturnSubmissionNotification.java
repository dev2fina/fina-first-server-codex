package net.fina.server.returns.entity;

import net.fina.server.returns.api.ReturnSubmissionNotificationType;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "IN_OVERDUE_RETURN_NOTIFICATIONS")
@Table(name = "IN_OVERDUE_RETURN_NOTIFICATIONS")
public class ReturnSubmissionNotification implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(name = "SCHEDULE_ID")
    private Schedule schedule;

    @Id
    @Column(name = "NOTIFICATION_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private ReturnSubmissionNotificationType notificationType;

    public ReturnSubmissionNotification() {
    }

    // convenience ctor
    public ReturnSubmissionNotification(Schedule schedule, ReturnSubmissionNotificationType notificationType) {
        this.schedule = schedule;
        this.notificationType = notificationType;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }


    public ReturnSubmissionNotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(ReturnSubmissionNotificationType notificationType) {
        this.notificationType = notificationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.schedule, this.notificationType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof ReturnSubmissionNotification) ) return false;
        ReturnSubmissionNotification other = (ReturnSubmissionNotification) obj;
        return Objects.equals(this.schedule, other.schedule) &&
                Objects.equals(this.notificationType, other.notificationType);
    }
}
