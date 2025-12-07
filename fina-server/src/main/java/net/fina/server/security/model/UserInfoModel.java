package net.fina.server.security.model;

import java.io.Serializable;
import java.util.List;

public class UserInfoModel implements Serializable {
    private String login;
    private List<String> permissions;
    private List<String> fiCodes;

    public UserInfoModel() {
    }

    public UserInfoModel(String login, List<String> permissions, List<String> fiCodes) {
        this.login = login;
        this.permissions = permissions;
        this.fiCodes = fiCodes;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getFiCodes() {
        return fiCodes;
    }

    public void setFiCodes(List<String> fiCodes) {
        this.fiCodes = fiCodes;
    }
}
