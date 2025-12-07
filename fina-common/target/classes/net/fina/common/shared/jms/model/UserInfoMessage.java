package net.fina.common.shared.jms.model;

import java.io.Serializable;

public class UserInfoMessage implements  Serializable {
    private String userLogin;
    private boolean logoutUser;

    public UserInfoMessage() {
    }

    public UserInfoMessage(String userLogin, boolean logoutUser) {
        this.userLogin = userLogin;
        this.logoutUser = logoutUser;
    }


    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public boolean isLogoutUser() {
        return logoutUser;
    }

    public void setLogoutUser(boolean logoutUser) {
        this.logoutUser = logoutUser;
    }
}
