package net.fina.server.license.entity;

import jakarta.persistence.*;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity(name = "IN_LICENSE_BANKING_OPERATIONS")
@Table(name = "IN_LICENSE_BANKING_OPERATIONS")
@org.hibernate.envers.Audited
public class LicenseBankingOperation {
    @Id
    @SequenceGenerator(name = "lic_banking_operation_sequence", sequenceName = "lic_banking_operation_sequence", allocationSize = 1)
    @GeneratedValue(generator = "lic_banking_operation_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATION_ID")
    @org.hibernate.envers.Audited(targetAuditMode = NOT_AUDITED)
    private BankingOperation bankingOperation;

    @Column(name = "ACTIVE")
    private boolean active;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CHANGE_DATE")
    private Date changeDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "LICENSE_OPERATION_ID")
    @AuditJoinTable(name = "IN_BANKING_OPERATION_COMMENTS_AUD", inverseJoinColumns = @JoinColumn(name = "COMMENT_ID"))
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.AUDITED)
    private List<BankingOperationComment> comments;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public List<BankingOperationComment> getComments() {
        return comments == null ? new ArrayList<>() : comments;
    }

    public void setComments(List<BankingOperationComment> comments) {
        this.comments = comments;
    }

    public BankingOperation getBankingOperation() {
        return bankingOperation;
    }

    public void setBankingOperation(BankingOperation bankingOperation) {
        this.bankingOperation = bankingOperation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LicenseBankingOperation that = (LicenseBankingOperation) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
