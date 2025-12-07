package net.fina.server.license.entity;


import org.hibernate.envers.Audited;

import jakarta.persistence.*;
import java.util.Date;

@Entity(name = "IN_LICENSE_COMMENTS")
@Table(name = "IN_LICENSE_COMMENTS")
@Audited
public class LicenseComment {
    @Id
    @SequenceGenerator(name = "license_comment_sequence", sequenceName = "license_comment_sequence", allocationSize = 1)
    @GeneratedValue(generator = "license_comment_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "\"COMMENT\"")
    private String comment;

    @Column(name = "MODIFIED_AT")
    private Date modifiedAt;

    public LicenseComment() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
