package net.fina.common.shared.postbox;

import java.io.Serializable;

public class UserModel implements Serializable {
    private long id;
    private String login;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
