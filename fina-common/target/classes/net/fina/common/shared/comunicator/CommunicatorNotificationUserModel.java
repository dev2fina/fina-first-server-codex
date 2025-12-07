package net.fina.common.shared.comunicator;

import net.fina.common.client.constants.CommunicatorReadStatus;

import java.io.Serializable;

public class CommunicatorNotificationUserModel implements Serializable {

    private long id;
    private String login;
    private CommunicatorReadStatus status;

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

    public CommunicatorReadStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicatorReadStatus status) {
        this.status = status;
    }

}
