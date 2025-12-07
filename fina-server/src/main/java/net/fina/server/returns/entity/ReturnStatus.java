package net.fina.server.returns.entity;

import net.fina.common.client.returns.ProcessStatus;
import net.fina.server.i18n.helper.Description;
import net.fina.server.security.entity.User;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
@Entity(name = "IN_RETURN_STATUSES")
@Table(name = "IN_RETURN_STATUSES")
@IdClass(ReturnStatuseId.class)
public class ReturnStatus implements Serializable {

    @Id
    @Column(name = "ID")
    private long id;

    @Id
    @OneToOne
    @JoinColumn(name = "RETURNID")
    private Return returns;

    @OneToOne
    @JoinColumn(name = "VERSIONID")
    private ReturnVersion returnVersion;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private ProcessStatus status;

    @Column(name = "STATUSDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusDate;

    @OneToOne
    @JoinColumn(name = "USERID")
    private User user;

    @Column(name = "NOTE")
    private String note;

    public ReturnStatus() {
    }

    public ReturnStatus(long id, long returnVersionId, String returnVersionCode, Description returnVersionDescription, ProcessStatus status, Date statusDate, String login, String note) {
        this.id = id;

        ReturnVersion rv = new ReturnVersion();
        rv.setId(returnVersionId);
        rv.setCode(returnVersionCode);
        rv.setDescription(returnVersionDescription);

        this.returnVersion = rv;
        this.status = status;
        this.statusDate = statusDate;

        User user = new User();
        user.setLogin(login);

        this.user = user;

        setNoteWithoutEncoding(note);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Return getReturns() {
        return returns;
    }

    public void setReturns(Return returns) {
        this.returns = returns;
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

    public String getNote() {
        return note;
    }

    public void setNoteWithoutEncoding(String note) {
        this.note = note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ReturnVersion getReturnVersion() {
        return returnVersion;
    }

    public void setReturnVersion(ReturnVersion returnVersion) {
        this.returnVersion = returnVersion;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ReturnStatus{" +
                "id=" + id +
                ", returnVersion=" + returnVersion +
                ", status=" + status +
                ", statusDate=" + statusDate +
                ", user=" + user +
                ", returns=" + returns +
                '}';
    }
}
