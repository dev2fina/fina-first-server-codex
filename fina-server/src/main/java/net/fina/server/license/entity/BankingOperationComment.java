package net.fina.server.license.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity(name = "IN_BANKING_OPERATIONS_COMMENTS")
@Table(name = "IN_BANKING_OPERATIONS_COMMENTS")
@org.hibernate.envers.Audited
public class BankingOperationComment {

    @Id
    @SequenceGenerator(name = "banking_operation_sequence_comm", sequenceName = "banking_operation_sequence_comm", allocationSize = 1)
    @GeneratedValue(generator = "banking_operation_sequence_comm", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "\"COMMENT\"")
    private String comment;

    @Column(name = "MODIFIED_AT")
    private Date modifiedAt;

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
