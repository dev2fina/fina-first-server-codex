package net.fina.common.shared.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAndGroupMetaModel {
    private long id;
    private String code;
    private String description;
    private List<UserModel> users;
    private boolean group;
    private boolean blocked;
    private boolean disabled;


    public UserAndGroupMetaModel() {
    }

    public UserAndGroupMetaModel(long id, String code, String description, boolean group) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.group = group;
    }

    public UserAndGroupMetaModel(long id, String code, String description, boolean group, boolean blocked, boolean disabled) {
        this(id, code, description, group);
        this.disabled = disabled;
        this.blocked = blocked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
