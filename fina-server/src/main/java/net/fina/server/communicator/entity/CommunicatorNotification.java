package net.fina.server.communicator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import net.fina.auditlog.api.Audited;
import net.fina.common.client.constants.CommunicatorNotificationStatus;
import net.fina.server.security.entity.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity(name = "IN_COMMUNICATOR_NOTIFICATIONS")
@Table(name = "IN_COMMUNICATOR_NOTIFICATIONS")
public class CommunicatorNotification implements Serializable, Audited {


    @Id
    @SequenceGenerator(name = "com_notification_sequence", sequenceName = "com_notification_sequence", allocationSize = 1)
    @GeneratedValue(generator = "com_notification_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    private String title;

    private String content;

    private Date publishDate;

    private Date creationDate;

    private Boolean sign;

    private boolean automatic;

    @OneToOne
    @JoinColumn(name = "USERID")
    private User user;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private CommunicatorNotificationStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "userNotificationId.notification", cascade = CascadeType.PERSIST)
    private Collection<CommunicatorNotificationUser> notificationUsers;

    @Column(name = "IS_DELETED")
    private boolean deleted;
    @Column(name = "HAS_ATTACHMENTS")
    private boolean hasAttachments;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String subject) {
        this.title = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date sendDate) {
        this.publishDate = sendDate;
    }

    public Collection<CommunicatorNotificationUser> getNotificationUsers() {
        if (notificationUsers == null) {
            return new ArrayList<>();
        }
        return notificationUsers;
    }

    public void setNotificationUsers(Collection<CommunicatorNotificationUser> messageUsers) {
        this.notificationUsers = messageUsers;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public CommunicatorNotificationStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicatorNotificationStatus status) {
        this.status = status;
    }

    public Boolean getSign() {
        return sign;
    }

    public void setSign(Boolean sign) {
        this.sign = sign;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getAutomatic() {
        return automatic;
    }

    public void setAutomatic(Boolean automatic) {
        this.automatic = automatic;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommunicatorNotification notification = (CommunicatorNotification) o;

        return id == notification.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
