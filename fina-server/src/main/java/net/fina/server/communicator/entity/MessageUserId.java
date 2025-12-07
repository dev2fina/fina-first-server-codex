package net.fina.server.communicator.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
public class MessageUserId implements Serializable {

    @Column(name = "MESSAGE_ID")
    private long message;

    @Column(name = "USER_ID")
    private long user;

    public MessageUserId() {
    }

    public MessageUserId(long message, long user) {
        this.message = message;
        this.user = user;
    }

    public long getMessage() {
        return message;
    }

    public void setMessage(long message) {
        this.message = message;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageUserId that = (MessageUserId) o;

        return user == that.user;
    }

    @Override
    public int hashCode() {
        int result = (int) message;
        result = 31 * result + (int)user;
        return result;
    }

    @Override
    public String toString() {
        return "MessageUserId{" +
                "message=" + message +
                ", user=" + user +
                '}';
    }
}
