package net.fina.common.shared.postbox;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PostboxFileModel implements Serializable {

    protected long id;

    protected String name;

    protected Date uploadTime;

    protected UserModel publisher;

    protected List<UserModel> users;

    protected long size;

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

    public UserModel getPublisher() {
        return publisher;
    }

    public void setPublisher(UserModel publisher) {
        this.publisher = publisher;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PostboxFileModel other = (PostboxFileModel) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
