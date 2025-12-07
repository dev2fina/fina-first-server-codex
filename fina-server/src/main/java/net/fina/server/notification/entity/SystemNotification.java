package net.fina.server.notification.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity(name = "SYS_NOTIFICATIONS")
@Table(name = "SYS_NOTIFICATIONS")
public class SystemNotification {

    @Id
    @SequenceGenerator(name = "notifications_sequence", sequenceName = "notifications_sequence", allocationSize = 1)
    @GeneratedValue(generator = "notifications_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    private long notify;

    private String notification;

    @Column(name = "datetime_added")
    private Date datetimeAdded;

    @Column(name = "datetime_read")
    private Date datetimeRead;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNotify() {
        return notify;
    }

    public void setNotify(long notify) {
        this.notify = notify;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public Date getDatetimeAdded() {
        return datetimeAdded;
    }

    public void setDatetimeAdded(Date datetimeAdded) {
        this.datetimeAdded = datetimeAdded;
    }

    public Date getDatetimeRead() {
        return datetimeRead;
    }

    public void setDatetimeRead(Date datetimeRead) {
        this.datetimeRead = datetimeRead;
    }
}
