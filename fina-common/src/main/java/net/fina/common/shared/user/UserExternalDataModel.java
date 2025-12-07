package net.fina.common.shared.user;

import java.io.Serializable;

public class UserExternalDataModel implements Serializable {
    private long id;
    private String login;
    private String contactPerson;
    private long contactPersonStrId;
    private String contactPersonPosition;
    private long contactPersonPositionStrId;
    private String phone;
    private String email;

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

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public long getContactPersonStrId() {
        return contactPersonStrId;
    }

    public void setContactPersonStrId(long contactPersonStrId) {
        this.contactPersonStrId = contactPersonStrId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactPersonPosition() {
        return contactPersonPosition;
    }

    public void setContactPersonPosition(String contactPersonPosition) {
        this.contactPersonPosition = contactPersonPosition;
    }

    public long getContactPersonPositionStrId() {
        return contactPersonPositionStrId;
    }

    public void setContactPersonPositionStrId(long contactPersonPositionStrId) {
        this.contactPersonPositionStrId = contactPersonPositionStrId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }

        UserExternalDataModel that = (UserExternalDataModel) other;

        return this.id == that.getId();
    }
}
