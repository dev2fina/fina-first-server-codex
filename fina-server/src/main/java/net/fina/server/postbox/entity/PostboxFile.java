package net.fina.server.postbox.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.security.entity.User;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity(name = "IN_POSTBOX_FILES")
@Table(name = "IN_POSTBOX_FILES")
public class PostboxFile implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "post_box_item_sequence", sequenceName = "post_box_item_sequence", allocationSize = 1)
    @GeneratedValue(generator = "post_box_item_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "upload_time")
    private Date uploadTime;

    @OneToOne
    @JoinColumn(name = "uploaded_by")
    private User publisher;

    @OneToMany
    @JoinTable(name = "IN_POSTBOX_FILE_USERS", joinColumns = {@JoinColumn(name = "file_id")}, inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Collection<User> users;

    @Column(name = "file_size")
    private long size;

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

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }
}
