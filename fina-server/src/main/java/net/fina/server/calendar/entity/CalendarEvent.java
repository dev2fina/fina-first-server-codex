package net.fina.server.calendar.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "IN_CALENDAR_EVENTS")
public class CalendarEvent {
    @Id
    @SequenceGenerator(name = "calendar_event_sequence", sequenceName = "calendar_event_sequence", allocationSize = 1)
    @GeneratedValue(generator = "calendar_event_sequence", strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(name = "\"DATE\"")
    private Date date;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "EVENT_TYPE")
    private EventType eventType;
    @Column(name = "\"COMMENT\"")
    private String comment;
    @Column(name = "GROUP_UUID")
    private String groupUUID;

    public CalendarEvent() {
    }

    public CalendarEvent(Date date, EventType eventType, String comment, String groupUUID) {
        this.date = date;
        this.eventType = eventType;
        this.comment = comment;
        this.groupUUID = groupUUID;
    }

    public CalendarEvent(long id, Date date, EventType eventType, String comment, String groupUUID) {
        this.id = id;
        this.date = date;
        this.eventType = eventType;
        this.comment = comment;
        this.groupUUID = groupUUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGroupUUID() {
        return groupUUID;
    }

    public void setGroupUUID(String groupUUID) {
        this.groupUUID = groupUUID;
    }
}
