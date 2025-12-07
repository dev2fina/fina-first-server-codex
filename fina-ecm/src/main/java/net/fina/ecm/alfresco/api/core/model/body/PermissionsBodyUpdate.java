package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PermissionElementRepresentation;

import java.util.List;

public class PermissionsBodyUpdate implements BaseRepresentation {
    private Boolean inheritanceEnabled;

    @JsonProperty(value = "locallySet")
    private List<PermissionElementRepresentation> locallySet;

    public PermissionsBodyUpdate() {
    }

    public PermissionsBodyUpdate(Boolean inheritanceEnabled, List<PermissionElementRepresentation> locallySet) {
        this.inheritanceEnabled = inheritanceEnabled;
        this.locallySet = locallySet;
    }

    @JsonProperty(value = "isInheritanceEnabled")
    public Boolean getInheritanceEnabled() {
        return inheritanceEnabled;
    }

    public void setInheritanceEnabled(Boolean inheritanceEnabled) {
        this.inheritanceEnabled = inheritanceEnabled;
    }

    public List<PermissionElementRepresentation> getLocallySet() {
        return locallySet;
    }

    public void setLocallySet(List<PermissionElementRepresentation> locallySet) {
        this.locallySet = locallySet;
    }
}
