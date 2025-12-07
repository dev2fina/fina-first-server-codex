package net.fina.server.fi.xml;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "name",
        "position",
})
public class ManagementCommittee {
    @XmlElement
    protected Descriptions name;
    @XmlElement
    protected Descriptions position;
    @XmlAttribute
    protected String electionDate;
    @XmlAttribute
    protected String approvalDate;
    @XmlAttribute
    protected String comment;

    public ManagementCommittee() {
    }

    public ManagementCommittee(Descriptions name, Descriptions position, String electionDate, String approvalDate, String comment) {
        this.name = name;
        this.position = position;
        this.electionDate = electionDate;
        this.approvalDate = approvalDate;
        this.comment = comment;
    }

    public Descriptions getName() {
        return name;
    }

    public void setName(Descriptions name) {
        this.name = name;
    }

    public Descriptions getPosition() {
        return position;
    }

    public void setPosition(Descriptions position) {
        this.position = position;
    }

    public String getElectionDate() {
        return electionDate;
    }

    public void setElectionDate(String electionDate) {
        this.electionDate = electionDate;
    }

    public String getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(String approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
