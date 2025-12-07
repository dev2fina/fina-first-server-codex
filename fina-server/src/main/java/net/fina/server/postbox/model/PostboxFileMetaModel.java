package net.fina.server.postbox.model;

import net.fina.server.security.model.UserMetaModel;

import java.util.Collection;
import java.util.Date;

public class PostboxFileMetaModel {
    private long id;
    private String name;
    private Date uploadTime;
    private byte[] content;
    private long size;
    private UserMetaModel publisher;
    private Collection<UserMetaModel> users;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public UserMetaModel getPublisher() {
        return publisher;
    }

    public void setPublisher(UserMetaModel publisher) {
        this.publisher = publisher;
    }

    public Collection<UserMetaModel> getUsers() {
        return users;
    }

    public void setUsers(Collection<UserMetaModel> users) {
        this.users = users;
    }
}
