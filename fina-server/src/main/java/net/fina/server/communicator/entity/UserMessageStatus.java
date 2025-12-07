package net.fina.server.communicator.entity;

import net.fina.auditlog.api.Audited;
import net.fina.common.client.constants.CommunicatorReadStatus;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "IN_COMMUNICATOR_USERS_MESSAGE_STATUS")
@Table(name = "IN_COMMUNICATOR_USERS_MESSAGE_STATUS")
public class UserMessageStatus implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "com_users_messages_sequence", sequenceName = "com_users_messages_sequence", allocationSize = 1)
    @GeneratedValue(generator = "com_users_messages_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Enumerated(EnumType.ORDINAL)
    private CommunicatorReadStatus status;
    @Column(name = "USER_ID")
    private long user;

    @Column(name = "READ_DATE")
    private Date readDate;

//    @Column(name = "LAST_MESSAGE_ID")
//    private long lastMessageId;

    @Column(name = "MESSAGE_ID")
    private long messageId;
    @Column(name = "MESSAGE_USER_ID")
    private long messageUserId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CommunicatorReadStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicatorReadStatus status) {
        this.status = status;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(long messageUserId) {
        this.messageUserId = messageUserId;
    }

    @Override
    public String toString() {
        return "UserMessageStatus{" +
                ", status=" + status +
                ", user=" + user +
                ", readDate=" + readDate +
                '}';
    }
}
