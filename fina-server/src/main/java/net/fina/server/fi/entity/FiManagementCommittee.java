package net.fina.server.fi.entity;

import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

//TODO fix envers FQN bug in NOT_AUDITED entities
@Entity(name = "net.fina.server.fi.entity.FiManagementCommittee")
@Table(name = "IN_BANK_MANAGEMENT_COMMITTEE")
public class FiManagementCommittee {
    @Id
    @SequenceGenerator(name = "fi_management_committee_sequence", sequenceName = "fi_management_committee_sequence", allocationSize = 1)
    @GeneratedValue(generator = "fi_management_committee_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description name;

    @Column(name = "POSITIONSTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description position;

    @Column(name = "ELECTION_DATE")
    @Temporal(TemporalType.DATE)
    private Date electionDate;

    @Column(name = "APPROVAL_DATE")
    @Temporal(TemporalType.DATE)
    private Date approvalDate;

    @Column(name = "\"COMMENT\"")
    private String comment;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Description getName() {
        return name;
    }

    public void setName(Description name) {
        this.name = name;
    }

    public Description getPosition() {
        return position;
    }

    public void setPosition(Description position) {
        this.position = position;
    }

    public Date getElectionDate() {
        return electionDate;
    }

    public void setElectionDate(Date electionDate) {
        this.electionDate = electionDate;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiManagementCommittee committee = (FiManagementCommittee) o;
        return getId() == committee.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
