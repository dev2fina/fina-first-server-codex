package net.fina.server.communicator.entity;


import jakarta.persistence.*;
import net.fina.common.client.constants.CommunicatorReadStatus;
import net.fina.server.security.entity.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "IN_MESSAGE_USERS")
@Table(name = "IN_MESSAGE_USERS")
public class MessageUser implements Serializable {

    @EmbeddedId
    private MessageUserId messageUserId;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private CommunicatorReadStatus status;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "message_id"),
            @JoinColumn(name = "message_user_id")
    }
    )
    private List<UserMessageStatus> userMessageStatus;

    @Column(name = "READ_DATE")
    private Date readDate;

    @OneToOne
    @JoinColumn(name = "LAST_MESSAGE_USER")
    private User lastMessageUser;
    @Column(name = "LAST_CONVERSATION_MESSAGE_DATE")
    private Date lastConversationMessageDate = new Date();

    public MessageUserId getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(MessageUserId messageUserId) {
        this.messageUserId = messageUserId;
    }

    public CommunicatorReadStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicatorReadStatus status) {
        this.status = status;
    }


    public List<UserMessageStatus> getUserMessageStatus() {
        return userMessageStatus == null ? new ArrayList<>() : userMessageStatus;
    }

    public void setUserMessageStatus(List<UserMessageStatus> userMessages) {
        this.userMessageStatus = userMessages;
    }


    public User getLastMessageUser() {
        return lastMessageUser;
    }

    public void setLastMessageUser(User lastMessageUser) {
        this.lastMessageUser = lastMessageUser;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public Date getLastConversationMessageDate() {
        return lastConversationMessageDate;
    }

    public void setLastConversationMessageDate(Date lastConversationMessageDate) {
        this.lastConversationMessageDate = lastConversationMessageDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageUser that = (MessageUser) o;

        if (messageUserId != null ? !messageUserId.equals(that.messageUserId) : that.messageUserId != null)
            return false;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        int result = messageUserId != null ? messageUserId.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MessageUser{" +
                "messageUser=" + messageUserId.toString() +
                ", status=" + status +
                ", read=" + readDate +
                ", lastMessageUser=" + (lastMessageUser != null ? lastMessageUser.getId() : null) +
                '}';
    }
}
