package net.fina.server.fi.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "IN_BANK_GROUP_HISTORY")
@Table(name = "IN_BANK_GROUP_HISTORY")
public class PeerGroupHistory implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_bank_group_history_sequence", sequenceName = "in_bank_group_history_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "in_bank_group_history_sequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "BANKID")
    private long fiId;

    @Column(name = "BANKGROUPID")
    private long fiGroupId;

    @Temporal(TemporalType.DATE)
    @Column(name = "CHANGEDATE")
    private Date change;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFiId() {
        return fiId;
    }

    public void setFiId(long fiId) {
        this.fiId = fiId;
    }

    public long getFiGroupId() {
        return fiGroupId;
    }

    public void setFiGroupId(long fiGroupId) {
        this.fiGroupId = fiGroupId;
    }

    public Date getChange() {
        return change;
    }

    public void setChange(Date change) {
        this.change = change;
    }
}
