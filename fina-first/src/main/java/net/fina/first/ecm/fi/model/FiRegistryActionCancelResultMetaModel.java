package net.fina.first.ecm.fi.model;

import net.fina.first.ecm.registry.model.FiRegistryActionType;
import net.fina.first.ecm.version.model.VersionMetaModel;

public class FiRegistryActionCancelResultMetaModel {

    private String fiRegistryId;
    private VersionMetaModel fiRegistryRevertToVersion;
    private FiRegistryActionType actionType;

    public FiRegistryActionCancelResultMetaModel(String fiRegistryId) {
        this.fiRegistryId = fiRegistryId;
    }

    public String getFiRegistryId() {
        return fiRegistryId;
    }

    public void setFiRegistryId(String fiRegistryId) {
        this.fiRegistryId = fiRegistryId;
    }

    public VersionMetaModel getFiRegistryRevertToVersion() {
        return fiRegistryRevertToVersion;
    }

    public void setFiRegistryRevertToVersion(VersionMetaModel fiRegistryRevertToVersion) {
        this.fiRegistryRevertToVersion = fiRegistryRevertToVersion;
    }

    public FiRegistryActionType getActionType() {
        return actionType;
    }

    public void setActionType(FiRegistryActionType actionType) {
        this.actionType = actionType;
    }
}
