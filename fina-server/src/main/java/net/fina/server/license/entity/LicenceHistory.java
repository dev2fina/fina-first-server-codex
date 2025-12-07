package net.fina.server.license.entity;


import net.fina.auditlog.api.Audited;
import net.fina.common.client.fis.LicenceStatus;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "IN_LICENCES_HISTORY")
@Table(name = "IN_LICENCES_HISTORY")
public class LicenceHistory implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_licenses_history_sequence", sequenceName = "in_licenses_history_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_licenses_history_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "licence_id")
    private Licence licence;

    @Column(name = "LICENCE_STATUS")
    private LicenceStatus licenceStatus;

    @Column(name = "CHANGE")
    private String change;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CHANGE_DATE")
    private Date changeDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Licence getLicence() {
        return licence;
    }

    public void setLicence(Licence licence) {
        this.licence = licence;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public LicenceStatus getLicenceStatus() {
        return licenceStatus;
    }

    public void setLicenceStatus(LicenceStatus licenceStatus) {
        this.licenceStatus = licenceStatus;
    }
}
