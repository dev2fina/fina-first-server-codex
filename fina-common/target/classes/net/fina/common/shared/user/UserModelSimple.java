package net.fina.common.shared.user;

import java.io.Serializable;

public class UserModelSimple implements Serializable {
    protected long id;
    protected String login;
    protected String description;
    protected long descriptionStrId;

    public UserModelSimple() {
    }

    public UserModelSimple(long id) {
        this.id = id;
    }

    public UserModelSimple(long id, String login, String description) {
        this.id = id;
        this.login = login;
        this.description = description;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDescriptionStrId() {
        return descriptionStrId;
    }

    public void setDescriptionStrId(long descriptionStrId) {
        this.descriptionStrId = descriptionStrId;
    }
}
