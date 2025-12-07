package net.fina.first.ecm.dashboard.model;

public class FiRegistryStatusCountMetaModel {
    private String fiType;
    private int active;
    private int inactive;
    private int canceled;

    public FiRegistryStatusCountMetaModel() {
    }

    public FiRegistryStatusCountMetaModel(String fiType, int active, int inactive, int canceled) {
        this.fiType = fiType;
        this.active = active;
        this.inactive = inactive;
        this.canceled = canceled;
    }

    public String getFiType() {
        return fiType;
    }

    public void setFiType(String fiType) {
        this.fiType = fiType;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getInactive() {
        return inactive;
    }

    public void setInactive(int inactive) {
        this.inactive = inactive;
    }

    public int getCanceled() {
        return canceled;
    }

    public void setCanceled(int canceled) {
        this.canceled = canceled;
    }
}
