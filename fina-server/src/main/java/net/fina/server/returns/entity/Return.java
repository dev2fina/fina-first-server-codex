package net.fina.server.returns.entity;

import net.fina.auditlog.api.Audited;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.server.fi.entity.Fi;
import net.fina.server.i18n.helper.Description;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Entity(name = "IN_RETURNS")
@Table(name = "IN_RETURNS")
public class Return implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "return_sequence", sequenceName = "return_sequence", allocationSize = 1)
    @GeneratedValue(generator = "return_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne
    @JoinColumn(name = "SCHEDULEID" ,referencedColumnName = "ID")
    private Schedule schedule;

    @Column(name = "VERSION")
    private long latestVersion;

    @OneToOne
    @JoinColumn(name = "VERSIONID", referencedColumnName = "ID")
    private ReturnVersion returnVersion;

    @OneToMany
    @JoinColumn(name = "RETURNID", referencedColumnName = "ID")
    private Collection<ReturnStatus> statuses;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private ProcessStatus status;

    @Transient
    private boolean create;

    public Return() {
    }

    public Return(long id, long versionId, Integer versionVersion) {
        this.id = id;
        ReturnVersion rv = new ReturnVersion();
        rv.setId(versionId);
        rv.setVersion(versionVersion);
        this.setReturnVersion(rv);
    }

    public Return(long id, long fiId, String fiCode, Description fiDescription, long periodId, Date fromDate, Date toDate, long periodTypeId, String periodTypeCode, long definitionId, String definitionCode, Description definitionDescription, long returnTypeId, String returnTypeCode, long statusId, ProcessStatus status, Date statusDate, long versionId, String versionCode) {
        setId(id);

        Schedule s = new Schedule();
        setSchedule(s);

        Fi fi = new Fi();
        fi.setId(fiId);
        fi.setCode(fiCode);
        fi.setDescription(fiDescription);

        s.setFi(fi);

        Period period = new Period();
        period.setId(periodId);
        period.setFromDate(fromDate);
        period.setToDate(toDate);
        s.setPeriod(period);

        PeriodType periodType = new PeriodType();
        periodType.setId(periodTypeId);
        periodType.setCode(periodTypeCode);
        period.setPeriodType(periodType);

        ReturnDefinition rd = new ReturnDefinition();
        rd.setId(definitionId);
        rd.setCode(definitionCode);
        rd.setDescription(definitionDescription);
        s.setReturnDefinition(rd);

        ReturnType rt = new ReturnType();
        rt.setId(returnTypeId);
        rt.setCode(returnTypeCode);
        rd.setReturnType(rt);

        ReturnStatus rs = new ReturnStatus();
        rs.setId(statusId);
        rs.setStatus(status);
        rs.setStatusDate(statusDate);
        setStatuses(Arrays.asList(rs));

        ReturnVersion rv = new ReturnVersion();
        rv.setId(versionId);
        rv.setCode(versionCode);
        setReturnVersion(rv);
    }

    public Return(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public ReturnVersion getReturnVersion() {
        return returnVersion;
    }

    public void setReturnVersion(ReturnVersion returnVersion) {
        this.returnVersion = returnVersion;
    }

    public Collection<ReturnStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(Collection<ReturnStatus> statuses) {
        this.statuses = statuses;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public long getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(long latestVersion) {
        this.latestVersion = latestVersion;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
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
        Return other = (Return) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Return{" +
                "id=" + id +
                ", schedule=" + schedule +
                ", returnVersion=" + returnVersion +
                ", create=" + create +
                '}';
    }
}
