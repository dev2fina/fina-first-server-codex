package net.fina.common.shared.dashboard;

import net.fina.common.client.fis.FiModel;

public class UserDataMetaModel {

    private String login;
    private String name;
    private FiModel fi;
    private int certificateValidity;
    private int passwordValidity;

    public UserDataMetaModel() {}

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FiModel getFi() {
        return fi;
    }

    public void setFi(FiModel fi) {
        this.fi = fi;
    }

    public int getCertificateValidity() {
        return certificateValidity;
    }

    public void setCertificateValidity(int certificateValidity) {
        this.certificateValidity = certificateValidity;
    }

    public int getPasswordValidity() {
        return passwordValidity;
    }

    public void setPasswordValidity(int passwordValidity) {
        this.passwordValidity = passwordValidity;
    }
}
