package net.fina.server.returns.entity;

import net.fina.auditlog.api.Audited;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.common.server.util.CommonUtil;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name = "IN_RETURN_STATUSES")
@IdClass(ReturnStatusLiteId.class)
public class ReturnStatusLite implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "return_status_sequence", sequenceName = "return_status_sequence", allocationSize = 1)
    @GeneratedValue(generator = "return_status_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Id
    @Column(name = "RETURNID")
    private long returnId;

    @Column(name = "VERSIONID")
    private long versionId;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private ProcessStatus status;

    @Column(name = "STATUSDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusDate;

    @Column(name = "USERID")
    private long userId;

    @Column(name = "NOTE")
    private String note;

    public ReturnStatusLite() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = CommonUtil.left(note, 2000);
    }
}
