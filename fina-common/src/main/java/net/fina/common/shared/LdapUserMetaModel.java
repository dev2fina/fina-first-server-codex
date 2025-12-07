package net.fina.common.shared;

import java.io.Serializable;

public class LdapUserMetaModel implements Serializable {
    private long id;
    private String login;
    private String password;
    private String name;

    private Type type;

    private boolean passwordChanged;

    private boolean blocked;
    private String title;

    private String phone;

    private String email;


    public LdapUserMetaModel() {
    }


    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public enum Type {
        ROOT_USER,
        ROOT_ROLE,
        USER,
        ROLE,
        USER_ROLE,
        ROLE_USER,
    }

}
