package net.fina.server.cems.entity;

import net.fina.server.security.entity.User;

import jakarta.persistence.*;
import java.util.Date;

@Entity(name = "CEMS_REPORTING_YEAR_INSPECTIONS")
@Table(name = "CEMS_REPORTING_YEAR_INSPECTIONS")
public class CEMSReportingYearInspection {

    @Id
    @SequenceGenerator(name = "cems_repo_year_inspection", sequenceName = "cems_repo_year_inspection", allocationSize = 1)
    @GeneratedValue(generator = "cems_repo_year_inspection", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "START_DATE")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "END_DATE")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @OneToOne
    @JoinColumn(name = "MANAGER_ID")
    private User manager;

    public CEMSReportingYearInspection() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }
}
