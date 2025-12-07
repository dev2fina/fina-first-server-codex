package net.fina.server.fi.entity;

import jakarta.persistence.*;

@Entity(name = "SYS_USER_BANKS")
@Table(name = "SYS_USER_BANKS")
@IdClass(UserFiId.class)
public class UserFi {

    @Id
    private long userId;

    @Id
    private long bankId;

    @Column(name = "CANAMEND")
    private Boolean canAmend;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    public Boolean isCanAmend() {
        return canAmend;
    }

    public void setCanAmend(Boolean canAmend) {
        this.canAmend = canAmend;
    }

    @Override
    public String toString() {
        return "UserFi{" +
                "userId=" + userId +
                ", bankId=" + bankId +
                ", canAmend=" + canAmend +
                '}';
    }
}
