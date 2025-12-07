package net.fina.server.cems.entity;

import net.fina.server.cems.entity.sanction.CEMSSanctionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Objects;

@Embeddable
public class CEMSRecommendationStatusInfo {
    @Column(name = "NOTE")
    private String note;

    @Column(name = "FI_ACTION")
    private String fiActions;

    @JoinColumn(name = "STATUS")
    @OneToOne
    private CEMSSanctionStatus status;

    public CEMSRecommendationStatusInfo() {
    }

    public CEMSRecommendationStatusInfo(String note, String fiActions, CEMSSanctionStatus status) {
        this.note = note;
        this.fiActions = fiActions;
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFiActions() {
        return fiActions;
    }

    public void setFiActions(String fiActions) {
        this.fiActions = fiActions;
    }

    public CEMSSanctionStatus getStatus() {
        return status;
    }

    public void setStatus(CEMSSanctionStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CEMSRecommendationStatusInfo that = (CEMSRecommendationStatusInfo) o;
        return Objects.equals(getNote(), that.getNote()) && Objects.equals(getFiActions(), that.getFiActions()) && getStatus() == that.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNote(), getFiActions(), getStatus());
    }
}
