package net.fina.server.fi.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "committees",
})
public class ManagementCommittees {
    protected List<ManagementCommittee> committees;

    public List<ManagementCommittee> getCommittees() {
        if (committees == null) {
            committees = new ArrayList<>();
        }
        return committees;
    }

    public void setCommittees(List<ManagementCommittee> committees) {
        this.committees = committees;
    }
}
