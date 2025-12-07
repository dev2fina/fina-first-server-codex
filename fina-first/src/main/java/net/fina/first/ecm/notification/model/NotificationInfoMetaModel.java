package net.fina.first.ecm.notification.model;

import net.fina.first.ecm.registry.model.FiRegistryActionType;
import net.fina.first.ecm.registry.model.FiRegistryMetaModel;

import java.util.Date;

public class NotificationInfoMetaModel {
    String id;
    private String addressee;
    private Date scheduledSendDate;
    private boolean isSent;
    private boolean isGapNotification;
    private FiRegistryMetaModel associatedFiRegistry;
    private FiRegistryActionType associatedActionType;
    private String associatedActionFinalStatus;
    private Date deadline;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddressee() {
        return addressee;
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public Date getScheduledSendDate() {
        return scheduledSendDate;
    }

    public void setScheduledSendDate(Date scheduledSendDate) {
        this.scheduledSendDate = scheduledSendDate;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public boolean isGapNotification() {
        return isGapNotification;
    }

    public void setGapNotification(boolean gapNotification) {
        isGapNotification = gapNotification;
    }

    public FiRegistryMetaModel getAssociatedFiRegistry() {
        return associatedFiRegistry;
    }

    public void setAssociatedFiRegistry(FiRegistryMetaModel associatedFiRegistry) {
        this.associatedFiRegistry = associatedFiRegistry;
    }

    public FiRegistryActionType getAssociatedActionType() {
        return associatedActionType;
    }

    public void setAssociatedActionType(FiRegistryActionType associatedActionType) {
        this.associatedActionType = associatedActionType;
    }

    public String getAssociatedActionFinalStatus() {
        return associatedActionFinalStatus;
    }

    public void setAssociatedActionFinalStatus(String associatedActionFinalStatus) {
        this.associatedActionFinalStatus = associatedActionFinalStatus;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}
