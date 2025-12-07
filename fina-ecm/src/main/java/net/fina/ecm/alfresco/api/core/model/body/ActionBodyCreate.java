package net.fina.ecm.alfresco.api.core.model.body;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Map;

public class ActionBodyCreate implements BaseRepresentation {
    private String actionDefinitionId;
    private String targetId;
    private Map<String, Object> params;

    public ActionBodyCreate() {
    }

    public ActionBodyCreate(String actionDefinitionId, String targetId, Map<String, Object> params) {
        this.actionDefinitionId = actionDefinitionId;
        this.targetId = targetId;
        this.params = params;
    }

    public String getActionDefinitionId() {
        return actionDefinitionId;
    }

    public void setActionDefinitionId(String actionDefinitionId) {
        this.actionDefinitionId = actionDefinitionId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
