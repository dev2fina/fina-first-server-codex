package net.fina.server.security.entity;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class UserPasswordId implements Serializable {

    private User user;
    private Date storedate;

    public UserPasswordId() {
    }

    public Date getStoredate() {
        return storedate;
    }

    public void setStoredate(Date storedate) {
        this.storedate = storedate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPasswordId that = (UserPasswordId) o;

        if (!storedate.equals(that.storedate)) return false;
        if (user.getId() != that.user.getId()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + storedate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserPasswordId{" +
                "storedate=" + storedate +
                '}';
    }
}
