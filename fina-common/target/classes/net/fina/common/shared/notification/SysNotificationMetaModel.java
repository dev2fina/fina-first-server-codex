package net.fina.common.shared.notification;

import java.io.Serializable;
import java.util.Date;

public class SysNotificationMetaModel implements Serializable {

    private long id;
    private long notify;
    private String notification;
    private Date datetimeAdded;
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
